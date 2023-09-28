package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    @Query("SELECT bc " +
            "FROM Booking AS bc " +
            "WHERE bc.booker.id = :bookerId " +
            "AND bc.start < :time " +
            "AND bc.end > :time " +
            "ORDER BY bc.start DESC")
    List<Booking> findByBookerIdCurrent(Long bookerId, LocalDateTime time);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findBookingByItemOwnerId(Long ownerId, Sort sort);

    @Query("SELECT bc " +
            "FROM Booking AS bc " +
            "WHERE bc.item.owner.id = :ownerId " +
            "AND bc.start < :time " +
            "AND bc.end > :time " +
            "ORDER BY bc.start ASC")
    List<Booking> findBookingByItemOwnerCurrent(Long ownerId, LocalDateTime time);

    List<Booking> findBookingByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime time, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime time, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    @Query("SELECT bc " +
            "FROM Booking AS bc " +
            "WHERE bc.booker.id = :userId " +
            "AND bc.item.id = :itemId " +
            "AND bc.end < :time")
    List<Booking> findBookingForComments(Long userId, Long itemId, LocalDateTime time);

    @Query("SELECT bc " +
            "FROM Booking AS bc " +
            "WHERE bc.start < :time " +
            "AND bc.item.owner.id = :userId " +
            "AND bc.item.id = :itemId " +
            "AND bc.status = 'APPROVED' " +
            "ORDER BY bc.start DESC")
    List<Booking> getLastBooking(LocalDateTime time, Long userId, Long itemId);

    @Query("SELECT bc " +
            "FROM Booking AS bc " +
            "WHERE bc.start > :time " +
            "AND bc.item.owner.id = :userId " +
            "AND bc.item.id = :itemId " +
            "AND bc.status = 'APPROVED' " +
            "ORDER BY bc.start ASC")
    List<Booking> getNextBooking(LocalDateTime time, Long userId, Long itemId);

}
