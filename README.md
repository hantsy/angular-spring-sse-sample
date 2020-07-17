# angular-spring-sse-sample
This sample is to demonstrate a chat application using the following cutting-edge technology stack :

* Angular as client
* Spring WebFlux based Server Sent Event to broadcast messages to clients
* Spring Data MongoDB based  `@Tailable`  query result as an infinite stream

## Build 

Before running the application, you should build and run client and server side respectively.

### Server 

Run a MongoDB service firstly, simply you can run it from a Docker container. There is a `docker-compose.yaml` file is ready for you.

```bash
docker-compose up mongodb
```

Build the application.

```e
./gradlew build
```

Run the target jar from the *build* folder to start up the application.

```bash
java -jar build/xxx.jar
```

### Client

Install dependencies.

```bash
npm install
```

Start up the application.

```bash
npm run start
```

Open a browser and  navigate to http://localhost:4200.



