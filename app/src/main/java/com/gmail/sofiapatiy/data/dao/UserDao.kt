package com.gmail.sofiapatiy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gmail.sofiapatiy.data.model.db.PlannerUserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(tasks: List<PlannerUserModel>)

    @Query("SELECT * FROM planner_user")
    fun getAllUsers(): Flow<List<PlannerUserModel>>

    @Query("DELETE FROM planner_user")
    suspend fun deleteAllUsers()

    @Transaction
    suspend fun replaceAllUsers(tasks: List<PlannerUserModel>) {
        deleteAllUsers()
        insertUsers(tasks)
    }
}