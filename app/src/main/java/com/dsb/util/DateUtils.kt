package com.dsb.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

fun startOfCurrentWeek(startDay: DayOfWeek = DayOfWeek.MONDAY): Long {
    val weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(startDay))
    return weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun weeksBetween(startMillis: Long, endMillis: Long, startDay: DayOfWeek = DayOfWeek.MONDAY): Long {
    val startDate = Instant.ofEpochMilli(startMillis)
        .atZone(ZoneId.systemDefault()).toLocalDate()
        .with(TemporalAdjusters.previousOrSame(startDay))
    val endDate = Instant.ofEpochMilli(endMillis)
        .atZone(ZoneId.systemDefault()).toLocalDate()
        .with(TemporalAdjusters.previousOrSame(startDay))
    return ChronoUnit.WEEKS.between(startDate, endDate) + 1
}

fun formatDate(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
}

fun formatShortDate(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("MMM d"))
}
