# BloodLink

A small end-to-end blood-donor coordination system. It has a Java Swing desktop frontend, a Spring Boot REST service, Hibernate/JPA persistence, and PostgreSQL.

## What it does

- Register donors with blood group, city, phone, and availability.
- Search available donors by blood group and city.
- Create blood requests and mark a request fulfilled.
- Keeps data in PostgreSQL through Hibernate.

## Run it

Prerequisites: Java 17+, Maven 3.9+, and a running PostgreSQL server.

1. Create the database and user once in PostgreSQL:

   ```bash
   psql postgres
   ```

   ```sql
   CREATE USER bloodlink WITH PASSWORD 'bloodlink';
   CREATE DATABASE bloodlink OWNER bloodlink;
   ```

2. In one terminal, start the backend:

   ```bash
   mvn spring-boot:run
   ```

3. In another terminal, launch the Swing desktop app:

   ```bash
   mvn exec:java -Dexec.mainClass=com.bloodlink.client.BloodLinkClient
   ```

The API is available at `http://localhost:8080/api`. The defaults are in `src/main/resources/application.properties`; override them using `DB_URL`, `DB_USER`, and `DB_PASSWORD` if using your own PostgreSQL server.

## API summary

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/api/donors?bloodGroup=O_POSITIVE&city=Pune` | Find available matching donors |
| POST | `/api/donors` | Register a donor |
| PATCH | `/api/donors/{id}/availability?available=false` | Set donor availability |
| GET / POST | `/api/requests` | List / create blood requests |
| PATCH | `/api/requests/{id}/fulfill` | Mark a request fulfilled |

Blood-group values use `A_POSITIVE`, `A_NEGATIVE`, `B_POSITIVE`, `B_NEGATIVE`, `AB_POSITIVE`, `AB_NEGATIVE`, `O_POSITIVE`, and `O_NEGATIVE`.
