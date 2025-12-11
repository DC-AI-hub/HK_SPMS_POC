package com.spms.backend.repository.entities.process;

import java.util.Arrays;

/**
 * Enum representing the status of a process version.
 */
public enum ProcessVersionStatus {
    /**
     * The process version is in draft state and not yet approved.
     */
    DRAFT,
    
    /**
     * The process version has been approved and is ready for use.
     */
    APPROVED,
    
    /**
     * The process version is deprecated and should not be used.
     */
    DEPRECATED,
    /**
     * The Process version has been deployed.
     */
    DEPLOYED;

    public static ProcessVersionStatus of(String name){
        return Arrays.stream(values()).filter(x->x.name().equals(name)).findFirst().orElse(DRAFT);
    }


}
