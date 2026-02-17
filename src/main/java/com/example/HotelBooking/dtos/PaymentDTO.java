package com.example.HotelBooking.dtos;

import com.example.HotelBooking.enums.PaymentGateway;
import com.example.HotelBooking.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //prevents null fields from being sent in API responses, making JSON cleaner and smaller
@JsonIgnoreProperties(ignoreUnknown = true) //allows the application to safely ignore unknown fields in requests, improving backward compatibility and preventing deserialization errors.
public class PaymentDTO {

    private Long id;
    private BookingDTO booking;

    private String transactionId;
    private BigDecimal amount;

    private PaymentGateway paymentMethod; //e.g. PayPal, Stripe, flutterwave, paystack

    private LocalDateTime paymentDate;

    private PaymentStatus status; //e.g. failed, success etc.

    private String bookingReference;
    private String failureReason;

    private String approvalLink; //e.g. paypal payment approval url
}
