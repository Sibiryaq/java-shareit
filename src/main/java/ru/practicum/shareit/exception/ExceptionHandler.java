package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ErrorResponse handleEmailDuplicationException(final EmailDuplicationException e) {
        log.warn("Дублирование почты --- " + e.getMessage());
        return new ErrorResponse("Дублирование почты", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ErrorResponse handleDataErrorException(final DataIntegrityViolationException e) {
        log.warn("Ошибка данных --- " + e.getMessage());
        return new ErrorResponse("Ошибка данных", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn("Ошибка валидации --- " + e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestHeaderException(final MissingRequestHeaderException e) {
        log.warn("Ошибка заголовка --- " + e.getMessage());
        return new ErrorResponse("Ошибка заголовка", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn("Ошибка поиска --- " + e.getMessage());
        return new ErrorResponse("Ошибка поиска", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingException(final BookingException e) {
        log.info("Ошибка бронирования --- " + e.getMessage());
        return new ErrorResponse("Ошибка бронирования", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseSimple handleBookingStatusException(final BookingStatusException e) {
        log.info("Неизвестное состояние: UNSUPPORTED_STATUS" + e.getMessage());
        return new ErrorResponseSimple(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCommentException(final CommentException e) {
        log.info("Ошибка комментирования --- " + e.getMessage());
        return new ErrorResponse("Ошибка комментирования", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        for (ObjectError err : e.getAllErrors()) {
            sb.append(e.getParameter().getParameterName()).append(": ");
            sb.append(err.getDefaultMessage()).append(". ");
        }
        sb.delete(sb.length() - 2, sb.length());
        log.warn("Ошибка валидации --- " + sb);
        return new ErrorResponse("Ошибка валидации", sb.toString());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        log.warn("Неизвестное исключение: ", e);
        return new ErrorResponse("Неизвестное исключение: ", e.getMessage());
    }
}
