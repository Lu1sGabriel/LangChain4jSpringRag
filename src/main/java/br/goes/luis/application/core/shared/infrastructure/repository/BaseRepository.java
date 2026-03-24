package br.goes.luis.application.core.shared.infrastructure.repository;

import br.goes.luis.application.core.infrastructure.exception.domain.entity.EntityNotActivateException;
import br.goes.luis.application.core.shared.domain.entity.BaseEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<Entity extends BaseEntity> extends JpaRepository<Entity, UUID> {


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