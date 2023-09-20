package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .item(comment.getItem())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}