package br.goes.luis.application.core.shared.helper;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public interface Mapper<D, E> {
    D toDto(E entity, Function<E, D> function);

    List<D> toDtoList(List<E> entityList, Function<E, D> function);

    Page<D> toDoPage(Page<E> entityPage, Function<E, D> function);
}