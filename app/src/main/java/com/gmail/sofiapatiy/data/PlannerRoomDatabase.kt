package com.gmail.sofiapatiy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gmail.sofiapatiy.data.dao.TaskDao
import com.gmail.sofiapatiy.data.dao.UserDao
import com.gmail.sofiapatiy.data.model.db.PlannerTaskModel
import com.gmail.sofiapatiy.data.model.db.PlannerUserModel

@Database(
    entities = [
        PlannerTaskModel::class,
        PlannerUserModel::class],
    version = 1,
    exportSchema = false
)
abstract class PlannerRoomDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getTaskDao(): TaskDao
}