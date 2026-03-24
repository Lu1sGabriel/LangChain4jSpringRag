package br.goes.luis.application.modules.chat.infrastructure.repository;

import br.goes.luis.application.core.shared.infrastructure.repository.BaseRepository;
import br.goes.luis.application.modules.chat.domain.entity.ChatEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends BaseRepository<ChatEntity> {
}