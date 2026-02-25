package com.bifos.batch.step.faulttolerant;

public class TerminationFailedException extends RuntimeException {
    public TerminationFailedException(String message) {
        super(message);
    }
}