package dev.lisek.meetly.backend.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/**
 * Corrects the date input and checks whether it's at least 13 years ago.
 * 
 * @param [dateStr] date to correct.
 * @return corrected date and whether it is at least 13 years ago.
 */
@Deprecated("Manual date input will be removed in near future")
fun correctDate(dateStr: String): Pair<String, Boolean> {
    return try {
        val inputDate = LocalDate.parse(dateStr, formatter)
        val deadline = LocalDate.now().minusYears(13).plusDays(1)
        Pair(inputDate.format(formatter), inputDate.isBefore(deadline))
    } catch (_: DateTimeParseException) {
        Pair(dateStr, false)
    }
}
