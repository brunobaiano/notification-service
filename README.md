# Notification Service

## Overview

The Notification Service is a Java-based application that sends notifications 
to users via a gateway.It includes rate limiting to prevent abuse and uses simple SLF4J for logging.

The rate limiting is implemented using Redis as a cache to store the number of requests made by each user,
and TTLs to expire the cache entries after a certain period.

## Technologies Used

- Java
- Maven
- SLF4J
- Docker
- Testcontainers
- Redis

## Prerequisites

- Java 21
- Maven 3.9.8
- Docker


## Building and Running the Application

### Using Docker Compose

To build the Docker image:

```sh
docker compose -f docker-compose.yml build
```

To run the application:

```sh
docker compose -f docker-compose.yml up
```

## Configuration

### Rate Limiting

Rate limiting rules are configured in the `rate-limits.json` file located in the resources directory.
note that the rate limits are defined in milliseconds.

### Notifications

Notifications are configured in the `notifications.csv` file located in the resources directory.
A notification has the following fields: 

Notification(String type, String userId, String message)

## Run the Application on IDE

Make sure you have a redis server running on localhost:6379

```sh
 docker run -p 6379:6379 -it redis:latest 
```
