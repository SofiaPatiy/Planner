package com.gmail.sofiapatiy.data.model.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PlannerUserFirebase(
    val username: String,
    val password: String,
    val email: String,
) {
    constructor() : this("", "", "")
}