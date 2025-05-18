package com.gmail.sofiapatiy.ktx

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Context.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}

fun DialogFragment.showOnScreen(fragmentManager: FragmentManager) {
    val dialogTag = "com.gmail.sofiapatii.dialog"

    // check if other dialogFragments are visible - could happened on fast multiple button pushes
    if (fragmentManager.fragments.none { it is DialogFragment }) {
        runCatching {
            show(fragmentManager, dialogTag)
        }.onFailure {
            Log.d("planner_dialog_failure", "DialogFragment failure: $it")
        }
    }
}

private val utcTimeZone = ZoneId.ofOffset("UTC", ZoneOffset.UTC)

fun Long.asLocalDateTime(): LocalDateTime =
    Instant
        .ofEpochMilli(this)
        .atZone(utcTimeZone)
        .toLocalDateTime()

fun LocalDateTime.asMilliseconds() =
    ZonedDateTime.of(this, utcTimeZone)
        .toInstant()
        .toEpochMilli()

fun LocalDate.toFormattedMonth(): String =
    this.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))

fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault()))

fun LocalDateTime.toUIDateFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern("EEE dd.MM.yyyy", Locale.getDefault()))

fun LocalDateTime.toUITimeFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))

fun LocalDateTime.toUIDateTimeFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern("EEE dd.MM.yyyy HH:mm", Locale.getDefault()))

fun LocalDateTime.toFirebaseFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault()))

// ---

fun View.setViewAndChildrenEnabled(isEnabled: Boolean) {
    this.isEnabled = isEnabled
    (this as? ViewGroup)?.children?.forEach {
        it.setViewAndChildrenEnabled(isEnabled)
    }
}