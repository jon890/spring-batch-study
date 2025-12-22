package com.bifos.batch.file.entity;

import lombok.Data;

@Data
public class SystemFailure {

    private String errorId;
    private String errorDataTime;
    private String severity;
    private Integer processId;
    private String errorMessage;
}
