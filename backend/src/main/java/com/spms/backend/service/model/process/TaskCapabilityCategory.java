package com.spms.backend.service.model.process;

import java.util.Arrays;

public enum TaskCapabilityCategory {

    USER("user"),
    MESSAGING("message"),
    UNKNOWN("unknown");
    private final String prefix;

    private static final String CATEGORY_PREFIX = "spms:tasks:";

    public String getPrefix() {
        return prefix;
    }

    TaskCapabilityCategory(String prefix) {
        this.prefix = CATEGORY_PREFIX + prefix;
    }

    public static TaskCapabilityCategory of(String name) {
        return Arrays.stream(values()).filter(x -> name.indexOf(x.prefix + ":") == 0)
                .findFirst().orElse(UNKNOWN);
    }
}