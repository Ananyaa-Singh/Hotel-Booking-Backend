package com.example.HotelBooking.services.impl;

import com.example.HotelBooking.dtos.Response;
import com.example.HotelBooking.dtos.RoomDTO;
import com.example.HotelBooking.entities.Room;
import com.example.HotelBooking.enums.RoomType;
import com.example.HotelBooking.exceptions.InvalidBookingStateAndDateException;
import com.example.HotelBooking.exceptions.NotFoundException;
import com.example.HotelBooking.repositories.RoomRepository;
import com.example.HotelBooking.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

//    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/product-image/";

//    image directory for our frontend app
private static final String IMAGE_DIRECTORY_FRONTEND = "C:/Users/HP/Downloads/hotel-frontend/hotel-frontend/public/rooms/";



    @Override
    public Response addRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        Room roomToSave = modelMapper.map(roomDTO, Room.class);

        if(imageFile != null) {
            String imagePath = saveImageToFrontEnd(imageFile);
            roomToSave.setImageUrl(imagePath);
        }

        roomRepository.save(roomToSave);

        return Response.builder()
                .status(200)
                .message("Room successfully added")
                .build();
    }

    @Override
    public Response updateRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        Room existingRoom = roomRepository.findById(roomDTO.getId())
                .orElseThrow(() -> new NotFoundException("Room not found"));

        if(imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImageToFrontEnd(imageFile);
            existingRoom.setImageUrl(imagePath);
        }

        if(roomDTO.getRoomNumber() != null && roomDTO.getRoomNumber() >= 0) {
            existingRoom.setRoomNumber(roomDTO.getRoomNumber());
        }

        if(roomDTO.getPricePerNight() != null && roomDTO.getPricePerNight().compareTo(BigDecimal.ZERO) >= 0) {
            existingRoom.setPricePerNight(roomDTO.getPricePerNight());
        }

        if(roomDTO.getCapacity() != null && roomDTO.getCapacity() > 0) {
            existingRoom.setCapacity(roomDTO.getCapacity());
        }

        if(roomDTO.getType() != null) {
            existingRoom.setType(roomDTO.getType());
        }

        if(roomDTO.getDescription() != null) {
            existingRoom.setDescription(roomDTO.getDescription());
        }
        roomRepository.save(existingRoom);

        return Response.builder()
                .status(200)
                .message("Room successfully updated")
                .build();
    }

    @Override
    public Response getAllRooms() {
        List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<RoomDTO> roomDTOList = modelMapper.map(roomList, new TypeToken<List<RoomDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .rooms(roomDTOList)
                .message("Success")
                .build();
    }

    @Override
    public Response getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found"));

        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        return Response.builder()
                .status(200)
                .message("Success")
                .room(roomDTO)
                .build();
    }

    @Override
    public Response deleteRoomById(Long id) {
        if(!roomRepository.existsById(id)) {
            throw new NotFoundException("Room not found");
        }
        roomRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Room successfully deleted")
                .build();
    }

    @Override
    public Response getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {
        //validation: Ensure that check-in date is not before today
        if(checkInDate.isBefore(LocalDate.now())) {
            throw new InvalidBookingStateAndDateException("Check-in date can not be before today");
        }

        //validation: Ensure that check-out date is not before check-in date
        if(checkOutDate.isBefore(checkInDate)){
            throw new InvalidBookingStateAndDateException("Check-out date can not be before Check-in date");
        }

        //validation: Ensure that check-in date is not same as check out today
        if(checkInDate.isEqual(checkOutDate)) {
            throw new InvalidBookingStateAndDateException("Check-in date can not be equal to Check-out date");
        }

        List<Room> roomList = roomRepository.findAvailableRooms(checkInDate, checkOutDate, roomType);

        List<RoomDTO> roomDTOList = modelMapper.map(roomList, new TypeToken<List<RoomDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .rooms(roomDTOList)
                .message("Success")
                .build();
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return Arrays.asList(RoomType.values());
    }

    @Override
    public Response searchRoom(String input) {
        List<Room> roomList = roomRepository.searchRooms(input);

        List<RoomDTO> roomDTOList = modelMapper.map(roomList, new TypeToken<List<RoomDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .rooms(roomDTOList)
                .message("Success")
                .build();
    }

    /*
//    save image to backend folder
    private String saveImage(MultipartFile imageFile)
    {
        // Validate file
        if (imageFile == null || imageFile.isEmpty()
                || imageFile.getContentType() == null
                || !imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only images are supported");
        }

        // create directory to store image if it does not exist
        File directory = new File(IMAGE_DIRECTORY);

        if(!directory.exists())
        {
            directory.mkdir();
        }

        //generate unique file names for the image
        String uniqueFileName = UUID.randomUUID() + " " + imageFile.getOriginalFilename();

        //get the absolute path of the image
        String imagePath = IMAGE_DIRECTORY + uniqueFileName;

        try{
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile);
        }catch(Exception ex)
        {
            throw new IllegalArgumentException(ex.getMessage());
        }
        return imagePath;
    }
     */

    //    save image to frontend folder
    private String saveImageToFrontEnd(MultipartFile imageFile) {

        if (imageFile == null || imageFile.isEmpty()
                || imageFile.getContentType() == null
                || !imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only images are supported");
        }

        File directory = new File(IMAGE_DIRECTORY_FRONTEND);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String uniqueFileName =
                UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        String imagePath =
                IMAGE_DIRECTORY_FRONTEND + uniqueFileName;

        try {
            imageFile.transferTo(new File(imagePath));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return "/rooms/" + uniqueFileName;
    }
}
