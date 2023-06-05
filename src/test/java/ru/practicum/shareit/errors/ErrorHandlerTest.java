package ru.practicum.shareit.errors;

import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exceptions.ErrorHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class ErrorHandlerTest {

    @Test
    void checkMethodNotValidExceptionWorkCorrectly() {


        ErrorHandler errorHandler = new ErrorHandler();

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        when(ex.getFieldError()).thenReturn(new FieldError("name", "templateField", "defaultMessage"));


        assertEquals("defaultMessage", errorHandler.handleMethodArgumentNotValidException(ex).getError());
        verify(ex, atLeast(1)).getFieldError();
    }


}
