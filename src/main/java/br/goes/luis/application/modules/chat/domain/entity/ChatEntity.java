package br.goes.luis.application.modules.chat.domain.entity;

import br.goes.luis.application.core.shared.domain.entity.BaseEntity;
import br.goes.luis.application.modules.document.domain.entity.DocumentEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "chatEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy(value = "createAt")
    private final Set<ChatMessageEntity> chatMessages = new HashSet<>();

    @OneToMany(mappedBy = "chatEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private final Set<ChatDocumentEntity> chatDocuments = new HashSet<>();

    public ChatEntity(String name) {
        this.name = name;
    }

    public void addMessage(ChatMessageEntity message) {
        this.chatMessages.add(message);
    }

    public void addDocument(List<DocumentEntity> documents) {
        documents.forEach(document -> chatDocuments.add(new ChatDocumentEntity(this, document)));
    }

}