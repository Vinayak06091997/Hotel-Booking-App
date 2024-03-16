package com.Hotel.Booking.App.Controller;

import com.Hotel.Booking.App.Exception.PhotoRetrievelException;
import com.Hotel.Booking.App.Model.BookedRoom;
import com.Hotel.Booking.App.Model.Room;
import com.Hotel.Booking.App.Response.BookingResponse;
import com.Hotel.Booking.App.Response.RoomResponse;
import com.Hotel.Booking.App.Service.BookingService;
import com.Hotel.Booking.App.Service.IRoomService;
import com.Hotel.Booking.App.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {


    private final IRoomService iRoomService;

    private final BookingService bookingService;
    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(
                    @RequestParam("photo") MultipartFile photo,
                    @RequestParam("roomType") String roomType,
                    @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {

        Room savedRoom =iRoomService.addNewRoom(photo,roomType,roomPrice);
        RoomResponse response=new RoomResponse(savedRoom.getId(),
                savedRoom.getRoomType(),savedRoom.getRoomPrice());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/room/types")
    public List<String> getRoomTypes(){

        return iRoomService.getAllRoomType();
    }


    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException, ResourceNotFoundException {
        List<Room> rooms=iRoomService.getAllRooms();
        List<RoomResponse> roomResponses=new ArrayList<>();
        for(Room room:rooms)
        {
            byte[] photoByte=iRoomService.getRoomPhotoByRoomId(room.getId());
            if(photoByte!=null&&photoByte.length>0)
            {
                String base64Photo = Base64.encodeBase64String((photoByte));
                RoomResponse roomResponse=getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);

            }
        }
        return  ResponseEntity.ok(roomResponses);
    }

    @PutMapping("/rooms/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false) String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice,
                                                   @RequestParam(required = false)  MultipartFile photo) throws SQLException, ResourceNotFoundException, IOException {

        byte[] photoByte= photo !=null&& !photo.isEmpty()?
                photo.getBytes(): iRoomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob=photoByte!=null&& photoByte.length>0?new SerialBlob(photoByte):null;
        Room theRoom=iRoomService.updateRoom(roomId,roomPrice,roomType,photoByte);
        theRoom.setPhoto(photoBlob);
        RoomResponse roomResponse=getRoomResponse(theRoom);

        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) throws ResourceNotFoundException {
        Optional<Room> theRoom=iRoomService.getRoomById(roomId);
        return theRoom.map((room)->
        {
            RoomResponse roomResponse=getRoomResponse(room);
            return ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(()-> new ResourceNotFoundException("Room not found"));
    }

    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId){
        iRoomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    private RoomResponse getRoomResponse(Room room) {
        List<BookedRoom> bookings=getAllBookingByRoomId(room.getId());
        List<BookingResponse> bookingsInfo= bookings.stream()
                .map(booking->new BookingResponse(booking.getBookingId()
                                                ,booking.getCheckInDate(),
                                                booking.getCheckOutDate()
                                                ,booking.getBookingConfirmationCode())).toList();
        byte[] photoBytes=null;
        Blob photoBlob=room.getPhoto();
        if(photoBlob!=null)
        {
            try{
                photoBytes=photoBlob.getBytes(1,(int) photoBlob.length());
            }catch (SQLException e){
                throw new PhotoRetrievelException("Error Retrieving Photo");
            }
        }
        return  new RoomResponse(room.getId(),room.getRoomType(),room.getRoomPrice(),room.isBooked( ),photoBytes,bookingsInfo);
    }

    private List<BookedRoom> getAllBookingByRoomId(Long roomId) {
        return bookingService.getAllBookingByRoomId(roomId  );
        
    }
}
