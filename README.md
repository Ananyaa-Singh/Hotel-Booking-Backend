# Hotel Booking System - Backend

This is the **Spring Boot backend** for the Hotel Booking System. It handles user authentication, room & booking management, payments via Razorpay, and notifications.

---

## **Features**

- User authentication & authorization (Admin / Customer)
- Room management (CRUD)
- Booking management
- Payment integration (Razorpay)
- Email notifications for booking/payment status
- JWT-based security

---

## **Technologies Used**

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- Lombok
- Razorpay API

---

## **Setup Instructions**

1. Clone the repository:

```bash
git clone <backend-repo-url>
cd hotel-booking-backend
Configure MySQL in src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/hotel_booking
spring.datasource.username=root
spring.datasource.password=your_password
Add Razorpay API keys in application.properties:

razorpay.key=your_razorpay_key
razorpay.secret=your_razorpay_secret
Run the backend:

mvn spring-boot:run
Backend API base URL: http://localhost:8080/api

API Endpoints
/api/auth/register – Register new user
/api/auth/login – Login
/api/rooms/** – Room CRUD
/api/bookings/** – Booking CRUD
/api/payment/** – Payment operations

JWT token required for protected routes.
