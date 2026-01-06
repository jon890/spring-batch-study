package com.bifos.batch.file.practice.entity;

import lombok.Data;

@Data
public class LogEntry {
    private String dateTime;
    private String level;
    private String message;
}