package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.response.ServiceResponse;
import com.example.carservice.carservice.model.enums.ServiceStatus;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.response.CustomPagingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomPageServiceDtoToCustomPagingServiceResponseMapperTest {

    private final CustomPageServiceDtoToCustomPagingServiceResponseMapper mapper =
            CustomPageServiceDtoToCustomPagingServiceResponseMapper.initialize();

    @Test
    void testToPagingResponse_whenServicePageIsNull_thenReturnNull() {

        // Given
        CustomPage<ServiceDto> servicePage = null;

        // When
        CustomPagingResponse<ServiceResponse> result = mapper.toPagingResponse(servicePage);

        // Then
        assertNull(result);

    }

    @Test
    void testToServiceResponseList_whenServiceListIsNull_thenReturnNull() {

        // Given
        List<ServiceDto> services = null;

        // When
        List<ServiceResponse> result = mapper.toServiceResponseList(services);

        // Then
        assertNull(result);

    }

    @Test
    void testToPagingResponse_whenValidServicePage_thenReturnsMappedResponse() {

        // Given
        ServiceDto dto = ServiceDto.builder()
                .id(UUID.randomUUID().toString())
                .title("Coolant Check")
                .description("Inspect and refill coolant levels")
                .status(ServiceStatus.CREATED)
                .build();

        List<ServiceDto> dtoList = List.of(dto);
        Page<ServiceDto> page = new PageImpl<>(dtoList);

        CustomPage<ServiceDto> customPage = CustomPage.of(dtoList, page);

        // When
        CustomPagingResponse<ServiceResponse> result = mapper.toPagingResponse(customPage);

        // Then
        assertNotNull(result);
        assertEquals(customPage.getTotalElementCount(), result.getTotalElementCount());
        assertEquals(customPage.getPageSize(), result.getPageSize());
        assertEquals(customPage.getPageNumber(), result.getPageNumber());
        assertEquals(1, result.getContent().size());
        assertEquals(dto.getTitle(), result.getContent().get(0).getTitle());
        assertEquals(dto.getDescription(), result.getContent().get(0).getDescription());
        assertEquals(dto.getStatus(), result.getContent().get(0).getStatus());

    }

}