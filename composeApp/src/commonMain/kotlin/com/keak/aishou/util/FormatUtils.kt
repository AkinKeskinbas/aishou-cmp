package com.keak.aishou.util

object FormatUtils {

    fun formatLaunchCount(count: Long): String {
        return when {
            count <= 99 -> count.toString()
            count <= 999 -> "99+"
            count <= 9999 -> "${count / 1000}k+"
            count <= 999999 -> "${count / 1000}k+"
            else -> "${count / 1000000}M+"
        }
    }

    fun formatDaysActive(days: Long): String {
        return when {
            days <= 99 -> "$days days"
            days <= 365 -> "99+ days"
            days <= 730 -> "1 year+"
            else -> "${days / 365} years+"
        }
    }
}