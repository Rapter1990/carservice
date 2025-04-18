package com.example.carservice.carservice.model.dto.request.car;

import com.example.carservice.carservice.model.enums.CarStatus;
import com.example.carservice.carservice.utils.annotation.ValidTurkishPlate;
import lombok.*;

/**
 * Request object used to update an existing car.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCarRequest {

    private String model;
    private String brand;

    @ValidTurkishPlate
    private String licensePlate;

    private CarStatus status;

    private String userId;

}
