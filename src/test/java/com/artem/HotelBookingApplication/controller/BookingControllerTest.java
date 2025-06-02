package com.artem.HotelBookingApplication.controller;

import com.artem.HotelBookingApplication.security.SecurityConfig;
import com.artem.HotelBookingApplication.security.jwt.AuthTokenFilter;
import com.artem.HotelBookingApplication.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private AuthTokenFilter authTokenFilter;

    private ObjectMapper objectMapper;
    private BookingDTO bookingDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setRoomId(1L);
        bookingDTO.setCheckInDate(LocalDate.of(2025, 6, 10));
        bookingDTO.setCheckOutDate(LocalDate.of(2025, 6, 15));
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        Mockito.doNothing().when(bookingService).createBooking(Mockito.any(BookingDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Бронювання успішно створено"));
    }

    @Test
    void testCreateBooking_InvalidInput() throws Exception {
        bookingDTO.setCheckInDate(null);
        Mockito.doThrow(new IllegalArgumentException("Дата заїзду обов’язкова"))
                .when(bookingService).createBooking(Mockito.any(BookingDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Помилка створення бронювання: Дата заїзду обов’язкова"));
    }

    @Test
    void testGetUserBookings_Success() throws Exception {
        List<BookingDTO> bookings = List.of(bookingDTO);
        Mockito.when(bookingService.getBookingsByUserId(1L)).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].roomId").value(1L));
    }

    @Test
    void testGetUserBookings_UserNotFound() throws Exception {
        Mockito.when(bookingService.getBookingsByUserId(999L))
                .thenThrow(new IllegalArgumentException("Користувача не знайдено"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/user/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}