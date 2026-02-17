package com.example.HotelBooking.dtos;

import com.example.HotelBooking.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //prevents null fields from being sent in API responses, making JSON cleaner and smaller
public class Response {

    // generic
    private int status;
    private String message;

    // for login
    private String token;
    private UserRole role;
    private Boolean isActive;
    private String expirationTime;

    // User data output
    private UserDTO user;
    private List<UserDTO> users;

    // Booking data output
    private BookingDTO booking;
    private List<BookingDTO> bookings;

    // Room data output
    private RoomDTO room;
    private List<RoomDTO> rooms;

    // Payment data output
    private PaymentDTO payment;
    private List<PaymentDTO> payments;

    // Notification data output
    private NotificationDTO notification;
    private List<NotificationDTO> notifications;

    private final LocalDateTime timestamp = LocalDateTime.now();
}
