package com.example.HotelBooking.dtos;

import com.example.HotelBooking.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //prevents null fields from being sent in API responses, making JSON cleaner and smaller
@JsonIgnoreProperties(ignoreUnknown = true) //allows the application to safely ignore unknown fields in requests, improving backward compatibility and preventing deserialization errors.
public class NotificationDTO {

    private Long id;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String body;

    private String bookingReference;

    private NotificationType type;

    private LocalDateTime createdAt;  // automatically stores creation time
}
