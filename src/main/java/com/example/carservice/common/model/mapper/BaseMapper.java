package com.example.carservice.common.model.mapper;

import java.util.Collection;
import java.util.List;

/**
 * Generic interface for mapping between source and target types.
 *
 * <p>This interface defines the contract for mapping a single object or a collection
 * of objects from a source type {@code S} to a target type {@code T}.</p>
 *
 * @param <S> the source type
 * @param <T> the target type
 */
public interface BaseMapper<S, T> {

    /**
     * Maps a single source object to a target object.
     *
     * @param source the source object to map
     * @return the mapped target object
     */
    T map(S source);

    /**
     * Maps a collection of source objects to a list of target objects.
     *
     * @param sources the collection of source objects
     * @return a list of mapped target objects
     */
    List<T> map(Collection<S> sources);

}
