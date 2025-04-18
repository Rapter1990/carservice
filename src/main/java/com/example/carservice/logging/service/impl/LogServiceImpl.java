package com.example.carservice.logging.service.impl;

import com.example.carservice.logging.entity.LogEntity;
import com.example.carservice.logging.repository.LogRepository;
import com.example.carservice.logging.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Override
    public void saveLogToDatabase(final LogEntity logEntity) {
        logEntity.setTime(LocalDateTime.now());
        logRepository.save(logEntity);
    }

}
