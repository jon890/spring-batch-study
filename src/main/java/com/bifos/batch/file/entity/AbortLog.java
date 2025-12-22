package com.bifos.batch.file.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AbortLog extends SystemLog {
    private String application;
    private String errorType;
    private String message;
    private String exitCode;
    private String processPath;
    private String status;
}
