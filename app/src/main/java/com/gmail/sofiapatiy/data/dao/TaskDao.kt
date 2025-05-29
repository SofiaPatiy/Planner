package com.gmail.sofiapatiy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gmail.sofiapatiy.data.model.db.PlannerTaskModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<PlannerTaskModel>)

    @Query("SELECT * FROM planner_task")
    fun getAllTasks(): Flow<List<PlannerTaskModel>>

    @Query("SELECT * FROM planner_task WHERE databaseTaskId=:taskId")
    fun getTaskById(taskId: Long): Flow<PlannerTaskModel?>

    @Query("DELETE FROM planner_task")
    suspend fun deleteAllTasks()

    @Transaction
    suspend fun replaceAllTasks(tasks: List<PlannerTaskModel>) {
        deleteAllTasks()
        insertTasks(tasks)
    }
}