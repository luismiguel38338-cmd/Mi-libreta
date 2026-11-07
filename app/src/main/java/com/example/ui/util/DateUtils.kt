package com.example.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val spanishLocale = Locale("es", "DO")
    private val timeFormat = SimpleDateFormat("h:mm a", spanishLocale)
    private val fullDateFormat = SimpleDateFormat("d 'de' MMMM, yyyy", spanishLocale)
    private val shortDateFormat = SimpleDateFormat("d MMM yyyy, h:mm a", spanishLocale)
    private val simpleDateOnlyFormat = SimpleDateFormat("d 'de' MMMM", spanishLocale)
    private val dueDateFormatter = SimpleDateFormat("d 'de' MMM, yyyy", spanishLocale)

    fun formatMovementDate(timestamp: Long): String {
        val now = Calendar.getInstance()
        val itemCal = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isToday = now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)

        val isYesterday = now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - itemCal.get(Calendar.DAY_OF_YEAR) == 1

        val timeStr = timeFormat.format(Date(timestamp)).lowercase(spanishLocale)

        return when {
            isToday -> "Hoy, $timeStr"
            isYesterday -> "Ayer, $timeStr"
            now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) -> {
                "${simpleDateOnlyFormat.format(Date(timestamp))}, $timeStr"
            }
            else -> shortDateFormat.format(Date(timestamp))
        }
    }

    fun formatClientCreatedDate(timestamp: Long): String {
        return fullDateFormat.format(Date(timestamp))
    }

    fun formatDueDate(timestamp: Long): String {
        val now = Calendar.getInstance()
        val dueCal = Calendar.getInstance().apply { timeInMillis = timestamp }

        val diffDays = (dueCal.timeInMillis - now.timeInMillis) / (1000 * 60 * 60 * 24)

        return when {
            diffDays == 0L -> "Vence hoy"
            diffDays == 1L -> "Vence mañana"
            diffDays == -1L -> "Venció ayer"
            diffDays < -1L -> "Venció hace ${-diffDays} días"
            else -> "Vence el ${dueDateFormatter.format(Date(timestamp))}"
        }
    }

    fun getStartOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getStartOfMonth(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
