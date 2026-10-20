# BloodLink

A small end-to-end blood-donor coordination system with a Java Swing desktop frontend, two Spring Boot REST services, Hibernate/JPA persistence, and PostgreSQL.

## What it does

- Register donors with blood group, city, phone, and availability.
- Search available donors by blood group and city.
- Create blood requests and mark a request fulfilled.
- Send and list notifications through a separate service.
- Keeps donor and request data in PostgreSQL through Hibernate.

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

2. In one terminal, start the main backend on port 8080:

   ```bash
   mvn spring-boot:run
   ```

3. In another terminal, launch the Swing desktop app:

   ```bash
   mvn exec:java -Dexec.mainClass=com.bloodlink.client.BloodLinkClient
   ```

4. Start the notification service on port 8081 from a third terminal:

   ```bash
   cd notification-service
   mvn spring-boot:run
   ```

The main API is available at `http://localhost:8080/api`, while notifications are served at `http://localhost:8081/api/notifications`. The defaults are in `src/main/resources/application.properties`; override them using `DB_URL`, `DB_USER`, and `DB_PASSWORD` if using your own PostgreSQL server.

## API summary

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/api/donors?bloodGroup=O_POSITIVE&city=Pune` | Find available matching donors using explicit HQL |
| POST | `/api/donors` | Register a donor |
| PATCH | `/api/donors/{id}/availability?available=false` | Set donor availability |
| GET | `/api/requests?city=Pune` | Find open requests by city using explicit HQL |
| POST | `/api/requests` | Create a blood request |
| PATCH | `/api/requests/{id}/fulfill` | Mark a request fulfilled |
| GET / POST | `http://localhost:8081/api/notifications` | List / create notifications |

Blood-group values use `A_POSITIVE`, `A_NEGATIVE`, `B_POSITIVE`, `B_NEGATIVE`, `AB_POSITIVE`, `AB_NEGATIVE`, `O_POSITIVE`, and `O_NEGATIVE`.

The main service logs controller execution time with `ApiTimingAspect`. Its `Person` and `Donor` entities demonstrate JPA `JOINED` inheritance.

A notification request has this shape:

```json
{
  "recipient": "+49-555-0100",
  "message": "An O-positive donor is available."
}
```
