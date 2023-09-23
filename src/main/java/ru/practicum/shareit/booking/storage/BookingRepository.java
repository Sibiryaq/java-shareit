package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(" SELECT b" +
            " FROM Booking b " +
            " JOIN User u ON (u.id = b.booker.id)" +
            " WHERE b.booker.id = ?1 AND b.end > ?2 AND b.start < ?2"
    )
    List<Booking> findByBookerIdAndEndIsBeforeAndStartIsAfter(Long bookerId, LocalDateTime end,
                                                              LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    @Query(" SELECT b" +
            " FROM Booking b " +
            " JOIN User u ON (u.id = b.booker.id)" +
            " WHERE u.id = ?1 AND b.end < ?2"
    )
    List<Booking> findByBookerIdAndEndAfter(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findAllByItemId(long itemId);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "JOIN User u ON (u.id = b.item.user.id) " +
            "WHERE u.id = ?1"
    )
    List<Booking> bookingsForItem(Long ownerId, Sort sort);

    @Query(" SELECT b" +
            " FROM Booking b " +
            " JOIN Item i ON (i.id = b.item.id) " +
            " WHERE i.id = ?1"
    )
    List<Booking> allBookingsForItem(Long itemId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN User u ON (u.id = b.item.user.id) " +
            "WHERE u.id = ?1 AND b.end < ?2"
    )
    List<Booking> bookingsForItemPast(Long ownerId, LocalDateTime now, Sort sort);

    @Query(" SELECT b" +
            " FROM Booking b " +
            " JOIN Item i ON (i.id = b.item.id) " +
            " JOIN User u ON (u.id = i.user.id)" +
            " WHERE b.booker.id = ?1 AND i.id = ?2 AND b.end < ?3"
    )
    List<Booking> bookingsForItemAndBookerPast(Long bookerId, Long itemId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN User u ON (u.id = b.item.user.id) " +
            "WHERE u.id = ?1 AND b.start > ?2"
    )
    List<Booking> bookingsForItemFuture(Long ownerId, LocalDateTime now, Sort sort);

    @Query(" SELECT b" +
            " FROM Booking b " +
            " JOIN User u ON (u.id = b.item.user.id)" +
            " WHERE b.item.user.id = ?1 AND b.end >= ?2 AND b.start < ?2"
    )
    List<Booking> bookingsForItemCurrent(Long ownerId, LocalDateTime now, Sort sort);

    @Query(" SELECT b" +
            " FROM Booking b " +
            " JOIN User u ON (u.id = b.item.user.id)" +
            " WHERE b.item.user.id = ?1 AND b.status = 'WAITING'"
    )
    List<Booking> bookingsForItemWaiting(Long ownerId, Sort sort);

    @Query(" SELECT b" +
            " FROM Booking b " +
            " JOIN User u ON (u.id = b.item.user.id)" +
            " WHERE b.item.user.id = ?1 AND b.status = 'REJECTED'"
    )
    List<Booking> bookingsForItemRejected(Long ownerId, Sort sort);


}
