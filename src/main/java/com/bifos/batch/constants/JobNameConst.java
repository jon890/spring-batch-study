package com.bifos.batch.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JobNameConst {

    private static final String DELIMITER = "_";

    private static final String LISTENER_EXAMPLE = "listener";

    private static final String LISTENER_SYSTEM_INFILTRATION = LISTENER_EXAMPLE + DELIMITER + "systemInfiltration";
    public static final String LISTENER_SYSTEM_INFILTRATION_JOB = LISTENER_SYSTEM_INFILTRATION + DELIMITER + "job";
    public static final String LISTENER_SYSTEM_INFILTRATION_RECON_STEP = LISTENER_SYSTEM_INFILTRATION + DELIMITER + "reconStep";
    public static final String LISTENER_SYSTEM_INFILTRATION_ATTACK_STEP = LISTENER_SYSTEM_INFILTRATION + DELIMITER + "attackStep";

    private static final String LISTENER_SYSTEM_TERMINATION = LISTENER_EXAMPLE + DELIMITER + "systemTermination";
    public static final String LISTENER_SYSTEM_TERMINATION_JOB = LISTENER_SYSTEM_TERMINATION + DELIMITER + "job";
    public static final String LISTENER_SYSTEM_TERMINATION_SCANNING_STEP = LISTENER_SYSTEM_TERMINATION + DELIMITER + "scanningStep";
    public static final String LISTENER_SYSTEM_TERMINATION_ELIMINATION_STEP = LISTENER_SYSTEM_TERMINATION + DELIMITER + "eliminationStep";
    public static final String LISTENER_SYSTEM_TERMINATION_ELIMINATION_TASKLET = LISTENER_SYSTEM_TERMINATION + DELIMITER + "eliminationTasklet";

    private static final String LISTENER_SYSTEM_DESTRUCTION = LISTENER_EXAMPLE + DELIMITER + "systemDestruction";
    public static final String LISTENER_SYSTEM_DESTRUCTION_JOB = LISTENER_SYSTEM_DESTRUCTION + DELIMITER + "job";
    public static final String LISTENER_SYSTEM_DESTRUCTION_TERMINATION_STEP = LISTENER_SYSTEM_DESTRUCTION + DELIMITER + "terminationStep";

    private static final String FILE_EXAMPLE = "file";

    private static final String FILE_SYSTEM_FAILURE = FILE_EXAMPLE + DELIMITER + "systemFailure";
    public static final String FILE_SYSTEM_FAILURE_JOB = FILE_SYSTEM_FAILURE + DELIMITER + "job";
    public static final String FILE_SYSTEM_FAILURE_STEP = FILE_SYSTEM_FAILURE + DELIMITER + "step";
    public static final String FILE_SYSTEM_FAILURE_READER = FILE_SYSTEM_FAILURE + DELIMITER + "reader";
    public static final String FILE_SYSTEM_FAILURE_WRITER = FILE_SYSTEM_FAILURE + DELIMITER + "writer";

    private static final String FILE_FIXED_SYSTEM_FAILURE = FILE_EXAMPLE + DELIMITER + "fixedSystemFailure";
    public static final String FILE_FIXED_SYSTEM_FAILURE_JOB = FILE_FIXED_SYSTEM_FAILURE + DELIMITER + "job";
    public static final String FILE_FIXED_SYSTEM_FAILURE_STEP = FILE_FIXED_SYSTEM_FAILURE + DELIMITER + "step";
    public static final String FILE_FIXED_SYSTEM_FAILURE_READER = FILE_FIXED_SYSTEM_FAILURE + DELIMITER + "reader";
    public static final String FILE_FIXED_SYSTEM_FAILURE_WRITER = FILE_FIXED_SYSTEM_FAILURE + DELIMITER + "writer";

    private static final String FILE_LOG_ANALYSIS = FILE_EXAMPLE + DELIMITER + "logAnalysis";
    public static final String FILE_LOG_ANALYSIS_JOB = FILE_LOG_ANALYSIS + DELIMITER + "job";
    public static final String FILE_LOG_ANALYSIS_STEP = FILE_LOG_ANALYSIS + DELIMITER + "step";
    public static final String FILE_LOG_ANALYSIS_READER = FILE_LOG_ANALYSIS + DELIMITER + "reader";
    public static final String FILE_LOG_ANALYSIS_WRITER = FILE_LOG_ANALYSIS + DELIMITER + "writer";

    private static final String FILE_SYSTEM_LOG = FILE_EXAMPLE + DELIMITER + "systemLog";
    public static final String FILE_SYSTEM_LOG_JOB = FILE_SYSTEM_LOG + DELIMITER + "job";
    public static final String FILE_SYSTEM_LOG_STEP = FILE_SYSTEM_LOG + DELIMITER + "step";
    public static final String FILE_SYSTEM_LOG_READER = FILE_SYSTEM_LOG + DELIMITER + "reader";
    public static final String FILE_SYSTEM_LOG_WRITER = FILE_SYSTEM_LOG + DELIMITER + "writer";
}
