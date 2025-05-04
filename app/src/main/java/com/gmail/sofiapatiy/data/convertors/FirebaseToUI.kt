package com.gmail.sofiapatiy.data.convertors

import com.gmail.sofiapatiy.data.model.firebase.PlannerTaskFirebase
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.ktx.toFirebaseFormattedString
import com.gmail.sofiapatiy.ktx.toLocalDateTime

fun PlannerTaskFirebase.toPlannerTaskInfo(key: String?) =
    PlannerTaskInfo(
        firebaseKey = key,
        name = name,
        note = note,
        urgency = Urgency.getUrgencyByLevel(urgency) ?: Urgency.Low,
        isRegular = isRegular,
        reminder = reminder.toLocalDateTime(),
        timeOfCompletion = timeOfCompletion.toLocalDateTime(),
        timeOfCreation = timeOfCreation.toLocalDateTime(),
        timeOfDeadline = timeOfDeadline.toLocalDateTime()
    )

fun PlannerTaskInfo.toPlannerTaskFirebase() =
    PlannerTaskFirebase(
        name = name,
        note = note,
        urgency = urgency.level,
        isRegular = isRegular,
        reminder = reminder.toFirebaseFormattedString(),
        timeOfCompletion = timeOfCompletion.toFirebaseFormattedString(),
        timeOfCreation = timeOfCreation.toFirebaseFormattedString(),
        timeOfDeadline = timeOfDeadline.toFirebaseFormattedString()
    )