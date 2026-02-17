package com.example.HotelBooking.dtos;

import com.example.HotelBooking.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UserDTO {

    private Long id;

    private String email;

    //@JsonIgnore //ignore the password field during serialization (converting an object to JSON)
    private String password;
    private String firstName;
    private String lastName;

    private String phoneNumber;

    private UserRole role; //e.g. CUSTOMER, ADMIN

    private Boolean isActive;
    private LocalDateTime createdAt;
}
