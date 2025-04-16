package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.entity.CarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ListCarEntityToListCarMapper {

    CarEntityToCarMapper carEntityToCarMapper = Mappers.getMapper(CarEntityToCarMapper.class);

    default List<Car> toCarList(List<CarEntity> carEntities) {
        if (carEntities == null) {
            return null;
        }

        return carEntities.stream()
                .map(carEntityToCarMapper::mapFromEntity)
                .collect(Collectors.toList());
    }

    static ListCarEntityToListCarMapper initialize() {
        return Mappers.getMapper(ListCarEntityToListCarMapper.class);
    }
}

