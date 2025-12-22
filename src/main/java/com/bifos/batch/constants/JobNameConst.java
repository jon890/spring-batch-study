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
}
