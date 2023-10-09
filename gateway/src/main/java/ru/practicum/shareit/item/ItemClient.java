package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }


    public ResponseEntity<Object> createItem(ItemDtoRequest itemDtoRequest, Long userId) {
        return post("", userId, itemDtoRequest);
    }

    public ResponseEntity<Object> updateOwnItem(ItemDtoRequest itemDtoRequest, Long userId) {
        return patch("/" + itemDtoRequest.getId(), userId, itemDtoRequest);
    }

    public ResponseEntity<Object> getItem(long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getOwnItems(Long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> searchItems(String text, Long userId) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(CommentDtoRequest commentDtoRequest, long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDtoRequest);
    }
}
