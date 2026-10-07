package com.nepbrain.technology.employee.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public final class NepaliDateConverter {

    /** Anchor: BS 2000-09-17 == AD 1944-01-01 (from official Nepali date picker library). */
    private static final int ANCHOR_BS_YEAR  = 2000;
    private static final int ANCHOR_BS_MONTH = 9;
    private static final int ANCHOR_BS_DAY   = 17;
    private static final LocalDate ANCHOR_AD = LocalDate.of(1944, 1, 1);

    /**
     * BS month days table: year → [12 month lengths].
     * Complete dataset for years 2000-2100 BS (1943-2043 AD).
     */
    private static final Map<Integer, int[]> BS_MONTH_DAYS = new LinkedHashMap<>();
    static {
        // Accurate BS month data from official Nepali date picker library
        // Source: https://nepalidatepicker.sajanmaharjan.com.np/v5/nepali.datepicker/js/nepali.datepicker.v5.0.6.min.js
        BS_MONTH_DAYS.put(1970, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1971, new int[] {31,31,32,31,32,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1972, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(1973, new int[] {30,32,31,32,31,30,30,30,29,30,29,31});
        BS_MONTH_DAYS.put(1974, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1975, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1976, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(1977, new int[] {30,32,31,32,31,31,29,30,29,30,29,31});
        BS_MONTH_DAYS.put(1978, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1979, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1980, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(1981, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(1982, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1983, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1984, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(1985, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(1986, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1987, new int[] {31,32,31,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1988, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(1989, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1990, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1991, new int[] {31,32,31,32,31,30,30,30,29,29,30,30});
        BS_MONTH_DAYS.put(1992, new int[] {31,32,31,32,31,30,30,30,29,30,29,31});
        BS_MONTH_DAYS.put(1993, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1994, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1995, new int[] {31,32,31,32,31,30,30,30,29,29,30,30});
        BS_MONTH_DAYS.put(1996, new int[] {31,32,31,32,31,30,30,30,29,30,29,31});
        BS_MONTH_DAYS.put(1997, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1998, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(1999, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2000, new int[] {30,32,31,32,31,30,30,30,29,30,29,31});
        BS_MONTH_DAYS.put(2001, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2002, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2003, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2004, new int[] {30,32,31,32,31,30,30,30,29,30,29,31});
        BS_MONTH_DAYS.put(2005, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2006, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2007, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2008, new int[] {31,31,31,32,31,31,29,30,30,29,29,31});
        BS_MONTH_DAYS.put(2009, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2010, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2011, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2012, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2013, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2014, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2015, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2016, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2017, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2018, new int[] {31,32,31,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2019, new int[] {31,32,31,32,31,30,30,30,29,30,29,31});
        BS_MONTH_DAYS.put(2020, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2021, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2022, new int[] {31,32,31,32,31,30,30,30,29,29,30,30});
        BS_MONTH_DAYS.put(2023, new int[] {31,32,31,32,31,30,30,30,29,30,29,31});
        BS_MONTH_DAYS.put(2024, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2025, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2026, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2027, new int[] {30,32,31,32,31,30,30,30,29,30,29,31});
        BS_MONTH_DAYS.put(2028, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2029, new int[] {31,31,32,31,32,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2030, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2031, new int[] {30,32,31,32,31,30,30,30,29,30,29,31});
        BS_MONTH_DAYS.put(2032, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2033, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2034, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2035, new int[] {30,32,31,32,31,31,29,30,30,29,29,31});
        BS_MONTH_DAYS.put(2036, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2037, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2038, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2039, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2040, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2041, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2042, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2043, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2044, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2045, new int[] {31,32,31,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2046, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2047, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2048, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2049, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2050, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2051, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2052, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2053, new int[] {31,32,31,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2054, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2055, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2056, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2057, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2058, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2059, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2060, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2061, new int[] {31,32,31,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2062, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2063, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2064, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2065, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2066, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2067, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2068, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2069, new int[] {31,32,31,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2070, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2071, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2072, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2073, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2074, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2075, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2076, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2077, new int[] {31,32,31,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2078, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2079, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2080, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2081, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2082, new int[] {31,32,31,32,31,31,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2083, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2084, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2085, new int[] {31,32,31,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2086, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2087, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2088, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2089, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2090, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2091, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2092, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2093, new int[] {31,32,31,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2094, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2095, new int[] {31,31,31,32,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2096, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2097, new int[] {31,31,32,32,31,30,30,29,30,29,30,30});
        BS_MONTH_DAYS.put(2098, new int[] {31,32,31,32,31,30,30,30,29,29,30,31});
        BS_MONTH_DAYS.put(2099, new int[] {31,31,31,32,31,31,29,30,30,29,30,30});
        BS_MONTH_DAYS.put(2100, new int[] {31,31,32,31,31,31,30,29,30,29,30,30});
    }

    // ----- Weekday Utilities -----
    
    /** English weekday names */
    private static final String[] ENGLISH_WEEKDAYS = {
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };
    
    /** Nepali weekday names */
    private static final String[] NEPALI_WEEKDAYS = {
        "आइतबार", "सोमबार", "मङ्गलबार", "बुधबार", "बिहिबार", "शुक्रबार", "शनिबार"
    };
    
    /** Short English weekday names */
    private static final String[] ENGLISH_WEEKDAYS_SHORT = {
        "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };
    
    /** Short Nepali weekday names */
    private static final String[] NEPALI_WEEKDAYS_SHORT = {
        "आइत", "सोम", "मङ्गल", "बुध", "बिहि", "शुक्र", "शनि"
    };

    /**
     * Get English weekday name for a given day of week.
     */
    public static String getEnglishWeekday(DayOfWeek dayOfWeek) {
        return ENGLISH_WEEKDAYS[dayOfWeek.getValue() % 7];
    }
    
    /**
     * Get Nepali weekday name for a given day of week.
     */
    public static String getNepaliWeekday(DayOfWeek dayOfWeek) {
        return NEPALI_WEEKDAYS[dayOfWeek.getValue() % 7];
    }
    
    /**
     * Get short English weekday name for a given day of week.
     */
    public static String getEnglishWeekdayShort(DayOfWeek dayOfWeek) {
        return ENGLISH_WEEKDAYS_SHORT[dayOfWeek.getValue() % 7];
    }
    
    /**
     * Get short Nepali weekday name for a given day of week.
     */
    public static String getNepaliWeekdayShort(DayOfWeek dayOfWeek) {
        return NEPALI_WEEKDAYS_SHORT[dayOfWeek.getValue() % 7];
    }
    
    /**
     * Get weekday information for a given AD date.
     */
    public static WeekdayInfo getWeekdayInfo(LocalDate adDate) {
        DayOfWeek dayOfWeek = adDate.getDayOfWeek();
        return new WeekdayInfo(
            dayOfWeek,
            getEnglishWeekday(dayOfWeek),
            getNepaliWeekday(dayOfWeek),
            getEnglishWeekdayShort(dayOfWeek),
            getNepaliWeekdayShort(dayOfWeek)
        );
    }

    /**
     * Get number of days in a BS month.
     */
    public static int getDaysInBsMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid BS month: " + month);
        }
        return getBsMonthLength(year, month);
    }

    // ----- Public API -----

    /** Get current BS year */
    public static int getCurrentBsYear() {
        LocalDate today = LocalDate.now();
        BsDate currentBs = adToBs(today);
        return currentBs.year;
    }

    /** Get current BS date */
    public static BsDate getCurrentBsDate() {
        LocalDate today = LocalDate.now();
        return adToBs(today);
    }

    /** Convert a BS date to AD date. Inputs are 1-based (month/day). */
    public static LocalDate bsToAd(int bsYear, int bsMonth, int bsDay) {
        validateBsDate(bsYear, bsMonth, bsDay);

        // Days from anchor BS date to target BS date
        int daysOffset = daysBetweenBs(
                ANCHOR_BS_YEAR, ANCHOR_BS_MONTH, ANCHOR_BS_DAY,
                bsYear, bsMonth, bsDay);

        return ANCHOR_AD.plusDays(daysOffset);
    }

    /** Convert an AD date to BS date. */
    public static BsDate adToBs(LocalDate ad) {
        // Days offset from AD anchor
        int daysFromAnchor = (int) (ad.toEpochDay() - ANCHOR_AD.toEpochDay());
        // Walk forward or backward over BS table from the anchor
        int y = ANCHOR_BS_YEAR, m = ANCHOR_BS_MONTH, d = ANCHOR_BS_DAY;

        if (daysFromAnchor >= 0) {
            while (daysFromAnchor > 0) {
                int daysInMonth = getBsMonthLength(y, m);
                int remainingInMonth = daysInMonth - d;
                if (daysFromAnchor <= remainingInMonth) {
                    d += daysFromAnchor;
                    daysFromAnchor = 0;
                } else {
                    daysFromAnchor -= (remainingInMonth + 1);
                    d = 1;
                    m++;
                    if (m > 12) { m = 1; y++; }
                }
            }
        } else { // walk backwards
            daysFromAnchor = -daysFromAnchor;
            while (daysFromAnchor > 0) {
                if (d > 1) {
                    int step = Math.min(d - 1, daysFromAnchor);
                    d -= step;
                    daysFromAnchor -= step;
                } else {
                    // go to previous month
                    m--;
                    if (m < 1) { m = 12; y--; }
                    int daysInPrevMonth = getBsMonthLength(y, m);
                    d = daysInPrevMonth;
                    daysFromAnchor -= 1; // we moved one day
                }
            }
        }
        return new BsDate(y, m, d);
    }

    // ----- Helpers -----

    private static int daysBetweenBs(int y1, int m1, int d1, int y2, int m2, int d2) {
        if (compareBs(y1, m1, d1, y2, m2, d2) == 0) return 0;
        int sign = compareBs(y1, m1, d1, y2, m2, d2) < 0 ? 1 : -1;

        int y = y1, m = m1, d = d1, days = 0;
        while (y != y2 || m != m2 || d != d2) {
            if (sign > 0) {
                // step forward
                d++;
                int dim = getBsMonthLength(y, m);
                if (d > dim) { d = 1; m++; if (m > 12) { m=1; y++; } }
                days++;
            } else {
                // step backward
                d--;
                if (d < 1) { m--; if (m < 1) { m=12; y--; } d = getBsMonthLength(y, m); }
                days++;
            }
        }
        return sign * days;
    }

    private static int compareBs(int y1, int m1, int d1, int y2, int m2, int d2) {
        if (y1 != y2) return Integer.compare(y1, y2);
        if (m1 != m2) return Integer.compare(m1, m2);
        return Integer.compare(d1, d2);
    }

    private static int getBsMonthLength(int year, int month1Based) {
        int[] months = BS_MONTH_DAYS.get(year);
        if (months == null) throw new IllegalArgumentException("BS year not in dataset: " + year);
        return months[month1Based - 1];
    }

    private static void validateBsDate(int y, int m, int d) {
        if (m < 1 || m > 12) throw new IllegalArgumentException("Invalid BS month: " + m);
        int dim = getBsMonthLength(y, m);
        if (d < 1 || d > dim) {
            throw new IllegalArgumentException("Invalid BS day: " + d + " for " + y + "-" + m + " (max " + dim + ")");
        }
    }

    // ----- Simple BS date record -----
    public static final class BsDate {
        public final int year, month, day;
        public final WeekdayInfo weekdayInfo;
        
        public BsDate(int year, int month, int day) { 
            this.year = year; 
            this.month = month; 
            this.day = day;
            // Calculate weekday from AD equivalent
            LocalDate adDate = bsToAd(year, month, day);
            this.weekdayInfo = getWeekdayInfo(adDate);
        }
        
        @Override public String toString() { 
            return year + "-" + pad(month) + "-" + pad(day) + " (" + weekdayInfo.englishWeekday + ")"; 
        }
        
        private static String pad(int n) { return n < 10 ? "0"+n : ""+n; }
    }
    
    // ----- Weekday information record -----
    public static final class WeekdayInfo {
        public final DayOfWeek dayOfWeek;
        public final String englishWeekday;
        public final String nepaliWeekday;
        public final String englishWeekdayShort;
        public final String nepaliWeekdayShort;
        
        public WeekdayInfo(DayOfWeek dayOfWeek, String englishWeekday, String nepaliWeekday, 
                          String englishWeekdayShort, String nepaliWeekdayShort) {
            this.dayOfWeek = dayOfWeek;
            this.englishWeekday = englishWeekday;
            this.nepaliWeekday = nepaliWeekday;
            this.englishWeekdayShort = englishWeekdayShort;
            this.nepaliWeekdayShort = nepaliWeekdayShort;
        }
        
        @Override
        public String toString() {
            return englishWeekday + " (" + nepaliWeekday + ")";
        }
    }

    // ----- Demo -----
    public static void main(String[] args) {
        // Test the corrected conversion
        LocalDate ad = bsToAd(2046, 7, 2); // Should be 1989-10-18
        WeekdayInfo weekdayInfo = getWeekdayInfo(ad);
        System.out.println("BS 2046-07-02 => AD " + ad + " (" + weekdayInfo + ")");

        // Test reverse conversion
        BsDate bs = adToBs(LocalDate.of(1989, 10, 18));
        System.out.println("AD 1989-10-18 => BS " + bs);

        // Test anchor date
        LocalDate anchorAd = bsToAd(2000, 9, 17);
        WeekdayInfo anchorWeekday = getWeekdayInfo(anchorAd);
        System.out.println("BS 2000-09-17 => AD " + anchorAd + " (" + anchorWeekday + ") (should be 1944-01-01)");

        // Current BS date
        BsDate currentBs = getCurrentBsDate();
        System.out.println("Current BS date: " + currentBs);
        System.out.println("Current BS year: " + getCurrentBsYear());
        
        // Test weekday conversion for today
        LocalDate today = LocalDate.now();
        WeekdayInfo todayWeekday = getWeekdayInfo(today);
        System.out.println("Today (" + today + ") is " + todayWeekday);
    }
}
