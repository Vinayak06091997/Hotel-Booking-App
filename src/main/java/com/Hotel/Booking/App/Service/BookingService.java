package com.Hotel.Booking.App.Service;


import com.Hotel.Booking.App.Exception.InvalidBookingRequestException;
import com.Hotel.Booking.App.Model.BookedRoom;
import com.Hotel.Booking.App.Model.Room;
import com.Hotel.Booking.App.Repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepository repository;
    private final IRoomService roomService;

    public List<BookedRoom> getAllBookingByRoomId(Long roomId)
    {
        return repository.findByRoomId(roomId);
    }

    @Override
    public List<BookedRoom> getAllBooking() {
        return repository.findAll();
    }

    @Override
    public BookedRoom findByBookingonfirmationCode(String confiramtionCode) {
        return repository.findbyBookingConfirmationCode(confiramtionCode);
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate()))
        {
            throw new InvalidBookingRequestException("Check-In Date must come before check-out ");
        }
        Room room=roomService.getRoomById(roomId).get();
        List<BookedRoom> existingbookings=room.getBookings();
        boolean roomIsAvailable=roomIsAvailable(bookingRequest,existingbookings);
        if(roomIsAvailable){
            room.addBooking(bookingRequest);
            repository.save(bookingRequest);
        }else{
            throw new InvalidBookingRequestException("This room has been booked for the selected dates");

        }
        return bookingRequest.getBookingConfirmationCode();
    }


    @Override
    public void cancleBooking(Long bookingId) {
        repository.deleteById(bookingId);

    }

    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingbooking) {
        return existingbooking.stream()
                .noneMatch(existingbookings->
                        bookingRequest.getCheckInDate().equals(existingbookings.getCheckInDate())

                         || bookingRequest.getCheckOutDate().isBefore(existingbookings.getCheckOutDate())
                         || bookingRequest.getCheckInDate().isAfter(existingbookings.getCheckInDate())
                         && bookingRequest.getCheckInDate().isBefore(existingbookings.getCheckOutDate())
                        || bookingRequest.getCheckInDate().isAfter(existingbookings.getCheckInDate())

                        && bookingRequest.getCheckOutDate().equals(existingbookings.getCheckOutDate())
                        || (bookingRequest.getCheckInDate().isBefore(existingbookings.getCheckInDate()))

                        && bookingRequest.getCheckOutDate().isAfter(existingbookings.getCheckOutDate())

                        || (bookingRequest.getCheckInDate().equals(existingbookings.getCheckOutDate())
                        && bookingRequest.getCheckOutDate().equals(existingbookings.getCheckInDate()))

                        || (bookingRequest.getCheckInDate().equals(existingbookings.getCheckOutDate())
                        && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

}
