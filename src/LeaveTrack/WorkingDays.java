package LeaveTrack;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class WorkingDays {
    /**
     * @param year the year of which we want to get the Orthodox Easter date
     * @return the date of the Orthodox easter of the given year
     */
    private static LocalDate getOrthodoxEaster(int year) {
        LocalDate oed;

        int r1 = year % 4;
        int r2 = year % 7;
        int r3 = year % 19;
        int r4 = (19 * r3 + 15) % 30;
        int r5 = (2 * r1 + 4 * r2 + 6 * r4 + 6) % 7;
        int days = r5 + r4 + 13;

        if (days > 39) {
            days = days - 39;
            oed = LocalDate.of(year, 5, days);
        } else if (days > 9) {
            days = days - 9;
            oed = LocalDate.of(year, 4, days);
        } else {
            days = days + 22;
            oed = LocalDate.of(year, 3, days);
        }
        return oed;
    }

    private static Set<LocalDate> getBankHolidays(int year) {
        Set<LocalDate> holidays = new HashSet<>();
        LocalDate newYearEve = LocalDate.of(year, 1, 1);
        LocalDate epiphany = LocalDate.of(year, 1, 6);
        LocalDate easter = getOrthodoxEaster(year);
        LocalDate cleanMonday = easter.minusDays(48);
        LocalDate independenceDay = LocalDate.of(year, 3, 25);
        LocalDate goodFriday = easter.minusDays(2);
        LocalDate easterMonday = easter.plusDays(1);
        LocalDate labourDay = LocalDate.of(year, 5, 1);
        LocalDate whitMonday = easter.plusDays(50);
        LocalDate assumption = LocalDate.of(year, 8, 15);
        LocalDate ochiDay = LocalDate.of(year, 10, 28);
        LocalDate christmas = LocalDate.of(year, 12, 25);
        LocalDate glorifying = LocalDate.of(year, 12, 26);
        holidays.add(newYearEve);
        holidays.add(epiphany);
        holidays.add(cleanMonday);
        holidays.add(independenceDay);
        holidays.add(goodFriday);
        holidays.add(easterMonday);
        holidays.add(labourDay);
        holidays.add(whitMonday);
        holidays.add(assumption);
        holidays.add(ochiDay);
        holidays.add(christmas);
        holidays.add(glorifying);

        return holidays;
    }

    public static int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        int workDays = 0;
        Set<LocalDate> nonWorkingDays;

        nonWorkingDays = getBankHolidays(startDate.getYear());
        do {
            if (startDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    startDate.getDayOfWeek() != DayOfWeek.SUNDAY &&
                    !nonWorkingDays.contains(startDate))
                workDays++;
            startDate = startDate.plusDays(1);
        } while (startDate.isBefore(endDate) || startDate.isEqual(endDate));


        return workDays;
    }
}
