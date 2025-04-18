package com.example.carservice.carservice.model.dto.request.services;

import com.example.carservice.common.model.dto.request.CustomPagingRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterServicePagingRequest {

    @Valid
    private ListServiceRequest filterRequest;

    @Valid
    private CustomPagingRequest pagingRequest;
}

