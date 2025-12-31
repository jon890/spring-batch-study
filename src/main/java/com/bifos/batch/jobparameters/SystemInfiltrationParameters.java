package com.bifos.batch.jobparameters;

import lombok.Data;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@StepScope
@Component
public class SystemInfiltrationParameters {

    private final String operationCommander;
    @Value("#{jobParameters[missionName]}")
    private String missionName;
    private int securityLevel;

    // 생성자 주입
    public SystemInfiltrationParameters(@Value("#{jobParameters[operationCommander]}") String operationCommander) {
        this.operationCommander = operationCommander;
    }

    // setter 주입
    @Value("#{jobParameters[securityLevel]}")
    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }
}
