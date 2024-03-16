package com.Hotel.Booking.App.Response;

import com.Hotel.Booking.App.Model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String guestFullName;

    private String guestEmail;

    private int numOfAdults;

    private int numOfChildren;

    private int totalNumOfGuest;

    private String bookingConfirmationCode;

    private RoomResponse roomResponse;
    private Room room;

    public BookingResponse(Long id, LocalDate checkInDate,
                           LocalDate checkOutDate, String bookingConfirmationCode) {
        this.id = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfirmationCode = bookingConfirmationCode;
    }

    public BookingResponse(Long bookingId,
                           LocalDate checkInDate,
                           LocalDate checkOutDate,
                           String guestFullName,
                           String guestEmail,
                           int numOfAdults,
                           int numOfChildren,
                           String bookingConfirmationCode,
                           Room room) {
        this.id = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfirmationCode = bookingConfirmationCode;
        this.guestFullName = guestFullName;
        this.guestEmail = guestEmail;
        this.numOfAdults = numOfAdults;
        this.numOfChildren = numOfChildren;
        this.room = room;

    }
}
