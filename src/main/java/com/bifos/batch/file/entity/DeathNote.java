package com.bifos.batch.file.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeathNote {
    private String victimId;
    private String victimName;
    private String executionDate;
    private String causeOfDeath;
}
