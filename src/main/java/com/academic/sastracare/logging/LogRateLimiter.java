package com.academic.sastracare.logging;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class LogRateLimiter {

    private static final ConcurrentHashMap<String, Instant> lastLog =
            new ConcurrentHashMap<>();

    private static final long WINDOW_SECONDS = 60;

    public static boolean shouldLog(String key) {

        Instant now = Instant.now();
        Instant previous = lastLog.get(key);

        if (previous == null ||
                now.getEpochSecond() - previous.getEpochSecond() > WINDOW_SECONDS) {

            lastLog.put(key, now);
            return true;
        }

        return false;
    }
}