DROP TABLE IF EXISTS users, requests, items, bookings, comments;

CREATE TABLE IF NOT EXISTS users (
    user_id         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_name       VARCHAR(250) NOT NULL,
    email           VARCHAR(320) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT uq_user_email unique (email)
    );

CREATE TABLE IF NOT EXISTS requests (
    request_id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description     VARCHAR(500) NOT NULL,
    requestor_id    BIGINT NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (request_id),
    CONSTRAINT fk_request_to_users FOREIGN KEY (request_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS items (
    item_id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_name       VARCHAR(200) NOT NULL,
    description     VARCHAR(500) NOT NULL,
    is_available    boolean NOT NULL,
    owner_id        BIGINT,
    request_id      BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (item_id),
    CONSTRAINT fk_items_to_users FOREIGN KEY (owner_id) REFERENCES users(user_id),
    CONSTRAINT fk_items_to_requests FOREIGN KEY (request_id) REFERENCES requests(request_id)
    );

CREATE TABLE IF NOT EXISTS bookings (
    booking_id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id         BIGINT NOT NULL,
    booker_id       BIGINT NOT NULL,
    status          VARCHAR(20) NOT NULL,
    CONSTRAINT pk_bookings PRIMARY KEY (booking_id),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items(item_id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS comments (
    comment_id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text            VARCHAR(500) NOT NULL,
    item_id         BIGINT NOT NULL,
    author_id       BIGINT NOT NULL,
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (comment_id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items(item_id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users(user_id)
    );