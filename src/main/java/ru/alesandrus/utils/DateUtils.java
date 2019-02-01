package ru.alesandrus.utils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final ZoneId zoneId = ZoneId.of("Europe/Moscow");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm");

    public static Timestamp conertToTimestamp(String date) {
        LocalDateTime localDateTime = getDate(date);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return new Timestamp(zonedDateTime.toInstant().toEpochMilli());
    }

    private static LocalDateTime getDate(String str) {
        LocalDateTime date;
        String[] arr = str.split("\\u00a0|\\s|:");
        if(str.contains("Сегодня")) {
            date = LocalDateTime.of(LocalDate.now(), LocalTime.of(Integer.parseInt(arr[1]), Integer.parseInt(arr[2])));
        } else if (str.contains("Вчера")) {
            date = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(Integer.parseInt(arr[1]), Integer.parseInt(arr[2])));
        } else if (!str.contains(":")) {
            date = LocalDateTime.of(Integer.parseInt(arr[2]), parseMonth(arr[1]), Integer.parseInt(arr[0]), 0, 0);
        } else {
            int currentYear = LocalDate.now().getYear();
            date = LocalDateTime.of(currentYear, parseMonth(arr[1]), Integer.parseInt(arr[0]),
                    Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
        }
        return date;
    }

    private static int parseMonth(String month) {
        switch (month) {
            case "января" : return 1;
            case "февраля" : return 2;
            case "марта" : return 3;
            case "апреля" : return 4;
            case "мая" : return 5;
            case "июня" : return 6;
            case "июля" : return 7;
            case "августа" : return 8;
            case "сентября" : return 9;
            case "октября" : return 10;
            case "ноября" : return 11;
            case "декабря" : return 12;
        }
        return -1;
    }

    public static String getCurrentTime() {
        LocalDateTime localDateTime = LocalDateTime.now(zoneId);
        return localDateTime.format(FORMATTER);
    }
}
