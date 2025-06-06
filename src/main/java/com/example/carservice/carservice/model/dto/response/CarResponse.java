package com.example.carservice.carservice.model.dto.response;

import com.example.carservice.auth.model.dto.response.UserResponse;
import com.example.carservice.carservice.model.enums.CarStatus;
import lombok.*;

import java.util.List;

/**
 * Response DTO representing a car returned by the API.
 * This object includes basic car information along with related user and service data.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {

    private String id;
    private String licensePlate;
    private String model;
    private String brand;
    private String userId;
    private CarStatus status;
    private UserResponse user;
    private List<ServiceResponse> serviceList;

}
