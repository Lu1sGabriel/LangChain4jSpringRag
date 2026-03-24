CREATE TABLE chats
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255)             NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE

);

CREATE INDEX idx_chats_deleted_at ON chats (deleted_at);