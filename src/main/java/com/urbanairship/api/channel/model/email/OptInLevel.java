package com.urbanairship.api.channel.model.email;

import com.google.common.base.Optional;

/**
 * Enum of opt in levels
 */
public enum OptInLevel {

    EMAIL_COMMERCIAL_OPTED_IN("commercial_opted_in"),
    EMAIL_COMMERCIAL_OPTED_OUT("commercial_opted_out"),
    EMAIL_TRANSACTIONAL_OPTED_IN("transactional_opted_in"),
    EMAIL_TRANSACTIONAL_OPTED_OUT("transactional_opted_out"),
    NONE("none");

    private final String identifier;

    private OptInLevel() {
        this(null);
    }

    private OptInLevel(String identifier) {
        this.identifier = identifier;
    }

    public static Optional<OptInLevel> find(String identifier) {
        for (OptInLevel optInLevel : values()) {
            if (optInLevel.getIdentifier().equals(identifier)) {
                return Optional.of(optInLevel);
            }
        }

        return Optional.absent();
    }

    public String getIdentifier() {
        return identifier;
    }

}

