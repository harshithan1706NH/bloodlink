BloodLink
BloodLink is a blood-donor coordination system built with a Java Swing desktop application and Spring Boot REST services. It allows users to register donors, search for available donors by blood group and city, create blood requests, track request fulfillment, and manage notification records.
Features
- Register blood donors with their blood group, city, contact number, and availability
- Search for available donors by blood group and city
- Update a donor's availability
- Create blood requests for a required blood group and city
- View open blood requests and mark requests as fulfilled
- Dashboard summary showing total donors, available donors, total requests, open requests, and fulfilled requests
- Separate REST service for creating and listing notification records
- Centralized API exception handling
- API execution-time logging using Spring AOP
- JPA entity inheritance using the JOINED strategy
Technology Stack
Area	Technologies
Desktop frontend	Java Swing
Main backend/API	Java 17, Spring Boot 3.4.2, Spring Web
Notification service	Java 17, Spring Boot 3.4.2, Spring Web, Bean Validation
Persistence	Spring Data JPA, Hibernate ORM
Database	PostgreSQL
API communication	HTTP/REST, JSON
Cross-cutting concerns	Spring AOP
Build and dependency management	Maven
Testing	JUnit and Spring Boot Test, H2 for tests


How the System Works
1. Desktop application: The Java Swing client provides the user interface and sends HTTP requests to the main REST API.
2. Donor registration: Donor information, including blood group, city, phone number, and availability, is submitted to the backend.
3. Donor search: The API can return donors and supports filtering available donors by blood group and city.
4. Blood request creation: A user creates a request with a required blood group and city. The backend saves the request in PostgreSQL.
5. Matching donors: The backend searches for available donors matching the request's blood group and city.
6. Notifications: The main backend sends notification requests to the separate notification service for matching donors. In the current implementation, the notification service stores notification records in memory; it does not itself integrate with an SMS or email provider.
7. Request fulfillment: A request can be marked as fulfilled, updating its status.
8. Dashboard summary: The dashboard endpoint returns aggregate counts for donors and blood requests.
9. Persistence and timing: Spring Data JPA and Hibernate handle database operations. Spring AOP records controller execution timing.
Database
BloodLink uses PostgreSQL for persistent donor and blood-request data. Hibernate/JPA maps Java entity classes to database tables and manages persistence.
Entity	Purpose
Person	Base entity containing shared person information.
Donor	Stores donor-specific information, including blood group, city, phone number, and availability.
BloodRequest	Stores blood requests, including blood group, city, and request status.
BloodGroup	Enum representing supported blood groups.
RequestStatus	Enum representing request states, including OPEN and FULFILLED.


The Person and Donor entities demonstrate JPA JOINED inheritance. The application configuration uses Hibernate's ddl-auto=update, which updates the database schema from entity mappings at startup. For production systems, use a managed migration tool and review schema changes instead of relying on automatic updates.

The notification service currently keeps its notification list in memory, so notification records are cleared when that service restarts. They are not stored in PostgreSQL in the current implementation.
API Endpoints
Main service — http://localhost:8080
Method	Endpoint	Purpose
GET	/api/donors	List donors; optionally filter using bloodGroup and city.
POST	/api/donors	Register a donor.
PATCH	/api/donors/{id}/availability?available=false	Update a donor's availability.
GET	/api/requests	List blood requests; optionally filter open requests by city.
POST	/api/requests	Create a blood request and notify matching donors through the notification service.
PATCH	/api/requests/{id}/fulfill	Mark a blood request as fulfilled.
GET	/api/dashboard/summary	Retrieve donor and request statistics.


Notification service — http://localhost:8081
Method	Endpoint	Purpose
GET	/api/notifications	List notification records held in memory.
POST	/api/notifications	Create a notification record.


Supported blood-group values include A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE, AB_NEGATIVE, O_POSITIVE, and O_NEGATIVE.
Prerequisites
- Java 17 or later
- Maven 3.9 or later
- PostgreSQL
- A desktop environment capable of running Java Swing
The main service uses PostgreSQL settings from environment variables DB_URL, DB_USER, and DB_PASSWORD, with local development defaults defined in src/main/resources/application.properties. The notification service listens on port 8081 by default, and the main API listens on port 8080.
Testing
The project includes tests for API timing and repository query behavior in the main service, and notification controller tests in the notification service. Run the Maven test command from the corresponding module directory to execute its tests.
