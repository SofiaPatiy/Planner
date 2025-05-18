package com.gmail.sofiapatiy.data.convertors

import com.gmail.sofiapatiy.data.model.db.PlannerTaskModel
import com.gmail.sofiapatiy.data.model.db.PlannerUserModel
import com.gmail.sofiapatiy.data.model.firebase.PlannerTaskFirebase
import com.gmail.sofiapatiy.data.model.firebase.PlannerUserFirebase
import com.gmail.sofiapatiy.data.model.firebase.Urgency
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.data.model.ui.PlannerUserInfo
import com.gmail.sofiapatiy.ktx.asBase64Encoded
import com.gmail.sofiapatiy.ktx.toFirebaseFormattedString
import com.gmail.sofiapatiy.ktx.toLocalDateTime

fun PlannerUserFirebase.toPlannerUserModel() =
    PlannerUserModel(
        userId = 0,
        username = username,
        password = password,
        email = email
    )

fun PlannerUserModel.toPlannerUserInfo() =
    PlannerUserInfo(
        username = username,
        password = password,
        email = email
    )

fun PlannerUserInfo.toPlannerUserFirebase() =
    PlannerUserFirebase(
        username = username,
        password = password.asBase64Encoded(),
        email = email
    )

fun PlannerTaskModel.toPlannerTaskInfo() =
    PlannerTaskInfo(
        databaseTaskId = databaseTaskId,
        taskFirebaseKey = taskFirebaseKey,
        userId = userId,
        name = name,
        note = note,
        urgency = Urgency.getUrgencyByLevel(urgency) ?: Urgency.Low,
        isRegular = isRegular,
        reminder = reminder.toLocalDateTime(),
        timeOfCompletion = timeOfCompletion.toLocalDateTime(),
        timeOfCreation = timeOfCreation.toLocalDateTime(),
        timeOfDeadline = timeOfDeadline.toLocalDateTime()
    )

fun PlannerTaskFirebase.toPlannerTaskModel(key: String?) =
    PlannerTaskModel(
        databaseTaskId = 0L, // auto-incremented by Room DB
        taskFirebaseKey = key,
        userId = userId,
        name = name,
        note = note,
        urgency = urgency,
        isRegular = isRegular,
        reminder = reminder,
        timeOfCompletion = timeOfCompletion,
        timeOfCreation = timeOfCreation,
        timeOfDeadline = timeOfDeadline
    )

fun PlannerTaskInfo.toPlannerTaskFirebase() =
    PlannerTaskFirebase(
        userId = userId,
        name = name,
        note = note,
        urgency = urgency.level,
        isRegular = isRegular,
        reminder = reminder.toFirebaseFormattedString(),
        timeOfCompletion = timeOfCompletion.toFirebaseFormattedString(),
        timeOfCreation = timeOfCreation.toFirebaseFormattedString(),
        timeOfDeadline = timeOfDeadline.toFirebaseFormattedString()
    )