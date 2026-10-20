# BloodLink

This repository contains the standalone desktop frontend and the notification service:

- [`frontend/`](frontend/): Java Swing client for donors and blood requests. It calls the main BloodLink REST API.
- [`notification-service/`](notification-service/): independent Spring Boot notification API.

The main BloodLink API and PostgreSQL backend are maintained in the original Java project repository. Start that backend on port `8080` before using the Swing client. See each subfolder's README for run instructions.
