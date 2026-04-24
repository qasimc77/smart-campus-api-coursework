# smart-campus-api-coursework
client server coursework by Qasim w2066316

This project presents the design and implementation of a RESTful API for a Smart Campus system using JAX-RS. The API enables the management of Rooms, Sensors, and Sensor Readings, supporting campus facilities and automated systems in monitoring environmental data.

The system follows REST architectural principles, uses in-memory data structures (HashMap and ArrayList), and provides a structured and scalable solution without the use of external databases, as required by the specification.

How to Run the Application
Open the project in NetBeans
Ensure GlassFish Server is configured
Run the application
Access the API via:

http://localhost:8080/smart-campus-api/api/v1

Example Endpoints

GET /api/v1/rooms
POST /api/v1/rooms
GET /api/v1/sensors
POST /api/v1/sensors
GET /api/v1/sensors/{sensorId}/readings
POST /api/v1/sensors/{sensorId}/readings

Part 1 – Service Architecture & Setup
Resource Lifecycle

In JAX-RS, resource classes are typically instantiated per request rather than as singletons. This ensures that each request is handled independently and avoids unintended shared state between requests.

To maintain shared application data, static in-memory collections (such as HashMap and ArrayList) are used. This allows data persistence across requests while still ensuring predictable behaviour.

Discovery Endpoint

The root endpoint (/api/v1) acts as a discovery service, returning metadata about the API including available resources and version information.

Hypermedia (HATEOAS) enhances RESTful design by allowing clients to dynamically navigate the API through links provided in responses. This reduces reliance on static documentation and improves flexibility.

Part 2 – Room Management
Room Retrieval

The API returns full room objects rather than only IDs. While returning IDs reduces network usage, full objects improve usability by reducing the need for additional requests from the client.

Room Deletion Logic

The DELETE operation is implemented as idempotent. Once a room is deleted, repeating the same request does not change the system state.

To prevent data inconsistency, rooms cannot be deleted if they contain active sensors. In such cases, the API returns a 409 Conflict response.

Part 3 – Sensor Operations
Data Validation

When creating a sensor, the API validates that the referenced room exists. If the room is not found, the API returns a 422 Unprocessable Entity response.

If a client submits data in an unsupported format (e.g. XML instead of JSON), JAX-RS will reject the request and return an appropriate error such as 415 Unsupported Media Type.

Filtering

Filtering is implemented using query parameters (e.g. /sensors?type=CO2). This approach is preferred because it allows flexible querying without altering the resource path structure, making it more scalable and aligned with REST conventions.

Part 4 – Sub-Resources
Sub-Resource Locator Pattern

The API uses a sub-resource locator for managing sensor readings:

/sensors/{sensorId}/readings

This design separates responsibilities into smaller classes, improving code organisation and maintainability compared to handling all routes in a single class.

Data Consistency

When a new sensor reading is added, the parent sensor’s currentValue is updated automatically. This ensures that the API always reflects the most recent state of the sensor.

Part 5 – Error Handling, Exception Mapping & Logging
Exception Handling

The API implements structured error handling using custom exception mappers:

409 Conflict – Attempt to delete a room with active sensors
422 Unprocessable Entity – Invalid room reference when creating a sensor
403 Forbidden – Sensor is in maintenance mode
500 Internal Server Error – Unexpected system errors

This approach ensures consistent and meaningful error responses.

Security Considerations

Returning raw stack traces can expose internal implementation details such as class names and system structure. This information could be exploited by attackers, so all errors are handled securely with controlled responses.

Logging

A logging filter is implemented to capture:

Incoming HTTP requests (method and URI)
Outgoing responses (status codes)

Using filters centralises logging logic, avoiding repetition and improving maintainability.

Design Decisions
JAX-RS was used as required by the coursework specification
No database was used; data is stored in memory using collections
REST principles were followed for consistency and clarity
JSON is used for all communication between client and server
Conclusion

The Smart Campus API demonstrates a fully functional RESTful system built using JAX-RS. It includes structured resource management, validation, error handling, and logging.

The implementation follows industry-standard practices and meets all coursework requirements, providing a scalable and maintainable backend solution.

Sample cURL Commands

curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms

curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms -H "Content-Type: application/json" -d "{"id":"LIB-301","name":"Library","capacity":100}"

curl -X GET http://localhost:8080/smart-campus-api/api/v1/sensors

curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors -H "Content-Type: application/json" -d "{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","roomId":"LIB-301"}"

curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d "{"value":23.5}"
