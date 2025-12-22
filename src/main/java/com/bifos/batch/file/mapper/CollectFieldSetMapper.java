package com.bifos.batch.file.mapper;

import com.bifos.batch.file.entity.CollectLog;
import com.bifos.batch.file.entity.SystemLog;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

public class CollectFieldSetMapper implements FieldSetMapper<SystemLog> {
    @Override
    public SystemLog mapFieldSet(FieldSet fs) throws BindException {
        CollectLog collectLog = new CollectLog();
        collectLog.setType(fs.readString("type"));
        collectLog.setDumpType(fs.readString("dumpType"));
        collectLog.setProcessId(fs.readString("processId"));
        collectLog.setTimestamp(fs.readString("timestamp"));
        collectLog.setDumpPath(fs.readString("dumpPath"));
        return collectLog;
    }
}