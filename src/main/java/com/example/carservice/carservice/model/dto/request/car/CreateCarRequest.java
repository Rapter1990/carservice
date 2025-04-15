package com.example.carservice.carservice.model.dto.request.car;

import com.example.carservice.carservice.utils.annotation.ValidTurkishPlate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCarRequest {

    @ValidTurkishPlate
    private String licensePlate;
    private String model;
    private String brand;
    private String userId;

}
