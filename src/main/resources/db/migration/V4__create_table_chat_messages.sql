CREATE TABLE chat_messages
(
    id         UUID PRIMARY KEY,
    chat_id    UUID                     NOT NULL,
    message    TEXT                     NOT NULL,
    type       VARCHAR(50)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_chat_messages_chat FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE
);

CREATE INDEX idx_chat_messages_chat_id ON chat_messages (chat_id);
CREATE INDEX idx_chat_messages_deleted_at ON chat_messages (deleted_at);