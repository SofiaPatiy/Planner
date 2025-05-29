package com.gmail.sofiapatiy.data.model.firebase

import android.graphics.Color
import android.os.Parcelable
import com.gmail.sofiapatiy.AppConstants.Companion.HIGH_URGENCY
import com.gmail.sofiapatiy.AppConstants.Companion.LOW_URGENCY
import com.gmail.sofiapatiy.AppConstants.Companion.MEDIUM_URGENCY
import kotlinx.parcelize.Parcelize

sealed class Urgency(val level: String, val colorId: Int, val sortPriority:Int) : Parcelable {

    companion object {
        fun getUrgencyByLevel(level: String): Urgency? {
            return when (level) {
                LOW_URGENCY -> Low
                MEDIUM_URGENCY -> Medium
                HIGH_URGENCY -> High
                else -> null
            }
        }
    }

    @Parcelize
    data object Low : Urgency(LOW_URGENCY, Color.GREEN, 2)

    @Parcelize
    data object Medium : Urgency(MEDIUM_URGENCY, Color.YELLOW, 1)

    @Parcelize
    data object High : Urgency(HIGH_URGENCY, Color.RED, 0)
}