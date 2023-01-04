package com.levi9.socialnetwork.Model;

import java.time.Duration;
import java.time.LocalDateTime;

public enum MuteDuration {
    HOURS_1, HOURS_8, HOURS_24, DAYS_7, DAYS_30, PERMANENT;

    public Boolean isPermanent() {
        return this == PERMANENT;
    }

    public Duration getDuration() {
        Duration duration;
        switch (this) {
        case HOURS_1 -> duration = Duration.ofHours(1);
        case HOURS_8 -> duration = Duration.ofHours(8);
        case HOURS_24 -> duration = Duration.ofHours(24);
        case DAYS_7 -> duration = Duration.ofDays(7);
        case DAYS_30 -> duration = Duration.ofDays(30);
        default -> duration = Duration.ZERO;
        }
        return duration;
    }
}
