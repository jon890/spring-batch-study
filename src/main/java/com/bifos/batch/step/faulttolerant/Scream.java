package com.bifos.batch.step.faulttolerant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Scream {
    private int id;
    private String scream;
    private String processMsg;


    @Override
    public String toString() {
        return id + "_" + scream;
    }
}

