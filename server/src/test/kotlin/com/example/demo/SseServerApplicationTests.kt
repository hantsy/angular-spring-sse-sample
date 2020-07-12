package com.example.demo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.Duration


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SseServerApplicationTests {

    @LocalServerPort
    var port: Int = 0

    lateinit var client: WebClient;

    @BeforeEach
    fun setup() {
        this.client = WebClient.builder()
                .clientConnector(ReactorClientHttpConnector())
                .codecs { it.defaultCodecs() }
                .exchangeStrategies(ExchangeStrategies.withDefaults())
                .baseUrl("http://localhost:" + port)
                .build()
    }

    @Test
    fun contextLoads() {
        val verifier = client.get().uri("messages")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Message::class.java)
                .log()
                .`as` { StepVerifier.create(it) }
                .consumeNextWith { it -> assertThat(it.body).isEqualTo("test message") }
                .consumeNextWith { it -> assertThat(it.body).isEqualTo("test message2") }
                .thenCancel()
                .verifyLater()
        client.post().uri("messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("test message")
                .exchange()
                .then()
                .block()
        client.post().uri("messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("test message2")
                .exchange()
                .then()
                .block();

        verifier.verify(Duration.ofSeconds(5))
    }
}

