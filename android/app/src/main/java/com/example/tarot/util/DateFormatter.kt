package com.example.tarot.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val readingDateFormatter =
    DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.forLanguageTag("ru"))

fun formatReadingDate(isoDate: String): String {
    val zoned = runCatching {
        OffsetDateTime.parse(isoDate).atZoneSameInstant(ZoneId.systemDefault())
    }.getOrNull() ?: runCatching {
        LocalDateTime.parse(isoDate).atZone(ZoneId.systemDefault())
    }.getOrNull()

    return zoned?.format(readingDateFormatter) ?: isoDate.take(16).replace('T', ' ')
}
