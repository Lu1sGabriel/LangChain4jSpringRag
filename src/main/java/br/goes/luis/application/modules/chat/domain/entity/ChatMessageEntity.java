package br.goes.luis.application.modules.chat.domain.entity;

import br.goes.luis.application.core.shared.domain.entity.BaseEntity;
import dev.langchain4j.data.message.ChatMessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessageEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chatEntity;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChatMessageType chatMessageType;

    public ChatMessageEntity(ChatEntity chatEntity, String message, ChatMessageType chatMessageType) {
        this.chatEntity = chatEntity;
        this.message = message;
        this.chatMessageType = chatMessageType;
    }

}