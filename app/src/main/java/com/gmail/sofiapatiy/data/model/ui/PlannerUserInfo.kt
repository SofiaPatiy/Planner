package com.gmail.sofiapatiy.data.model.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlannerUserInfo(
    val firebaseKey: String? = null, // will be set later
    val username: String,
    val password: String,
    val email: String,
) : Parcelable