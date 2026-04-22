# Smart Campus Sensor & Room Management API

A fully RESTful API built with **JAX-RS (Jersey 2.41)** for managing campus rooms
and IoT sensors. Uses **in-memory storage only** (no database required).

---

## 📋 Requirements

| Tool        | Version  |
|-------------|----------|
| Java JDK    | 11+      |
| Apache Maven| 3.6+     |
| Tomcat      | 9.x *(optional — only needed for WAR deploy)* |

---

## 🚀 Quick Start

### Option A — Maven Tomcat Plugin (fastest, no Tomcat install needed)

```bash
cd smart-campus-api
mvn tomcat7:run
```

API is live at: **http://localhost:8080/api/v1**

---

### Option B — Build WAR + Deploy to Tomcat

```bash
mvn clean package
cp target/smart-campus-api.war /path/to/tomcat/webapps/
/path/to/tomcat/bin/startup.sh
```

API is live at: **http://localhost:8080/smart-campus-api/api/v1**

---

## 🔗 Endpoints

| Method   | URI                                  | Description                              | Status Codes          |
|----------|--------------------------------------|------------------------------------------|-----------------------|
| `GET`    | `/api/v1`                            | API discovery + HATEOAS links            | 200                   |
| `GET`    | `/api/v1/rooms`                      | List all rooms                           | 200                   |
| `POST`   | `/api/v1/rooms`                      | Create a new room                        | 201, 400              |
| `GET`    | `/api/v1/rooms/{id}`                 | Get a specific room                      | 200, 404              |
| `DELETE` | `/api/v1/rooms/{id}`                 | Delete a room                            | 204, 404, 409         |
| `GET`    | `/api/v1/sensors`                    | List all sensors                         | 200                   |
| `GET`    | `/api/v1/sensors?type=CO2`           | Filter sensors by type                   | 200                   |
| `POST`   | `/api/v1/sensors`                    | Create a sensor (roomId required)        | 201, 422              |
| `GET`    | `/api/v1/sensors/{id}/readings`      | List all readings for a sensor           | 200, 404              |
| `POST`   | `/api/v1/sensors/{id}/readings`      | Add a new reading                        | 201, 403, 404         |

---

## 🧱 Project Structure

```
smart-campus-api/
├── pom.xml
└── src/main/
    ├── java/com/smartcampus/
    │   ├── config/
    │   │   └── ApplicationConfig.java      ← JAX-RS app config + @ApplicationPath
    │   ├── model/
    │   │   ├── Room.java
    │   │   ├── Sensor.java
    │   │   └── SensorReading.java
    │   ├── store/
    │   │   └── DataStore.java              ← Singleton in-memory store + seed data
    │   ├── service/
    │   │   ├── RoomService.java            ← Room business logic
    │   │   └── SensorService.java          ← Sensor + reading business logic
    │   ├── resource/
    │   │   ├── DiscoveryResource.java      ← GET /api/v1
    │   │   ├── RoomResource.java           ← /rooms endpoints
    │   │   ├── SensorResource.java         ← /sensors endpoints + sub-resource locator
    │   │   └── ReadingSubResource.java     ← /sensors/{id}/readings endpoints
    │   ├── exception/
    │   │   ├── ResourceNotFoundException.java
    │   │   ├── RoomNotEmptyException.java
    │   │   ├── LinkedResourceNotFoundException.java
    │   │   └── SensorUnavailableException.java
    │   ├── mapper/
    │   │   ├── ResourceNotFoundExceptionMapper.java   → 404
    │   │   ├── RoomNotEmptyExceptionMapper.java       → 409
    │   │   ├── LinkedResourceNotFoundExceptionMapper  → 422
    │   │   ├── SensorUnavailableExceptionMapper.java  → 403
    │   │   └── GlobalExceptionMapper.java             → 500
    │   └── filter/
    │       └── LoggingFilter.java          ← Logs all requests + responses
    └── webapp/WEB-INF/
        └── web.xml
```

---

## 🧪 Seed Data

The API starts with pre-loaded data so you can test immediately:

| Resource | ID | Details                                      |
|----------|----|----------------------------------------------|
| Room     | 1  | Lab A101, Building A Floor 1, capacity 30    |
| Room     | 2  | Lecture Hall B201, Building B Floor 2, cap 200 |
| Room     | 3  | Server Room C001, Building C Basement, cap 5 |
| Sensor   | 1  | CO2 (ppm) in Room 1 — active                 |
| Sensor   | 2  | Temperature (Celsius) in Room 1 — active     |
| Sensor   | 3  | Humidity (%) in Room 2 — active              |
| Sensor   | 4  | CO2 (ppm) in Room 2 — **inactive** (use to test 403) |

---

## ⚠️ Troubleshooting

| Problem                     | Fix                                                         |
|-----------------------------|-------------------------------------------------------------|
| Port 8080 in use            | Change `<port>` in pom.xml tomcat plugin config             |
| 404 on all routes           | Ensure `@ApplicationPath("/api/v1")` is present            |
| 500 on POST requests        | Ensure `Content-Type: application/json` header is sent      |
| JSON not serialising        | Confirm `JacksonFeature` is registered in ApplicationConfig |
| Compilation errors          | Run `mvn clean package` to rebuild all classes              |
