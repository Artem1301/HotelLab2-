package com.artem.HotelBookingApplication.service;

import com.artem.HotelBookingApplication.model.BookedRoom;
import com.artem.HotelBookingApplication.model.Room;
import com.artem.HotelBookingApplication.model.User;
import com.artem.HotelBookingApplication.repository.BookingRepository;
import com.artem.HotelBookingApplication.repository.RoomRepository;
import com.artem.HotelBookingApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    private BookingDTO bookingDTO;
    private User user;
    private Room room;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setRoomId(1L);
        bookingDTO.setCheckInDate(LocalDate.of(2025, 6, 10));
        bookingDTO.setCheckOutDate(LocalDate.of(2025, 6, 15));

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
    }

    @Test
    void testCreateBooking_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookingService.createBooking(bookingDTO);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(bookingDTO));
        assertEquals("Користувача не знайдено", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_MissingCheckInDate() {
        bookingDTO.setCheckInDate(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(bookingDTO));
        assertEquals("Дати заїзду та виїзду обов’язкові", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testGetBookingsByUserId_Success() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.of(2025, 6, 10));
        booking.setCheckOutDate(LocalDate.of(2025, 6, 15));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(booking));

        List<BookingDTO> bookings = bookingService.getBookingsByUserId(1L);
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getUserId());
        assertEquals(1L, bookings.get(0).getRoomId());
    }

    @Test
    void testGetBookingsByUserId_UserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsByUserId(999L));
        assertEquals("Користувача не знайдено", exception.getMessage());
        verify(bookingRepository, never()).findByUserId(anyLong());
    }
}