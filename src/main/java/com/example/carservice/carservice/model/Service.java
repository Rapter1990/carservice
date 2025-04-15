package com.example.carservice.carservice.model;

import com.example.carservice.carservice.model.enums.ServiceStatus;
import com.example.carservice.common.model.BaseDomainModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Service extends BaseDomainModel {

    private String id;
    private String title;
    private String description;
    private ServiceStatus status;

}
