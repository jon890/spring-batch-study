package com.bifos.batch.file.practice.entity;

import com.bifos.batch.file.practice.type.LogLevel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessedLogEntry {
    private LocalDateTime dateTime;
    private LogLevel level;
    private String message;
    private String errorCode;
}
