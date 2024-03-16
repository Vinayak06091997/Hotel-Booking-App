package com.Hotel.Booking.App.Service;

import com.Hotel.Booking.App.Exception.ResourceNotFoundException;
import com.Hotel.Booking.App.Model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IRoomService {
    Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws IOException, SQLException;

    List<String> getAllRoomType();


    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws ResourceNotFoundException, SQLException;


    void deleteRoom(Long roomId);

    Room updateRoom(Long roomId, BigDecimal roomPrice, String roomType, byte[] photoByte) throws ResourceNotFoundException;

    Optional<Room> getRoomById(Long roomId);
}
