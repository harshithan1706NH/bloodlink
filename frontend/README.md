# BloodLink Desktop Frontend

This is the standalone Swing client extracted from the BloodLink Java project. It provides a dashboard, donor search and registration, and blood-request listing and creation. It communicates with the main BloodLink REST API; it does not contain the backend or database.

## Requirements

- Java 17 or newer
- Maven 3.9 or newer
- The main BloodLink Spring Boot API running at `http://localhost:8080/api`

## Run

From this `frontend` directory:

```bash
mvn compile exec:java
```

To use a different API address, set `BLOODLINK_API_URL` or pass a Java system property:

```bash
BLOODLINK_API_URL=http://localhost:8080/api mvn compile exec:java
```

```bash
mvn compile exec:java -Dexec.args="" -Dbloodlink.api=http://localhost:8080/api
```

Start the main backend and its database separately. The notification service in the repository is also separate and is not required by this Swing client.
