package com.opendatajungle.reference.data.api.shared.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateUtils {
    private DateUtils() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
