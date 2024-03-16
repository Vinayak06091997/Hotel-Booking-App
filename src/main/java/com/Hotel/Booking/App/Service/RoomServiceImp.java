package com.Hotel.Booking.App.Service;

import com.Hotel.Booking.App.Exception.InternalServerError;
import com.Hotel.Booking.App.Exception.ResourceNotFoundException;
import com.Hotel.Booking.App.Model.Room;
import com.Hotel.Booking.App.Repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImp  implements IRoomService{

    private final RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException, SQLException {

        Room room =new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if (!file.isEmpty()) {
           byte[] photoBytes= file.getBytes();
           Blob photoBlob=new SerialBlob(photoBytes);
           room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomType() {
        return roomRepository.findDistinctTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws ResourceNotFoundException, SQLException {
        Optional<Room> theRoom=roomRepository.findById(roomId);
        if(theRoom.isEmpty())
        {
            throw new ResourceNotFoundException("Sorry Room not found");
        }
        Blob photoBlob=theRoom.get().getPhoto();
        if(photoBlob!=null)
        {
            return photoBlob.getBytes(1,(int) photoBlob.length());
        }
        return null;
    }

    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> theRoom=roomRepository.findById(roomId);
        if(theRoom.isPresent())
        {
            roomRepository.deleteById(roomId);
        }
    }

    @Override
    public Room updateRoom(Long roomId, BigDecimal roomPrice, String roomType, byte[] photoByte) throws ResourceNotFoundException {
        Room room=roomRepository.findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not Found"));
        if(roomType!=null) room.setRoomType(roomType);
        if(roomPrice!=null) room.setRoomPrice(roomPrice);
        if(photoByte!=null && photoByte.length>0){
            try{
                room.setPhoto(new SerialBlob(photoByte));
            }catch (SQLException ex){
                throw new InternalServerError("Error updating room");
            }

        }
        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }
}
