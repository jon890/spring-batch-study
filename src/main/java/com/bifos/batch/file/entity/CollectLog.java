package com.bifos.batch.file.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class CollectLog extends SystemLog {
    private String dumpType;
    private String processId;
    private String dumpPath;
}
