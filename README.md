# Hotel Room API (Java + Spring Boot)

A beginner-friendly REST API for managing hotel rooms. It uses in-memory storage only; **data is cleared when the app restarts**.

## Requirements
- Java 17 or newer
- Maven 3.6+ (`mvn -v`)
- VS Code with Java extensions (optional)

## Run in VS Code
1. Extract `HotelRoomAPI.zip`.
2. Open the extracted `HotelRoomAPI` folder in VS Code.
3. Open Terminal → New Terminal.
4. Run:
   ```bash
   mvn spring-boot:run
   ```
5. Open `http://localhost:8080/api/rooms` in your browser. An empty response is normal.

If your existing project already has Maven Wrapper files (`mvnw.cmd`), you can run `.\mvnw.cmd spring-boot:run` on Windows instead.

## Endpoints

| Method | URL | Purpose |
|---|---|---|
| POST | `/api/rooms` | Create a room |
| GET | `/api/rooms` | List rooms with pagination, filters, search and sorting |
| GET | `/api/rooms/{id}` | Get one room |
| PUT | `/api/rooms/{id}` | Replace a room |
| PATCH | `/api/rooms/{id}` | Update selected fields |
| DELETE | `/api/rooms/{id}` | Delete a room |

### Create room example
Use Thunder Client in VS Code. Select `POST`, enter `http://localhost:8080/api/rooms`, choose Body → JSON, and send:

```json
{
  "name": "Deluxe Room",
  "location": "Chennai",
  "type": "Deluxe",
  "pricePerNight": 2500,
  "capacity": 2,
  "available": true,
  "roomNumber": "CHN-101"
}
```

Expected status: `201 Created`.

### List, filter, search, sort
- `GET /api/rooms?page=1&limit=10`
- `GET /api/rooms?type=Deluxe`
- `GET /api/rooms?available=true`
- `GET /api/rooms?minPricePerNight=1000&maxPricePerNight=5000`
- `GET /api/rooms?search=Chennai`
- `GET /api/rooms?sort=pricePerNight&order=desc`

Supported room types in this implementation: `Single`, `Double`, `Suite`, `Deluxe`, `Family`.
`sort` supports `name`, `pricePerNight`, and `capacity`. `order` supports `asc` and `desc`.

## Notes
- The API is in-memory as requested; it does not use a database or JSON file.
- If port 8080 is busy, stop the other app using that port or start with `PORT=8081` (shell syntax differs by operating system).
- Exact allowed room types/validation rules should be checked against your assessment instructions before submission.
