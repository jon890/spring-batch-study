package com.bifos.batch.file.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogEntry {

    private String threadNum;
    private String message;
}
