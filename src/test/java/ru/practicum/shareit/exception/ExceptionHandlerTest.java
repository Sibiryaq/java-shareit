package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ConstraintViolationException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExceptionHandlerTest {

    private final ExceptionsHandler handler = new ExceptionsHandler();

    @Test
    void handleEmailDuplicationExceptionTest() {
        EmailDuplicationException e = new EmailDuplicationException("Email duplication");
        ErrorResponse response = handler.handleConflictExceptions(e);

        assertNotNull(response);
        assertEquals(response.getDescription(), e.getMessage());
    }

    @Test
    void handleDataErrorExceptionTest() {
        DataIntegrityViolationException e = new DataIntegrityViolationException("Data error");
        ErrorResponse response = handler.handleConflictExceptions(e);

        assertNotNull(response);
        assertEquals(response.getDescription(), e.getMessage());
    }

    @Test
    void handleDataErrorExceptionTest_Mail() {
        DataIntegrityViolationException e = new DataIntegrityViolationException("constraint [uq_user_email]");
        ErrorResponse response = handler.handleConflictExceptions(e);

        assertNotNull(response);
        assertEquals(response.getDescription(), e.getMessage());
    }

    @Test
    void handleValidationExceptionTest() {
        ValidationException e = new ValidationException("Validation error");
        ErrorResponse response = handler.handleValidationException(e);

        assertNotNull(response);
        assertEquals(response.getDescription(), e.getMessage());
    }

    @Test
    void handleNotFoundExceptionTest() {
        NotFoundException e = new NotFoundException("Search error");
        ErrorResponse response = handler.handleNotFoundException(e);

        assertNotNull(response);
        assertEquals(response.getDescription(), e.getMessage());
    }

    @Test
    void handleBookingExceptionTest() {
        BookingException e = new BookingException("Booking error");
        ErrorResponse response = handler.handleBookingException(e);

        assertNotNull(response);
        assertEquals(response.getDescription(), e.getMessage());
    }

    @Test
    void handleBookingStatusExceptionTest() {
        BookingStatusException e = new BookingStatusException("Unknown state: UNSUPPORTED_STATUS");
        ErrorResponseSimple response = handler.handleBookingStatusException(e);

        assertNotNull(response);
        assertEquals(response.getError(), e.getMessage());
    }

    @Test
    void handleCommentExceptionTest() {
        CommentException e = new CommentException("Comment error");
        ErrorResponse response = handler.handleCommentException(e);

        assertNotNull(response);
        assertEquals(response.getDescription(), e.getMessage());
    }

    @Test
    void handleExceptionTest() {
        Exception e = new Exception("Error");
        ErrorResponse response = handler.handleException(e);

        assertNotNull(response);
        assertEquals(response.getDescription(), e.getMessage());
    }
}
