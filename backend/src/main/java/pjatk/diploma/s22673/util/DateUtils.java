package pjatk.diploma.s22673.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class DateUtils {

    private DateUtils() {}

    public static int calculateWorkingDays(LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate date = start;

        while (!date.isAfter(end)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                count++;
            }
            date = date.plusDays(1);
        }
        return count;
    }
}

