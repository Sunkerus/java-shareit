package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static ItemRequestDto requestDto;
    private final String headerShareUserId = "X-Sharer-User-Id";
    private final Long userId = 1L;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setUp() {
        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Description",
                true,
                2L);

        requestDto = new ItemRequestDto(
                1L,
                "Description",
                List.of(itemDto),
                LocalDateTime.now());
    }


    @Test
    void shouldItemRequestCreateCorrectly() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(requestDto);

        String response = mvc.perform(post("/requests")
                        .header(headerShareUserId, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(requestDto, mapper.readValue(response, ItemRequestDto.class));
        verify(itemRequestService, times(1)).createItemRequest(anyLong(), any());
    }


    @Test
    void shouldItemRequestByIdGetCorrectly() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(requestDto);

        String response = mvc.perform(get("/requests/{id}", 1L)
                        .header(headerShareUserId, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(requestDto, mapper.readValue(response, ItemRequestDto.class));
        verify(itemRequestService, times(1)).getItemRequestById(anyLong(), anyLong());
    }


    @Test
    void shouldAllItemRequestsGetCorrectly() throws Exception {
        when(itemRequestService.getAllItemRequests(0, 2, userId)).thenReturn(List.of(requestDto));

        String response = mvc.perform(get("/requests/all")
                        .header(headerShareUserId, userId)
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<ItemRequestDto> actualRequests = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(List.of(requestDto), actualRequests);
        verify(itemRequestService, times(1)).getAllItemRequests(0, 2, userId);
    }

    @Test
    void shouldItemRequestByOwnerIdGetCorrectly() throws Exception {
        when(itemRequestService.getItemRequestByOwnerId(anyLong())).thenReturn(List.of(requestDto));

        String response = mvc.perform(get("/requests")
                        .header(headerShareUserId, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<ItemRequestDto> actualRequests = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(List.of(requestDto), actualRequests);
        verify(itemRequestService, times(1)).getItemRequestByOwnerId(anyLong());
    }
}