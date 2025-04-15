package com.example.carservice.carservice.model.dto.request.car;

import com.example.carservice.carservice.model.enums.CarStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCarRequest {

    private String model;
    private String brand;
    private String licensePlate;
    private CarStatus status;

}
