package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class TopExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.error("Нарушение ограничений --- " + e.getMessage());
        return new ErrorResponse("Нарушение ограничений", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterExc(final MissingServletRequestParameterException e) {
        log.error("Отсутствует параметр --- " + e.getMessage());
        return new ErrorResponse("Отсутствует параметр", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        for (ObjectError err : e.getAllErrors()) {
            sb.append(Objects.requireNonNull(e.getFieldError()).getField()).append(": ");
            sb.append(err.getDefaultMessage()).append(". ");
        }
        sb.delete(sb.length() - 2, sb.length());
        log.error("Ошибка валидации --- " + sb);
        return new ErrorResponse("Ошибка валидации", sb.toString());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestHeaderException(final MissingRequestHeaderException e) {
        log.error("Ошибка заголовка --- " + e.getMessage());
        return new ErrorResponse("Ошибка заголовка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseSimple handleBookingStatusException(final BookingStatusException e) {
        log.error("Unknown state: UNSUPPORTED_STATUS" + e.getMessage());
        return new ErrorResponseSimple(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        log.error("Неизвестное исключение --- ", e);
        return new ErrorResponse("Неизвестное исключение ", e.getMessage());
    }
}
