package br.goes.luis.application.modules.chat.application.useCase;

import java.util.List;
import java.util.UUID;

public interface ChatAttachDocumentUseCase {
    void attach(UUID chatId, List<UUID> documentsIds);
}