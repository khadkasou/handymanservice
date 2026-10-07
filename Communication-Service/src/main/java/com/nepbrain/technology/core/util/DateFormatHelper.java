package com.nepbrain.technology.core.util;

import com.nepbrain.technology.core.payload.response.DateFormatResponse;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Function;

public class DateFormatHelper {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static  <T, D> DateFormatResponse formatDate(T input, Function<T, D> createdAtGetter, Function<T, D> lastModifiedAtGetter) {
        return formatDateWithFormatter(input, createdAtGetter, lastModifiedAtGetter, DEFAULT_FORMATTER);
    }

    public static  <T, D> DateFormatResponse formatDateWithFormatter(T input, Function<T, D> createdAtGetter, Function<T, D> lastModifiedAtGetter,
                                                             DateTimeFormatter formatter) {
        String createdAt = formatDate(createdAtGetter.apply(input), formatter);
        String lastModifiedAt = formatDate(lastModifiedAtGetter.apply(input), formatter);
        return new DateFormatResponse(createdAt, lastModifiedAt);
    }

    private static String formatDate(Object date, DateTimeFormatter formatter) {
        return switch (date) {
            case null -> null;
            case Instant instant -> formatter.format(instant.atZone(ZoneId.systemDefault()));
            case TemporalAccessor temporalAccessor -> formatter.format(temporalAccessor);
            case Date date1 -> formatter.format(date1.toInstant().atZone(ZoneId.systemDefault()));
            default -> throw new IllegalArgumentException("Unsupported date type: " + date.getClass());
        };

    }

    @SafeVarargs
    public  static final <T> Map<Object, String> dateFormatter(List<T> entities, Function<T, ?>... dateExtractors) {

        if (entities == null || entities.isEmpty() || dateExtractors.length == 0) {
            return Collections.emptyMap();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<Object, String> formattedDates = new HashMap<>();

        for (T entity : entities) {
            for (Function<T, ?> extractor : dateExtractors) {
                Object dateObj = extractor.apply(entity);
                if (dateObj != null) {
                    String formatted = formatAnyDate(dateObj, simpleDateFormat);
                    formattedDates.put(dateObj, formatted);
                }
            }
        }

        return formattedDates;
    }

    private  static String formatAnyDate(Object dateObj, SimpleDateFormat sdf) {
        if (dateObj instanceof LocalDateTime ldt) {
            return ldt.format(DateFormatHelper.DEFAULT_FORMATTER);
        } else if (dateObj instanceof LocalDate ld) {
            return ld.atStartOfDay().format(DateFormatHelper.DEFAULT_FORMATTER);
        } else if (dateObj instanceof LocalTime lt) {
            return lt.atDate(LocalDate.of(1970, 1, 1)).format(DateFormatHelper.DEFAULT_FORMATTER);
        } else if (dateObj instanceof Date d) {
            return sdf.format(d);
        } else if (dateObj instanceof Instant i) {
            return DateFormatHelper.DEFAULT_FORMATTER.format(i.atZone(ZoneId.systemDefault()));
        } else if (dateObj instanceof OffsetDateTime odt) {
            return odt.format(DateFormatHelper.DEFAULT_FORMATTER);
        } else if (dateObj instanceof ZonedDateTime zdt) {
            return zdt.format(DateFormatHelper.DEFAULT_FORMATTER);
        }
        return dateObj.toString(); // fallback
    }
}
