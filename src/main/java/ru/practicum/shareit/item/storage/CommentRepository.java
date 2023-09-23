package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(long itemId);

    @Query("select c from Comment c" +
            " join Item i on c.item.id = i.id " +
            " join User u on i.user.id = u.id " +
            " where i.id = ?1 and u.id = ?2")
    List<Comment> findAllByItemIdAndOwnerId(long itemId, Long ownerId);
}
