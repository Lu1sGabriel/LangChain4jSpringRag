CREATE TABLE chat_documents
(
    id          UUID PRIMARY KEY,
    chat_id     UUID                     NOT NULL,
    document_id UUID                     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_chat_documents_chat FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_documents_document FOREIGN KEY (document_id) REFERENCES documents (id) ON DELETE CASCADE
);

CREATE INDEX idx_chat_documents_chat_id ON chat_documents (chat_id);
CREATE INDEX idx_chat_documents_document_id ON chat_documents (document_id);
CREATE INDEX idx_chat_documents_deleted_at ON chat_documents (deleted_at);