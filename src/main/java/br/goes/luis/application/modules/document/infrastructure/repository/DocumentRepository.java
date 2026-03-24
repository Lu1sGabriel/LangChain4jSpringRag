package br.goes.luis.application.modules.document.infrastructure.repository;

import br.goes.luis.application.core.shared.infrastructure.repository.BaseRepository;
import br.goes.luis.application.modules.document.domain.entity.DocumentEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends BaseRepository<DocumentEntity> {
    boolean existsByHash(String hash);
}