package com.example.carservice.carservice.controller;

import com.example.carservice.base.AbstractRestControllerTest;
import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.request.services.*;
import com.example.carservice.carservice.model.dto.response.ServiceResponse;
import com.example.carservice.carservice.model.enums.ServiceStatus;
import com.example.carservice.carservice.model.mapper.service.CustomPageServiceDtoToCustomPagingServiceResponseMapper;
import com.example.carservice.carservice.model.mapper.service.ServiceDtoToServiceResponseMapper;
import com.example.carservice.carservice.service.ServicesToCarService;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.CustomPaging;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ServicesControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    ServicesToCarService servicesToCarService;

    private final ServiceDtoToServiceResponseMapper responseMapper = ServiceDtoToServiceResponseMapper.initialize();
    private final CustomPageServiceDtoToCustomPagingServiceResponseMapper pageMapper =
            CustomPageServiceDtoToCustomPagingServiceResponseMapper.initialize();

    @Test
    void testCreateServiceWithAdminToken() throws Exception {

        // Given
        final CreateServiceRequest request = CreateServiceRequest.builder()
                .title("Engine Check")
                .description("Full diagnostic engine service")
                .status(ServiceStatus.CREATED)
                .build();

        final ServiceDto dto = ServiceDto.builder()
                .id(UUID.randomUUID().toString())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        final ServiceResponse expected = responseMapper.map(dto);

        // When
        when(servicesToCarService.createService(any(CreateServiceRequest.class))).thenReturn(dto);

        // Then
        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.title").value(expected.getTitle()))
                .andExpect(jsonPath("$.response.description").value(expected.getDescription()))
                .andExpect(jsonPath("$.response.status").value(expected.getStatus().toString()));

        // Verify
        verify(servicesToCarService).createService(any(CreateServiceRequest.class));

    }

    @Test
    void testAssignServiceToCarWithAdminToken() throws Exception {

        // Given
        final AssignServiceToCarRequest request = AssignServiceToCarRequest.builder()
                .carId(UUID.randomUUID().toString())
                .serviceId(UUID.randomUUID().toString())
                .build();

        final ServiceDto dto = ServiceDto.builder()
                .id(request.getServiceId())
                .title("Tire Rotation")
                .description("Rotated all four tires")
                .status(ServiceStatus.PENDING)
                .build();

        // When
        when(servicesToCarService.assignServiceToCar(any())).thenReturn(dto);

        // Then
        mockMvc.perform(post("/api/services/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.id").value(dto.getId()))
                .andExpect(jsonPath("$.response.status").value(dto.getStatus().toString()));

        // Verify
        verify(servicesToCarService).assignServiceToCar(any(AssignServiceToCarRequest.class));

    }

    @Test
    void testGetAllServicesWithAdminToken() throws Exception {

        // Given
        final CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        final List<ServiceDto> dtos = List.of(
                ServiceDto.builder().id(UUID.randomUUID().toString()).title("X").status(ServiceStatus.DONE).build()
        );

        final CustomPage<ServiceDto> page = CustomPage.of(dtos, new PageImpl<>(dtos));

        // When
        when(servicesToCarService.getAllServices(any())).thenReturn(page);

        // Then
        mockMvc.perform(post("/api/services/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagingRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.length()").value(1));

        // Verify
        verify(servicesToCarService).getAllServices(any(CustomPagingRequest.class));

    }

    @Test
    void testUpdateServiceWithAdminToken() throws Exception {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String serviceId = UUID.randomUUID().toString();

        final UpdateServiceRequest updateRequest = UpdateServiceRequest.builder()
                .title("Updated Service")
                .description("Updated Description")
                .status(ServiceStatus.IN_PROGRESS)
                .build();

        final ServiceDto updated = ServiceDto.builder()
                .id(serviceId)
                .title(updateRequest.getTitle())
                .description(updateRequest.getDescription())
                .status(updateRequest.getStatus())
                .build();

        // When
        when(servicesToCarService.updateServiceByCarId(eq(carId), eq(serviceId), any(UpdateServiceRequest.class)))
                .thenReturn(updated);

        // Then
        mockMvc.perform(put("/api/services/car/{carId}/service/{serviceId}", carId, serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.title").value(updateRequest.getTitle()))
                .andExpect(jsonPath("$.response.status").value(updateRequest.getStatus().toString()));

        // Verify
        verify(servicesToCarService).updateServiceByCarId(eq(carId), eq(serviceId), any(UpdateServiceRequest.class));

    }

    @Test
    void testFilterServicesWithAdminToken() throws Exception {

        // Given
        ListServiceRequest.Filter filter = new ListServiceRequest.Filter();
        filter.setCarId(Optional.of(UUID.randomUUID().toString()));
        filter.setStatus(Optional.of(ServiceStatus.DONE));

        ListServiceRequest listRequest = new ListServiceRequest();
        listRequest.setFilter(filter);

        CustomPagingRequest pagingRequest = new CustomPagingRequest();
        pagingRequest.setPagination(CustomPaging.builder().pageNumber(1).pageSize(10).build());

        final FilterServicePagingRequest wrapper = new FilterServicePagingRequest(listRequest, pagingRequest);

        final List<ServiceDto> dtos = List.of(
                ServiceDto.builder().id(UUID.randomUUID().toString()).title("Filter Match").status(ServiceStatus.DONE).build()
        );

        final CustomPage<ServiceDto> page = CustomPage.of(dtos, new PageImpl<>(dtos));

        // When
        when(servicesToCarService.getServices(any(ListServiceRequest.class), any(CustomPagingRequest.class))).thenReturn(page);

        // Then
        mockMvc.perform(post("/api/services/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrapper))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.length()").value(1))
                .andExpect(jsonPath("$.response.content[0].status").value("DONE"));

        // Verify
        verify(servicesToCarService).getServices(any(ListServiceRequest.class), any(CustomPagingRequest.class));

    }

    @Test
    void testGetServicesByCarWithAdminToken() throws Exception {

        // Given
        final String carId = UUID.randomUUID().toString();

        final CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder().pageNumber(1).pageSize(10).build())
                .build();

        final List<ServiceDto> dtos = List.of(
                ServiceDto.builder()
                        .id(UUID.randomUUID().toString())
                        .title("Oil Change")
                        .description("Change oil")
                        .status(ServiceStatus.DONE)
                        .build()
        );

        final CustomPage<ServiceDto> page = CustomPage.of(dtos, new PageImpl<>(dtos));

        // When
        when(servicesToCarService.getServicesByCarId(eq(carId), any(CustomPagingRequest.class)))
                .thenReturn(page);

        // Then
        mockMvc.perform(post("/api/services/car/{carId}", carId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagingRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content.length()").value(1))
                .andExpect(jsonPath("$.response.content[0].title").value("Oil Change"))
                .andExpect(jsonPath("$.response.content[0].status").value("DONE"));

        // Verify
        verify(servicesToCarService).getServicesByCarId(eq(carId), any(CustomPagingRequest.class));

    }

}
