# Auth Service

Spring Boot 3.5 service that handles authentication for the Expense Tracker system. It issues short‑lived JWT access tokens, persists refresh tokens, and publishes user profile events to Kafka on signup.

## Stack
- Java 21, Spring Boot (Web, Security, OAuth2 resource server, Actuator)
- Spring Data JPA with MySQL
- JWT via `jjwt`
- Kafka producer for user events
- Gradle with Lombok

## Prerequisites
- JDK 21
- MySQL instance (defaults: host `mysql`, port `3306`, db `authservice`, user `root`, password `password`)
- Kafka broker reachable at the configured bootstrap server (defaults to `mysql:9092`) and topic `user_service`

## Configuration
Key properties live in `src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:mysql}:${MYSQL_PORT:3306}/${MYSQL_DB:authservice}
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create   # resets schema on startup; switch to update for persistence
server.port=9898

spring.kafka.topic-json.name=user_service
spring.kafka.producer.bootstrap-server=mysql:9092
spring.kafka.producer.value-serializer=com.example.authservice.serializer.UserInfoSerializer
```

The JWT signing secret is currently hard-coded in `JwtService.SECRET`; set a stronger secret via config for production use.

## Run Locally
```bash
# start the app
./gradlew bootRun

# or build a runnable jar
./gradlew bootJar
java -jar build/libs/authservice-0.0.1-SNAPSHOT.jar
```

## API
All endpoints are prefixed with `/auth/v1`.

- `POST /login` — authenticate and receive access + refresh tokens  
  Request body:
  ```json
  { "username": "demo", "password": "P@ssw0rd!" }
  ```

- `POST /signup` — create a user, emit a Kafka `UserInfoEvent`, and receive tokens  
  Request body (snake_case fields):
  ```json
  {
    "username": "demo",
    "password": "P@ssw0rd!",
    "first_name": "Demo",
    "last_name": "User",
    "phone_number": 1234567890,
    "email": "demo@example.com"
  }
  ```

- `POST /refreshToken` — exchange a refresh token for a new access token  
  Request body:
  ```json
  { "token": "<refresh-token>" }
  ```

Access tokens currently expire after 1 minute (`JwtService`); refresh tokens after 10 minutes (`RefreshTokenService`). Use the returned `accessToken` as `Authorization: Bearer <token>` for protected endpoints elsewhere in the system.

## Kafka Event
On successful signup the service publishes a `UserInfoEvent` to the `user_service` topic with:
```json
{
  "user_id": "<uuid>",
  "first_name": "...",
  "last_name": "...",
  "email": "...",
  "phone_number": 1234567890
}
```

## Testing
```bash
./gradlew test
```

