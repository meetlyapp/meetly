package dev.lisek.meetly.backend

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun correctDate(dateStr: String): Pair<String, Boolean> {
    return try {
        val inputDate = LocalDate.parse(dateStr, formatter)
        val deadline = LocalDate.now().minusYears(13).plusDays(1)
        Pair(inputDate.format(formatter), inputDate.isBefore(deadline))
    } catch (_: DateTimeParseException) {
        Pair(dateStr, false)
    }
}