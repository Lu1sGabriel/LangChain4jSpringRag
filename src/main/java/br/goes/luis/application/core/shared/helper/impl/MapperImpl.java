package br.goes.luis.application.core.shared.helper.impl;

import br.goes.luis.application.core.shared.helper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MapperImpl<D, E> implements Mapper<D, E> {

    @Override
    public D toDto(E entity, Function<E, D> function) {
        if (entity == null) {
            return null;
        }
        return function.apply(entity);
    }

    @Override
    public List<D> toDtoList(List<E> entityList, Function<E, D> function) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .map(function)
                .toList();
    }

    @Override
    public Page<D> toDoPage(Page<E> entityPage, Function<E, D> function) {
        if (entityPage == null || entityPage.isEmpty()) {
            return Page.empty();
        }
        return entityPage.map(function);
    }

}