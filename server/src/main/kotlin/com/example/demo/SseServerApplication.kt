package com.example.demo

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Tailable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.time.Instant

@SpringBootApplication
class SseServerApplication {

    @Bean
    fun runner(template: ReactiveMongoTemplate) = CommandLineRunner {
        println("running CommandLineRunner...")
        template.insert(Message(body="test")).then().block()
        template.executeCommand("{\"convertToCapped\": \"messages\", size: 100000}")
                .subscribe(::println);
    }
}

fun main(args: Array<String>) {
    runApplication<SseServerApplication>(*args)
}

@RestController()
@RequestMapping(value = ["messages"])
@CrossOrigin(origins = ["http://localhost:4200"])
class MessageController(private val messages: MessageRepository) {

    @PostMapping
    fun hello(@RequestBody p: String) =
            this.messages.save(Message(body = p, sentAt = Instant.now())).log().then()

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun messageStream(): Flux<Message> = this.messages.getMessagesBy().log()
}

interface MessageRepository : ReactiveMongoRepository<Message, String> {
    @Tailable
    fun getMessagesBy(): Flux<Message>
}

@Document(collection = "messages")
data class Message(@Id var id: String? = null, var body: String, var sentAt: Instant = Instant.now())
