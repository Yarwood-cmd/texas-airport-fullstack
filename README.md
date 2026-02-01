# Texas Airport Reservation System

A full-stack mobile application for booking flights across Texas airports. Built with **Spring Boot** (backend) and **Android/Kotlin** (mobile app).

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=openjdk)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?style=flat-square&logo=kotlin)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?style=flat-square&logo=springboot)
![Android](https://img.shields.io/badge/Android-API%2024+-brightgreen?style=flat-square&logo=android)

## Features

### User Features
- **User Authentication** - Register, login with JWT-based security
- **Browse Flights** - View available Texas routes with real-time seat availability
- **Book Flights** - Reserve seats with passenger details and seat preferences
- **Manage Bookings** - View booking history, cancel reservations
- **Frequent Flyer Program** - Earn miles, unlock membership tiers (Silver/Gold/Platinum), receive discounts

### Technical Highlights
- RESTful API with Spring Boot
- JWT authentication & role-based authorization
- Android app with Retrofit for API integration
- MVVM architecture patterns
- Material Design 3 UI components
- Pull-to-refresh, loading states, error handling

## Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| Java 17 | Programming language |
| Spring Boot 3.2 | Application framework |
| Spring Security | Authentication & authorization |
| Spring Data JPA | Database access |
| H2 Database | In-memory database (dev) |
| JWT (jjwt) | Token-based authentication |
| Maven | Build tool |

### Android
| Technology | Purpose |
|------------|---------|
| Kotlin | Programming language |
| Retrofit 2 | HTTP client for API calls |
| Coroutines | Asynchronous programming |
| Material Design 3 | UI components |
| ViewBinding | View access |
| SharedPreferences | Local token storage |

## Project Structure

```
texas-airport-fullstack/
├── backend/                    # Spring Boot REST API
│   ├── src/main/java/
│   │   └── com/airport/
│   │       ├── config/         # Security, JWT, CORS config
│   │       ├── controller/     # REST endpoints
│   │       ├── model/          # JPA entities
│   │       ├── repository/     # Data access layer
│   │       └── service/        # Business logic
│   └── pom.xml
│
└── android/                    # Android mobile app
    ├── app/src/main/
    │   ├── java/com/airport/android/
    │   │   ├── api/            # Retrofit client & API interface
    │   │   ├── model/          # Data classes
    │   │   ├── adapter/        # RecyclerView adapters
    │   │   ├── ui/             # Activities
    │   │   └── util/           # Session management
    │   └── res/                # Layouts, drawables, values
    └── build.gradle
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.9+
- Android Studio (latest)
- Android SDK API 34

### Run the Backend

```bash
cd backend
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Run the Android App

1. Open the `android/` folder in Android Studio
2. Wait for Gradle sync to complete
3. Ensure the backend is running
4. Click **Run** (the app connects to `10.0.2.2:8080` from the emulator)

### Test Accounts

| Email | Password | Type |
|-------|----------|------|
| john@example.com | password123 | Regular Customer |
| jane@example.com | password123 | Gold Frequent Flyer (15% discount) |
| admin@texasairport.com | admin123 | Admin |

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT |

### Flights
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/flights` | Get all flights |
| GET | `/api/flights/available` | Get flights with seats |
| GET | `/api/flights/{id}` | Get flight by ID |
| GET | `/api/flights/search/destination/{dest}` | Search by destination |

### Bookings
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bookings` | Get user's bookings |
| POST | `/api/bookings` | Create booking |
| DELETE | `/api/bookings/{id}` | Cancel booking |

## Sample Routes

| Flight | Route | Departure | Price |
|--------|-------|-----------|-------|
| TX101 | Dallas → Austin | 08:00 AM | $149.99 |
| TX102 | Houston → San Antonio | 09:30 AM | $129.99 |
| TX103 | Austin → Dallas | 11:00 AM | $149.99 |
| TX104 | San Antonio → Houston | 01:00 PM | $129.99 |
| TX105 | Dallas → Houston | 02:30 PM | $179.99 |

## Screenshots

*Login → Browse Flights → Book → View Bookings*

## OOP Concepts Demonstrated

- **Encapsulation** - Private fields with getters/setters
- **Inheritance** - User types extend base functionality
- **Polymorphism** - `calculateDiscount()` varies by customer type
- **Abstraction** - Service layer separates business logic from controllers
- **Composition** - Booking contains User, Flight, Passenger

## Future Enhancements

- [ ] Flight search filters (date, price range)
- [ ] Push notifications for booking confirmations
- [ ] Seat map selection UI
- [ ] Payment integration
- [ ] Offline mode with local caching

## Author

**Paul Yarwood**
- GitHub: [@Yarwood-cmd](https://github.com/Yarwood-cmd)
- Texas A&M University-Commerce - B.S. Computer Information Systems (2026)

## License

This project is open source and available under the [MIT License](LICENSE).

