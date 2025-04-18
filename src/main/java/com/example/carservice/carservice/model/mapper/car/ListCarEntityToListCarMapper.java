package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.entity.CarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting a list of {@link CarEntity} to a list of {@link Car} domain models.
 */
@Mapper
public interface ListCarEntityToListCarMapper {

    CarEntityToCarMapper carEntityToCarMapper = Mappers.getMapper(CarEntityToCarMapper.class);

    /**
     * Maps a list of {@link CarEntity} to a list of {@link Car} domain models.
     *
     * @param carEntities list of JPA entities
     * @return list of domain models
     */
    default List<Car> toCarList(List<CarEntity> carEntities) {
        if (carEntities == null) {
            return null;
        }

        return carEntities.stream()
                .map(carEntityToCarMapper::mapFromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Initializes the mapper using MapStruct's factory method.
     *
     * @return a singleton mapper instance
     */
    static ListCarEntityToListCarMapper initialize() {
        return Mappers.getMapper(ListCarEntityToListCarMapper.class);
    }

}

