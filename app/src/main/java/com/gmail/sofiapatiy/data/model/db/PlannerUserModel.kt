package com.gmail.sofiapatiy.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "planner_user"
)
data class PlannerUserModel(
    @PrimaryKey(autoGenerate = true) val userId: Long,
    val username: String,
    val password: String,
    val email: String,
)