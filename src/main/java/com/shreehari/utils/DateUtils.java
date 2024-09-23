package com.shreehari.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    static ZoneId indiaTimeZone = ZoneId.of("Asia/Kolkata");
    public static String getIndiaDateToday() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now(indiaTimeZone);
        return dtf.format(today);
    }

    public static String currentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        return Instant.now().atZone(indiaTimeZone).format(formatter);
    }

    public static String getDateToday() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
        LocalDate today = LocalDate.now(indiaTimeZone);
        return dtf.format(today);
    }

    public Date getYesterdayDate() {
        LocalDate yesterday = LocalDate.now(indiaTimeZone).minusDays(1);
        return Date.from(yesterday.atStartOfDay(indiaTimeZone).toInstant());
    }
}
