package br.goes.luis.application.core.shared.infrastructure.repository;

import br.goes.luis.application.core.infrastructure.exception.domain.entity.EntityNotActivateException;
import br.goes.luis.application.core.shared.domain.entity.BaseEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<Entity extends BaseEntity> extends JpaRepository<Entity, UUID> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.id IN :ids AND e.deletedAt IS NULL")
    List<Entity> findAllActiveById(@Param("ids") List<UUID> ids);

    default Entity findByIdOrThrow(UUID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entidade com o ID (%s) não encontrada", id)));
    }

    default Entity findByIdAndActivateOrThrow(UUID id) {
        var entity = findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entidade com o ID (%s) não encontrada", id)));

        if (!entity.isActivate()) {
            throw new EntityNotActivateException("A entidade não está ativa");
        }

        return entity;
    }

    @Override
    default void delete(Entity entity) {
        entity.deactivate();
        save(entity);
    }

    default void activate(Entity entity) {
        entity.activate();
        save(entity);
    }

}