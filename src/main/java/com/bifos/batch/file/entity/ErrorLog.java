package com.bifos.batch.file.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ErrorLog extends SystemLog {
    private String application;
    private String errorType;
    private String message;
    private String resourceUsage;
    private String logPath;
}
