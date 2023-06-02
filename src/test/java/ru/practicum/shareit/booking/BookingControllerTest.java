package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.controllers.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSufficiencyDto;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.OverriddenPageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static User user;
    private static Item item;
    private static BookingDto bookingSaving;
    private static BookingSufficiencyDto bookingSufficiencyDto;
    private final String headerShareUserId = "X-Sharer-User-Id";
    private final Long userId = 1L;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    static void startConfig() {

        user = new User();
        user.setEmail("example@example.org");
        user.setId(1L);
        user.setName("Name");

        ItemRequest request = new ItemRequest();
        request.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("Description");
        request.setId(1L);
        request.setRequester(user);

        item = new Item();
        item.setAvailable(true);
        item.setDescription("Description");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(user);
        item.setRequest(request);

        bookingSaving = new BookingDto(
                null,
                item.getId(),
                user.getId(),
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Description",
                true,
                request.getId());


        bookingSufficiencyDto = new BookingSufficiencyDto(
                1L,
                "APPROVED",
                new UserDto(1L, "Name", "example@example.com"),
                itemDto,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2)
        );
    }


    @Test
    void shouldSaveWorkCorrectly() throws Exception {
        when(bookingService.save(anyLong(), any()))
                .thenReturn(bookingSufficiencyDto);

        String result = mvc.perform(post("/bookings")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(bookingSaving))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(bookingSufficiencyDto, mapper.readValue(result, BookingSufficiencyDto.class));
        verify(bookingService, times(1)).save(anyLong(),any());
    }


    @Test
    void saveWhenBookingIncorrectMustReturnBadRequest() throws Exception {
        BookingDto incorrectBooking = new BookingDto(
                null,
                item.getId(),
                user.getId(),
                "WAITING",
                null,
                null);

        mvc.perform(post("/bookings")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(incorrectBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> {
                    MethodArgumentNotValidException exception = (MethodArgumentNotValidException) result.getResolvedException();
                    assert exception != null;
                    BindingResult bindingResult = exception.getBindingResult();


                    List<String> errors = List.of("The booking start date cannot be empty.", "The booking end date cannot be empty.");
                    List<String> actualErrors = bindingResult.getAllErrors()
                            .stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.toList());

                    if (!errors.contains(actualErrors.get(0)) && errors.contains(actualErrors.get(1))) {
                        throw new AssertionError("Expected" + errors + "but got" + actualErrors);
                    }
                });

        verify(bookingService, never()).save(anyLong(), any());
    }


    @Test
    void shouldAcceptWorkCorrectly() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingSufficiencyDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(headerShareUserId, userId)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingSufficiencyDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService, times(1)).approve(anyLong(),any(),anyBoolean());
    }


    @Test
    void acceptWhenParamMissingMustThrowInternalServerError() throws Exception {

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(headerShareUserId, userId))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldGetByIdReturnCorrect() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingSufficiencyDto);

        String result = mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(headerShareUserId, userId))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(bookingSufficiencyDto, mapper.readValue(result, BookingSufficiencyDto.class));
        verify(bookingService, times(1)).getById(anyLong(), anyLong());
    }


    @Test
    void shouldGetByOwnerReturnCorrect() throws Exception {
        when(bookingService.getByOwner(anyLong(), anyString(), any(OverriddenPageRequest.class)))
                .thenReturn(List.of(bookingSufficiencyDto));

        String result = mvc.perform(get("/bookings/owner")
                        .header(headerShareUserId, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<BookingSufficiencyDto> actualResult = mapper.readValue(result, new TypeReference<>() {
        });

        assertEquals(List.of(bookingSufficiencyDto), actualResult);
        verify(bookingService, times(1))
                                    .getByOwner(anyLong(), anyString(), any(OverriddenPageRequest.class));
    }

    @Test
    void getBookingsByOwnerIfParamInvalidThenReturnError() throws Exception {
        ErrorResponse error = new ErrorResponse("from var must be equals 0 or bigger.size var must be equals 1 or bigger.");
        String response = mvc.perform(get("/bookings/owner")
                        .header(headerShareUserId, userId)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse actualError = mapper.readValue(response, ErrorResponse.class);

        assertEquals(error, actualError);
        verify(bookingService, never()).getByOwner(anyLong(), anyString(), any(OverriddenPageRequest.class));
    }


    @Test
    void testGetBookingsByBookerIfValueIsIncorrectReturnException() throws Exception {
        when(bookingService.getByBookerId(anyLong(), anyString(), any(OverriddenPageRequest.class)))
                .thenReturn(List.of(bookingSufficiencyDto));

        String result = mvc.perform(get("/bookings/")
                        .header(headerShareUserId, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<BookingSufficiencyDto> actualResult = mapper.readValue(result, new TypeReference<>() {
        });

        assertEquals(List.of(bookingSufficiencyDto), actualResult);

        verify(bookingService, times(1)).
                getByBookerId(anyLong(), anyString(), any(OverriddenPageRequest.class));
    }


}

