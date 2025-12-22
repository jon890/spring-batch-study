package com.bifos.batch.file.mapper;

import com.bifos.batch.file.entity.AbortLog;
import com.bifos.batch.file.entity.SystemLog;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

public class AbortFieldSetMapper implements FieldSetMapper<SystemLog> {
    @Override
    public SystemLog mapFieldSet(FieldSet fs) throws BindException {
        AbortLog abortLog = new AbortLog();
        abortLog.setType(fs.readString("type"));
        abortLog.setApplication(fs.readString("application"));
        abortLog.setErrorType(fs.readString("errorType"));
        abortLog.setTimestamp(fs.readString("timestamp"));
        abortLog.setMessage(fs.readString("message"));
        abortLog.setExitCode(fs.readString("exitCode"));
        abortLog.setProcessPath(fs.readString("processPath"));
        abortLog.setStatus(fs.readString("status"));
        return abortLog;
    }
}