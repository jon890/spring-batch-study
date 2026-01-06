package com.bifos.batch.file.practice.type;

public enum LogLevel {
    INFO, WARN, ERROR, DEBUG, UNKNOWN;

    public static LogLevel fromString(String level) {
        if (level == null || level.trim().isEmpty()) {
            return UNKNOWN;
        }
        try {
            return valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
