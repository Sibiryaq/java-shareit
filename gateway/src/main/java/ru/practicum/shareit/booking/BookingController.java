package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.BookingStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private static final String USER_HEADER = "X-Sharer-User-Id";
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDtoRequest bookingDtoRequest,
												@RequestHeader(USER_HEADER) Long userId) {
		if (bookingDtoRequest.getStart() == null || bookingDtoRequest.getEnd() == null) {
			throw new BookingException("Начало и окончание бронирования не должны быть пустыми");
		}
		if (bookingDtoRequest.getStart().isEqual(bookingDtoRequest.getEnd())) {
			throw new BookingException("Начало и окончание бронирования одинаковы");
		}
		if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
			throw new BookingException("Окончание бронирования раньше начала бронирования");
		}
		log.info("Booking Controller: Бронирование создано. ID пользователя {}", userId);
		return bookingClient.createBooking(bookingDtoRequest, userId);
	}

	@PatchMapping(value = "/{bookingId}")
	public ResponseEntity<Object> changeState(@PathVariable Long bookingId,
											  @RequestParam Boolean approved,
											  @RequestHeader(USER_HEADER) Long userId) {
		log.info("Booking Controller: изменение состояние бронирования. " +
				"ID Пользователя {}, ID бронирования {}", userId, bookingId);
		return bookingClient.changeState(bookingId, approved, userId);
	}

	@GetMapping(value = "/{bookingId}")
	public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
											 @RequestHeader(USER_HEADER) Long userId) {
		log.info("Booking Controller: Получение бронирования. ID Пользователя {}," +
				" ID бронирования {}", userId, bookingId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getOwnBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
												 @RequestParam(defaultValue = "0")
												 @Min(value = 0, message = "Minimum from: 0") int from,
												 @RequestParam(defaultValue = "10")
												 @Min(value = 1, message = "Minimum size: 1") int size,
												 @RequestHeader(USER_HEADER) Long userId) {
		log.info("Booking Controller: Получены бронирования владельца. " +
				"Пользователь с ID {}, Состояние {}", userId, state);
		BookingState realState;
		try {
			realState = BookingState.valueOf(state.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BookingStatusException("Unknown state: UNSUPPORTED_STATUS");
		}
		return bookingClient.getOwnBookings(realState, userId, from, size);
	}

	@GetMapping(value = "/owner")
	public ResponseEntity<Object> getOwnItemsBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
													  @RequestParam(defaultValue = "0")
													  @Min(value = 0, message = "Minimum from: 0") int from,
													  @RequestParam(defaultValue = "10")
													  @Min(value = 1, message = "Minimum size: 1") int size,
													  @RequestHeader(USER_HEADER) Long userId) {
		log.info("Booking Controller: Получен владелец бронирований. ID пользователя {}, Состояние {}", userId, state);
		BookingState realState;
		try {
			realState = BookingState.valueOf(state.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BookingStatusException("Unknown state: UNSUPPORTED_STATUS");
		}
		return bookingClient.getOwnItemsBookings(realState, userId, from, size);
	}
}
