package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.response.ServiceResponse;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceDtoToServiceResponseMapperImplTest {

    private final ServiceDtoToServiceResponseMapper mapper = new ServiceDtoToServiceResponseMapperImpl();

    @Test
    void testMapCollection_whenSourceIsNull_thenReturnNull() {

        // When
        List<ServiceResponse> result = mapper.map((Collection<ServiceDto>) null);

        // Then
        assertNull(result);

    }

    @Test
    void testMapSingle_whenSourceIsNull_thenReturnNull() {

        // When
        ServiceResponse result = mapper.map((ServiceDto) null);

        // Then
        assertNull(result);

    }

}
