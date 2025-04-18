package com.example.carservice.logging.service;

import com.example.carservice.logging.entity.LogEntity;

public interface LogService {

    void saveLogToDatabase(final LogEntity logEntity);

}
