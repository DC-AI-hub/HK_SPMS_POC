package com.spms.backend.service.model.process;

import java.util.Arrays;


public enum EventCapabilityCategory {

    USER("user"),
    UNKNOWN("unknown");
    private static final String CATEGORY_PREFIX = "spms:events:";

    private final String prefix;

    public String getPrefix() {
        return prefix;
    }

    EventCapabilityCategory(String prefix) {
        this.prefix = CATEGORY_PREFIX + prefix;
    }

    public static EventCapabilityCategory of(String name) {
        return Arrays.stream(values()).filter(x -> name.indexOf(x.prefix + ":") == 0)
                .findFirst().orElse(UNKNOWN);
    }

}
