Distributed Job Scheduler

A distributed, fault-tolerant job scheduling system built with Spring Boot and PostgreSQL, designed to handle background jobs with retries, locking, recovery, and extensible job handlers.

🚀 Features

Create and schedule background jobs

Distributed job claiming with DB-level locking

Retry mechanism with max retry limits

Job failure handling & recovery

Job handlers by type (Strategy Pattern)

Metrics for job success & failure

Pagination and filtering APIs

Stuck job detection and recovery

Unit-tested core scheduling logic

Dockerized setup

🛠 Tech Stack

Backend: Java 21, Spring Boot 3

Database: PostgreSQL (JSONB, enums, row locking)

ORM: Spring Data JPA / Hibernate

Validation: Jakarta Validation

Testing: JUnit 5, Mockito

Containerization: Docker & Docker Compose
