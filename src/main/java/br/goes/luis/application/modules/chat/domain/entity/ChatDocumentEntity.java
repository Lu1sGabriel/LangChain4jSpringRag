package br.goes.luis.application.modules.chat.domain.entity;

import br.goes.luis.application.core.shared.domain.entity.BaseEntity;
import br.goes.luis.application.modules.document.domain.entity.DocumentEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_documents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatDocumentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chatEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity documentEntity;

    public ChatDocumentEntity(ChatEntity chatEntity, DocumentEntity documentEntity) {
        this.chatEntity = chatEntity;
        this.documentEntity = documentEntity;
    }

}