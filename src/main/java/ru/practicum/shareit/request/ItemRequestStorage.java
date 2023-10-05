package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    @Query("SELECT rq " +
            "FROM ItemRequest AS rq " +
            "WHERE rq.requestor.id <> :userId")
    Page<ItemRequest> findAllOtherRequests(Long userId, Pageable pageable);
}
