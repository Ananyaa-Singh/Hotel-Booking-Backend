package com.example.HotelBooking.services;

import com.example.HotelBooking.dtos.BookingDTO;
import com.example.HotelBooking.dtos.Response;

public interface BookingService {

    Response getAlBookings();

    Response createBooking(BookingDTO bookingDTO);

    Response findBookingByReferenceNo(String bookingReference);

    Response updateBooking(BookingDTO bookingDTO);
}
