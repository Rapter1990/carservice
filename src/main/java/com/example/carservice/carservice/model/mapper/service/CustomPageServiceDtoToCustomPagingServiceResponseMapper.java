package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.response.ServiceResponse;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.response.CustomPagingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface CustomPageServiceDtoToCustomPagingServiceResponseMapper {

    ServiceDtoToServiceResponseMapper serviceMapper = Mappers.getMapper(ServiceDtoToServiceResponseMapper.class);

    /**
     * Converts a CustomPage of ServiceDto into a CustomPagingResponse of ServiceResponse.
     *
     * @param servicePage the custom paginated service DTOs
     * @return the mapped paginated service responses
     */
    default CustomPagingResponse<ServiceResponse> toPagingResponse(CustomPage<ServiceDto> servicePage) {
        if (servicePage == null) {
            return null;
        }

        return CustomPagingResponse.<ServiceResponse>builder()
                .content(toServiceResponseList(servicePage.getContent()))
                .totalElementCount(servicePage.getTotalElementCount())
                .totalPageCount(servicePage.getTotalPageCount())
                .pageNumber(servicePage.getPageNumber())
                .pageSize(servicePage.getPageSize())
                .build();
    }

    /**
     * Converts a list of ServiceDto into a list of ServiceResponse.
     *
     * @param services the service DTOs
     * @return the mapped service responses
     */
    default List<ServiceResponse> toServiceResponseList(List<ServiceDto> services) {
        if (services == null) {
            return null;
        }

        return services.stream()
                .map(serviceMapper::map)
                .collect(Collectors.toList());
    }

    static CustomPageServiceDtoToCustomPagingServiceResponseMapper initialize() {
        return Mappers.getMapper(CustomPageServiceDtoToCustomPagingServiceResponseMapper.class);
    }

}

