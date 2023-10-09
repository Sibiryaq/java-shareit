package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler({EmailDuplicationException.class, DataIntegrityViolationException.class})
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ErrorResponse handleConflictExceptions(final RuntimeException e) {
        String errorMessage;
        if (e instanceof EmailDuplicationException) {
            errorMessage = "Дублирование почты";
        } else {
            errorMessage = "Ошибка данных";
        }
        log.error(errorMessage + " --- " + e.getMessage());
        return new ErrorResponse(errorMessage, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка поиска --- " + e.getMessage());
        return new ErrorResponse("Ошибка поиска", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Ошибка валидации --- " + e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestHeaderException(final MissingRequestHeaderException e) {
        log.error("Ошибка заголовка --- " + e.getMessage());
        return new ErrorResponse("Ошибка заголовка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingException(final BookingException e) {
        log.info("Ошибка бронирования --- " + e.getMessage());
        return new ErrorResponse("Ошибка бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseSimple handleBookingStatusException(final BookingStatusException e) {
        log.info("Неизвестное состояние: UNSUPPORTED_STATUS" + e.getMessage());
        return new ErrorResponseSimple(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCommentException(final CommentException e) {
        log.info("Ошибка комментирования --- " + e.getMessage());
        return new ErrorResponse("Ошибка комментирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        log.error("Неизвестное исключение: ", e);
        return new ErrorResponse("Неизвестное исключение: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        for (ObjectError err : e.getAllErrors()) {
            sb.append(e.getParameter().getParameterName()).append(": ");
            sb.append(err.getDefaultMessage()).append(". ");
        }
        sb.delete(sb.length() - 2, sb.length());
        log.error("Ошибка валидации --- " + sb);
        return new ErrorResponse("Ошибка валидации", sb.toString());
    }
}
