package com.gmail.sofiapatiy.data.model.ui

import android.os.Parcelable
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.ktx.asNormalizedDateTime
import com.gmail.sofiapatiy.ktx.toUIDateTimeFormattedString
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class PlannerTaskInfo(
    val taskFirebaseKey: String? = null, // will be set later
    val databaseTaskId: Long,
    val userId: String,
    val name: String,
    val note: String,
    val reminder: LocalDateTime,
    val timeOfDeadline: LocalDateTime,
    val timeOfCompletion: LocalDateTime,
    val timeOfCreation: LocalDateTime,
    val isRegular: Boolean,
    val urgency: Urgency,
) : Parcelable {

    @IgnoredOnParcel
    val formattedCreationDateTime = timeOfCreation.toUIDateTimeFormattedString()

    @IgnoredOnParcel
    val formattedDeadlineDateTime = timeOfDeadline.toUIDateTimeFormattedString()

    @IgnoredOnParcel
    val normalizedDeadlineDateTime = timeOfDeadline.asNormalizedDateTime()
}

fun Iterable<PlannerTaskInfo>.getDailyUrgency() =
    when (any { it.urgency == Urgency.High }) {
        true -> Urgency.High
        else ->
            when (any { it.urgency == Urgency.Medium }) {
                true -> Urgency.Medium
                else ->
                    when (any { it.urgency == Urgency.Low }) {
                        true -> Urgency.Low
                        else -> null
                    }
            }
    }