# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W – Client-Server Architectures  
**Student:** M A Rushen Kavindu  
**Student ID:** w2153204  
**University:** University of Westminster  

---

## API Overview

A fully RESTful JAX-RS API built with Jersey 2.41 and an embedded Grizzly HTTP server. It manages campus **Rooms** and **Sensors** (CO2, temperature, occupancy, humidity, lighting controllers) and maintains a historical log of **SensorReadings**. All data is stored in-memory using `ConcurrentHashMap`.

### Architecture

```
com.smartcampus
├── Main.java                          ← Grizzly server entry point
├── SmartCampusApplication.java        ← @ApplicationPath("/api/v1")
├── model/         Room, Sensor, SensorReading
├── storage/       DataStore (thread-safe singleton)
├── resource/      DiscoveryResource, RoomResource, SensorResource,
│                  SensorReadingResource, HealthResource, StatsResource,
│                  SearchResource, ExportResource
├── exception/     Custom exceptions (5 types)
├── exception/mapper/ Exception mappers (6 mappers)
├── filter/        LoggingFilter, CorsFilter, RateLimitingFilter
└── response/      ApiResponse<T>, ErrorResponse
```

### Base URL
```
http://localhost:8080/api/v1
```

---

## How to Build and Run

### Prerequisites
- Java 11+
- Apache Maven 3.6+

### Build
```bash
mvn clean package
```

### Run
```bash
java -jar target/smart-campus-api-1.0.0.jar
```

The server starts at `http://localhost:8080`. Press **ENTER** to stop.

---

## Sample curl Commands

### 1. Discovery — GET /api/v1
```bash
curl -X GET http://localhost:8080/api/v1
```

### 2. List all rooms — GET /api/v1/rooms
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

### 3. Create a new room — POST /api/v1/rooms
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LAB-404","name":"AI Research Lab","capacity":25,"building":"Computer Science","floor":"4th Floor"}'
```

### 4. Get a specific room — GET /api/v1/rooms/{roomId}
```bash
curl -X GET http://localhost:8080/api/v1/rooms/LIB-301
```

### 5. Delete a room — DELETE /api/v1/rooms/{roomId}
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LAB-404
```

### 6. List all sensors — GET /api/v1/sensors
```bash
curl -X GET http://localhost:8080/api/v1/sensors
```

### 7. Filter sensors by type — GET /api/v1/sensors?type=CO2
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

### 8. Create a new sensor — POST /api/v1/sensors
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-002","type":"CO2","status":"ACTIVE","currentValue":390.0,"roomId":"LIB-301","unit":"ppm"}'
```

### 9. Get all readings for a sensor
```bash
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings
```

### 10. Add a new reading — POST /api/v1/sensors/{sensorId}/readings
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":23.7,"note":"Afternoon reading"}'
```

### 11. Trigger 409 — Delete room with sensors
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

### 12. Trigger 422 — Sensor with non-existent roomId
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"BAD-001","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"FAKE-999"}'
```

### 13. Trigger 403 — Post reading to MAINTENANCE sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-002/readings \
  -H "Content-Type: application/json" \
  -d '{"value":19.5}'
```

### 14. Health check
```bash
curl -X GET http://localhost:8080/api/v1/health
```

### 15. Statistics
```bash
curl -X GET http://localhost:8080/api/v1/stats
```

### 16. Cross-entity search
```bash
curl -X GET "http://localhost:8080/api/v1/search?q=library"
```

### 17. Export all data
```bash
curl -X GET http://localhost:8080/api/v1/export
```

### 18. Reading statistics for a sensor
```bash
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings/stats
```

---

## API Endpoints Reference

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/v1 | Discovery / HATEOAS links |
| GET | /api/v1/health | Health check |
| GET | /api/v1/stats | Aggregated statistics |
| GET | /api/v1/search?q= | Cross-entity search |
| GET | /api/v1/export | Full data export |
| GET | /api/v1/rooms | List all rooms |
| POST | /api/v1/rooms | Create room |
| GET | /api/v1/rooms/{id} | Get room |
| PUT | /api/v1/rooms/{id} | Update room |
| DELETE | /api/v1/rooms/{id} | Delete room (safe) |
| GET | /api/v1/sensors | List/filter sensors |
| POST | /api/v1/sensors | Register sensor |
| GET | /api/v1/sensors/{id} | Get sensor |
| PUT | /api/v1/sensors/{id} | Update sensor |
| DELETE | /api/v1/sensors/{id} | Delete sensor |
| GET | /api/v1/sensors/{id}/readings | Get reading history |
| POST | /api/v1/sensors/{id}/readings | Add reading |
| GET | /api/v1/sensors/{id}/readings/{rid} | Get single reading |
| DELETE | /api/v1/sensors/{id}/readings/{rid} | Delete reading |
| GET | /api/v1/sensors/{id}/readings/stats | Reading statistics |

---

## Conceptual Report

### Part 1 — Service Architecture & Setup

**Q1: Default lifecycle of a JAX-RS Resource class and impact on in-memory data management**

By default, JAX-RS creates a **new instance of every resource class for each incoming HTTP request** (per-request scope). This means instance variables inside a resource class are not shared between requests and cannot safely hold shared state. If in-memory data (such as a HashMap of rooms) were stored as an instance field on `RoomResource`, it would be re-created empty on every request — losing all previously added data.

To safely persist data across requests, this project uses a **singleton `DataStore` class** (`DataStore.getInstance()`). The singleton is instantiated once when the JVM starts and lives for the entire application lifecycle. All resource classes obtain the same shared instance. To prevent race conditions from concurrent requests modifying the same maps simultaneously, the DataStore uses `ConcurrentHashMap` — a thread-safe map that allows multiple threads to read concurrently and handles write contention internally without requiring explicit `synchronized` blocks on every method. For multi-step operations (e.g., creating a sensor AND updating the parent room's sensor list), the resource methods perform the steps in a consistent order to minimise the risk of partial updates.

**Q2: Why is HATEOAS considered a hallmark of advanced RESTful design?**

HATEOAS (Hypermedia As The Engine Of Application State) means that every API response includes hyperlinks to related resources and available actions. Rather than requiring clients to hardcode URLs, they can navigate the API dynamically by following the links provided. The discovery endpoint (`GET /api/v1`) returns a `_links` map with all primary resource URLs.

This benefits client developers in several ways: first, if the server's URL structure changes, clients that follow links rather than hardcoding paths require no code changes. Second, new developers can explore the entire API from a single entry point without reading separate documentation. Third, the API self-documents its capabilities — a client can inspect what operations are available in the current state rather than guessing from static docs.

---

### Part 2 — Room Management

**Q3: Implications of returning IDs vs full room objects in a list**

Returning only IDs requires the client to issue one additional GET request per room to retrieve its details — this is the classic **N+1 problem**. For a campus with 500 rooms, listing all rooms would require 501 HTTP round-trips, significantly increasing latency and server load. Returning full objects in one response is more bandwidth-intensive for the initial request but eliminates the N+1 pattern entirely. The optimal approach depends on use case: for a dashboard that displays a full room table, return full objects; for a dropdown picker that only needs names, a compact summary representation is sufficient. This API returns full objects by default as it best suits the facility management dashboard use case.

**Q4: Is the DELETE operation idempotent?**

Yes, DELETE is idempotent in this implementation. Idempotency means that making the same request multiple times produces the same final state. In this API, the first `DELETE /api/v1/rooms/LAB-404` removes the room and returns HTTP 200. Any subsequent identical request finds the room already gone and throws a `ResourceNotFoundException`, which the mapper converts to HTTP 404. The server state after both calls is identical: the room does not exist. The HTTP response code differs (200 vs 404), but idempotency is defined by **state outcome**, not response code — and the W3C HTTP specification confirms this interpretation.

---

### Part 3 — Sensor Operations & Linking

**Q5: Technical consequences of sending wrong Content-Type to a @Consumes endpoint**

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells the JAX-RS runtime that this method only accepts `application/json` payloads. If a client sends `Content-Type: text/plain`, the runtime inspects the header **before** invoking the method and returns HTTP **415 Unsupported Media Type** automatically — the resource method is never called. If the client sends `Content-Type: application/json` but the body is malformed JSON, Jackson's deserialiser throws a `JsonParseException` during unmarshalling, which the `GlobalExceptionMapper` intercepts and converts to HTTP 500 (or a custom 400 handler can be added). This two-layer protection ensures the resource method always receives a valid, typed Java object.

**Q6: @QueryParam vs path segment for filtering — why query params are superior**

Path segments (`/sensors/type/CO2`) imply a **hierarchical resource identity** — they suggest that `type/CO2` is a named resource, not a filter criterion. This creates problems: the path becomes rigid (only one filter at a time), it conflicts with actual resource IDs, and DELETE/PUT on `/sensors/type/CO2` would be semantically ambiguous. Query parameters (`?type=CO2&status=ACTIVE`) are designed specifically for filtering, sorting, and searching. They are composable (multiple params can be combined), optional (no param = no filter), and do not pollute the resource hierarchy. They also align with the REST principle that the path identifies a resource, while the query string refines the representation returned.

---

### Part 4 — Deep Nesting with Sub-Resources

**Q7: Architectural benefits of the Sub-Resource Locator pattern**

The Sub-Resource Locator pattern allows `SensorResource` to delegate all reading-related logic to a dedicated `SensorReadingResource` class by returning an instance from a `@Path("/{sensorId}/readings")` method (with no HTTP method annotation). This provides significant benefits over a monolithic controller: each class has a single, clear responsibility — `SensorResource` manages sensors, `SensorReadingResource` manages readings. The codebase remains readable and navigable even as the API grows. Each sub-resource class can be unit-tested independently by instantiating it directly with a mock `sensorId`. Adding further nesting (e.g., `/readings/stats`) requires changes only to `SensorReadingResource`, not to the parent class. In large APIs, this pattern prevents any single file from becoming an unmanageable "God class."

---

### Part 5 — Advanced Error Handling, Exception Mapping & Logging

**Q8: Why HTTP 422 is more semantically accurate than 404 for a missing reference**

HTTP 404 Not Found means the **requested URL path** does not exist on the server. HTTP 422 Unprocessable Entity means the server understood the request and the JSON is syntactically valid, but the **semantic content** of the payload is invalid. When a client POSTs a sensor with `roomId: "FAKE-999"`, the endpoint `/api/v1/sensors` absolutely exists (it's not a 404 scenario). The problem is that the value inside the valid JSON payload references a room that does not exist — a semantic validation failure. HTTP 422 precisely communicates: "Your request was well-formed, but I cannot process it because a referenced entity is missing." This distinction helps client developers immediately understand whether they have a URL problem (404) or a data problem (422).

**Q9: Cybersecurity risks of exposing Java stack traces**

A raw Java stack trace leaks multiple categories of sensitive information to potential attackers. It reveals the **full package and class hierarchy** of the application (e.g., `com.smartcampus.storage.DataStore`), which enables attackers to craft targeted exploits against known vulnerable library versions. It exposes **library names and versions** (e.g., Jersey 2.41, Jackson 2.15) allowing attackers to look up CVEs for those exact versions. It reveals **internal logic paths and variable names**, helping attackers understand the system's structure and identify injection points. It can expose **file system paths** on the server. The `GlobalExceptionMapper` in this project solves this by logging the full trace server-side (where only authorised developers can access it) while returning only a generic "An unexpected error occurred" message to the client.

**Q10: Why JAX-RS filters are superior to per-method logging**

JAX-RS filters implementing `ContainerRequestFilter` / `ContainerResponseFilter` enforce **cross-cutting concerns** at the framework level, making them architecturally superior to scattering `Logger.info()` calls across every resource method. A filter runs for every request automatically — even those that fail before reaching a resource method (e.g., requests rejected by authentication). With per-method logging, any new resource method added by a developer would silently miss logging unless they remember to add it. Filters also keep resource methods clean and focused on business logic (Single Responsibility Principle). In this project, `LoggingFilter` logs both request method/URI and response status code in one place, providing complete observability with zero coupling to resource code.

---

## Extra / Bonus Features

| Feature | Endpoint | Description |
|---------|----------|-------------|
| Health Check | GET /api/v1/health | Liveness probe with uptime/request metrics |
| Global Stats | GET /api/v1/stats | Aggregated sensor status counts |
| Cross-entity Search | GET /api/v1/search?q= | Search rooms and sensors by keyword |
| Data Export | GET /api/v1/export | Full JSON snapshot of all data |
| Reading Stats | GET /api/v1/sensors/{id}/readings/stats | Min/max/avg/sum per sensor |
| CORS Filter | — | Allows browser clients from any origin |
| Rate Limiting | — | 200 req/min per IP → HTTP 429 |
| PUT Room/Sensor | PUT /api/v1/rooms/{id} | Full update of a room |
| Sensor Filters | ?status=ACTIVE&roomId= | Multi-parameter sensor filtering |
| Room Filters | ?building=&minCapacity= | Room filtering by building/capacity |
| Pagination (readings) | ?limit=N | Return only last N readings |
