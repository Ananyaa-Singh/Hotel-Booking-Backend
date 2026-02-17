package com.example.HotelBooking;

//import com.example.HotelBooking.dtos.NotificationDTO;
//import com.example.HotelBooking.enums.NotificationType;
//import com.example.HotelBooking.services.NotificationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync //enable async processing
public class HotelBookingApplication{

	public static void main(String[] args) {
		SpringApplication.run(HotelBookingApplication.class, args);
	}

//	public class HotelBookingApplication implements CommandLineRunner {
//@Autowired
//private NotificationService notificationService;
//	@Override
//	public void run(String... args) throws Exception {
//		NotificationDTO notificationDTO = NotificationDTO.builder()
//				.type(NotificationType.EMAIL)
//				.recipient("ananyasinghpta12@gmail.com")
//				.body("I'm testing this from a command line")
//				.subject("Testing Email Sending")
//				.build();
//
//		notificationService.sendEmail(notificationDTO);
//	}
//	}
}
