package com.gmail.sofiapatiy.data.model.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PlannerTaskFirebase(
    val name: String,
    val note: String,
    val reminder: String,
    val timeOfDeadline: String,
    val timeOfCompletion: String,
    val timeOfCreation: String,
    val isRegular: Boolean,
    val urgency: String,
) {
    constructor() : this("", "", "", "", "", "", false, "")
}