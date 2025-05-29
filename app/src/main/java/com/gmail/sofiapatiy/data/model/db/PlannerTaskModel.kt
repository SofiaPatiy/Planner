package com.gmail.sofiapatiy.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "planner_task"
)
data class PlannerTaskModel(
    @PrimaryKey(autoGenerate = true) val databaseTaskId: Long,
    val taskFirebaseKey: String? = null,
    val userId: String,
    val name: String,
    val note: String,
    val reminder: String,
    val timeOfDeadline: String,
    val timeOfCompletion: String,
    val timeOfCreation: String,
    val isRegular: Boolean,
    val urgency: String,
)