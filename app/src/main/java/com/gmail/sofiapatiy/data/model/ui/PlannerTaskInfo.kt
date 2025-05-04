package com.gmail.sofiapatiy.data.model.ui

import android.os.Parcelable
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.ktx.toUIDateTimeFormattedString
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class PlannerTaskInfo(
    val firebaseKey: String? = null, // will be set later
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
}