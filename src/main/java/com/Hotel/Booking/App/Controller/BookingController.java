package com.Hotel.Booking.App.Controller;


import com.Hotel.Booking.App.Exception.InvalidBookingRequestException;
import com.Hotel.Booking.App.Exception.ResourceNotFoundException;
import com.Hotel.Booking.App.Model.BookedRoom;
import com.Hotel.Booking.App.Model.Room;
import com.Hotel.Booking.App.Response.BookingResponse;
import com.Hotel.Booking.App.Response.RoomResponse;
import com.Hotel.Booking.App.Service.BookingService;
import com.Hotel.Booking.App.Service.IBookingService;
import com.Hotel.Booking.App.Service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private final IBookingService iBookingService;

    private final IRoomService RoomService;

    @GetMapping("/all-booking")
    public ResponseEntity<List<BookingResponse>> getAllBooking(){
        List<BookedRoom> booking=iBookingService.getAllBooking();
        List<BookingResponse> bookingResponses=new ArrayList<>();
        for(BookedRoom book:booking)
        {
            BookingResponse bookingResponse=getBookingResponse(book);
            bookingResponses.add(bookingResponse);

        }
        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping("/confirmation/{confirmationcode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confiramtionCode)
    {
        BookedRoom bookedRoom=iBookingService.findByBookingonfirmationCode(confiramtionCode);
        BookingResponse bookingResponse=getBookingResponse(bookedRoom);
        return ResponseEntity.ok(bookingResponse);
    }


    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookedRoom bookingRequest)
    {
            try{
                String confirmationCode= iBookingService.saveBooking(roomId,bookingRequest);
                return  ResponseEntity.ok(
                        "Room booked successfully, !  Your Confirmation Code is :"+confirmationCode);
            }catch (InvalidBookingRequestException ex){
                return  ResponseEntity.badRequest().body(ex.getMessage());

            }
    }


    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancleBooking(@PathVariable Long bookingId)
    {
        iBookingService.cancleBooking(bookingId);
    }

    private BookingResponse getBookingResponse(BookedRoom booking) {

        Room room =RoomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse roomResponse=new RoomResponse(room.getId(),room.getRoomType(),room.getRoomPrice());
        return new BookingResponse(booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),booking.getGuestEmail(),
                booking.getNumOfAdults(),booking.getNumOfChildren(),
                booking.getBookingConfirmationCode(),room);
    }

}
