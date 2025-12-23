package com.bifos.batch.file.mapper;

import com.bifos.batch.file.entity.ErrorLog;
import com.bifos.batch.file.entity.SystemLog;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

public class ErrorFieldSetMapper implements FieldSetMapper<SystemLog> {
    @Override
    public SystemLog mapFieldSet(FieldSet fs) throws BindException {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setType(fs.readString("type"));
        errorLog.setApplication(fs.readString("application"));
        errorLog.setErrorType(fs.readString("errorType"));
        errorLog.setTimestamp(fs.readString("timestamp"));
        errorLog.setMessage(fs.readString("message"));
        errorLog.setResourceUsage(fs.readString("resourceUsage"));
        errorLog.setLogPath(fs.readString("logPath"));
        return errorLog;
    }
}