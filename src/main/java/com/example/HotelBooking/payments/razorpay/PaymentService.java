package com.example.HotelBooking.payments.razorpay;

import com.example.HotelBooking.dtos.NotificationDTO;
import com.example.HotelBooking.entities.Booking;
import com.example.HotelBooking.entities.PaymentEntity;
import com.example.HotelBooking.enums.NotificationType;
import com.example.HotelBooking.enums.PaymentGateway;
import com.example.HotelBooking.enums.PaymentStatus;
import com.example.HotelBooking.exceptions.NotFoundException;
import com.example.HotelBooking.payments.razorpay.dto.PaymentRequest;
import com.example.HotelBooking.repositories.BookingRepository;
import com.example.HotelBooking.repositories.PaymentRepository;
import com.example.HotelBooking.services.NotificationService;
import com.example.HotelBooking.services.UserService;
import org.springframework.transaction.annotation.Transactional;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    /*
     --------------------------------------------------
     CREATE RAZORPAY ORDER
     --------------------------------------------------
     */
    public String createPaymentIntent(PaymentRequest paymentRequest) {

        log.info("Inside createPaymentIntent()");
        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Payment already done");
        }

        try {
            RazorpayClient razorpay = new RazorpayClient(key, secret);

            JSONObject options = new JSONObject();
            options.put("amount",
                    paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // paise
            options.put("currency", "INR");
            options.put("receipt", bookingReference);

            Order order = razorpay.orders.create(options);

            return order.get("id").toString();

        } catch (Exception e) {
            throw new RuntimeException("Error creating Razorpay order");
        }
    }


    /*
     --------------------------------------------------
     UPDATE BOOKING AFTER SUCCESSFUL PAYMENT
     --------------------------------------------------
     */
    @Transactional
    public void updatePaymentBooking(PaymentRequest paymentRequest) {

        log.info("Inside updatePaymentBooking()");
        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Save payment record
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentGateway(PaymentGateway.RAZORPAY);
        payment.setPaymentStatus(
                Boolean.TRUE.equals(paymentRequest.getSuccess())
                        ? PaymentStatus.COMPLETED
                        : PaymentStatus.FAILED
        );
        payment.setTransactionId(paymentRequest.getTransactionId()); // razorpay_payment_id
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBookingReference(bookingReference);
        payment.setUser(booking.getUser());

        if (!Boolean.TRUE.equals(paymentRequest.getSuccess())) {
            payment.setFailureReason(paymentRequest.getFailureReason());
        }

        paymentRepository.save(payment); //save payment to database

        //create and send notification
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(booking.getUser().getEmail())
                .type(NotificationType.EMAIL)
                .bookingReference(bookingReference)
                .build();

        log.info("About to send notification inside updatePaymentBooking by Email");

        if (Boolean.TRUE.equals(paymentRequest.getSuccess()))
        {
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingRepository.save(booking); // Update booking status

            notificationDTO.setSubject("Booking Payment Successful");
            notificationDTO.setBody("Congratulations! Your payment for booking with reference " + bookingReference + " has been successfully completed");
            notificationService.sendEmail(notificationDTO); //send email
        }
        else
        {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking); // Update booking status

            notificationDTO.setSubject("Booking Payment Failed");
            notificationDTO.setBody("Your payment for booking with reference " + bookingReference + " has been failed for reason " + paymentRequest.getFailureReason());
            notificationService.sendEmail(notificationDTO); //send email
        }


        log.info("Payment processed for booking: {}", bookingReference);
    }
}