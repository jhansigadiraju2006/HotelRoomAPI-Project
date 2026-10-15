# Hotel Room API (Java + Spring Boot)

## Project Overview

Hotel Room API is a RESTful backend application developed using Java and Spring Boot to manage hotel room information. It provides endpoints to create, retrieve, update, and delete rooms, along with pagination, filtering, searching, and sorting.

**Storage:** In-memory storage. All room data is cleared when the application restarts.

## Technologies Used

- Java 17 or newer
- Spring Boot
- Apache Maven
- RESTful Web Services
- Thunder Client or Postman for API testing

## Requirements

Before running the application, ensure you have installed:

- Java JDK 17 or newer
- Apache Maven 3.6 or newer
- Visual Studio Code or another Java IDE (optional)
- Thunder Client or Postman (optional)

Verify Java installation:

```bash
java -version
```

Verify Maven installation:

```bash
mvn -v
```

## Setup and Execution

### Step 1: Clone the Repository

Replace `YOUR-USERNAME` with your GitHub username.

```bash
git clone https://github.com/YOUR-USERNAME/HotelRoomAPI.git
```

### Step 2: Open the Project Folder

```bash
cd HotelRoomAPI
```

Open this folder in VS Code. Make sure the folder contains the `pom.xml` file and the `src` directory.

### Step 3: Run the Application

Open the VS Code terminal using **Terminal → New Terminal** and execute:

```bash
mvn spring-boot:run
```

Wait until Spring Boot reports that the application has started successfully.

If the project contains a Maven Wrapper, Windows users can alternatively run:

```powershell
.\mvnw.cmd spring-boot:run
```

### Step 4: Verify the API

Open the following URL in your browser:

http://localhost:8080/api/rooms

An empty JSON array (`[]`) is a normal response when no rooms have been created.

Keep the terminal running while testing the API. Press `Ctrl + C` to stop the application.

## API Endpoints

| HTTP Method | Endpoint | Description |
|---|---|---|
| POST | `/api/rooms` | Create a new room |
| GET | `/api/rooms` | Retrieve the list of rooms |
| GET | `/api/rooms/{id}` | Retrieve a room by ID |
| PUT | `/api/rooms/{id}` | Replace an existing room |
| PATCH | `/api/rooms/{id}` | Update selected room fields |
| DELETE | `/api/rooms/{id}` | Delete a room |

## Create a Room

Use Thunder Client in VS Code or Postman to test the API.

1. Start the application.
2. Create a new request.
3. Select the `POST` method.
4. Enter the URL:

   `http://localhost:8080/api/rooms`

5. Select **Body → JSON**.
6. Enter the following request body:

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

7. Click **Send**.

Expected status: `201 Created`, provided the request passes the application's validation rules.

## Pagination, Filtering, Searching, and Sorting

The following examples apply if these query parameters are supported by the implementation.

### Pagination

```http
GET /api/rooms?page=1&limit=10
```

### Filter by Room Type

```http
GET /api/rooms?type=Deluxe
```

### Filter by Availability

```http
GET /api/rooms?available=true
```

### Filter by Price Range

```http
GET /api/rooms?minPricePerNight=1000&maxPricePerNight=5000
```

### Search by Keyword

```http
GET /api/rooms?search=Chennai
```

### Sort by Price

```http
GET /api/rooms?sort=pricePerNight&order=desc
```

Supported room types: `Single`, `Double`, `Suite`, `Deluxe`, and `Family`.

Supported sorting fields: `name`, `pricePerNight`, and `capacity`.

Supported sorting orders: `asc` and `desc`.

## Important Notes

- The application uses in-memory storage and does not require a database.
- Room data is lost when the application stops or restarts.
- The default server port is `8080`.
- If port `8080` is occupied, configure another port using the method supported by the application's Spring Boot setup.
- Available endpoints, request fields, and validation rules depend on the actual source code.

## Future Enhancements

- Database integration for persistent room storage.
- Room booking and reservation management.
- Authentication and authorization.
- Enhanced validation and error handling.
- Automated unit and integration testing.

## Author

**Your Name**

## License

Add a suitable open-source license if you intend to distribute this project under specific reuse terms.
