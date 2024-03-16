package com.Hotel.Booking.App.Service;

import com.Hotel.Booking.App.Model.BookedRoom;

import java.util.List;

public interface IBookingService {
    List<BookedRoom> getAllBooking();

    BookedRoom findByBookingonfirmationCode(String confiramtionCode);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    void cancleBooking(Long bookingId);
}
