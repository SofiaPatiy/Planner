package com.gmail.sofiapatiy.repository

import com.gmail.sofiapatiy.AppConstants.Companion.DB_URL
import com.gmail.sofiapatiy.AppConstants.Companion.TASKS_NODE
import com.gmail.sofiapatiy.AppConstants.Companion.USERS_NODE
import com.gmail.sofiapatiy.data.convertors.toPlannerTaskFirebase
import com.gmail.sofiapatiy.data.convertors.toPlannerTaskInfo
import com.gmail.sofiapatiy.data.convertors.toPlannerUserFirebase
import com.gmail.sofiapatiy.data.convertors.toPlannerUserInfo
import com.gmail.sofiapatiy.data.dao.TaskDao
import com.gmail.sofiapatiy.data.dao.UserDao
import com.gmail.sofiapatiy.data.model.db.PlannerTaskModel
import com.gmail.sofiapatiy.data.model.db.PlannerUserModel
import com.gmail.sofiapatiy.data.model.ui.PlannerTaskInfo
import com.gmail.sofiapatiy.data.model.ui.PlannerUserInfo
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class PlannerRepository(
    private val userDao: UserDao,
    private val taskDao: TaskDao
) {
    suspend fun refreshUsers(users: List<PlannerUserModel>) {
        userDao.replaceAllUsers(users)
    }

    suspend fun deleteUsers() {
        userDao.deleteAllUsers()
    }

    fun getAllUsers() =
        userDao.getAllUsers().map { users ->
            users.map { it.toPlannerUserInfo() }
        }

    fun getAllTasks() =
        taskDao.getAllTasks().map { tasks ->
            tasks.map { it.toPlannerTaskInfo() }
        }

    fun getTaskById(taskId: Long) =
        taskDao.getTaskById(taskId).map { task ->
            task?.toPlannerTaskInfo()
        }

    suspend fun refreshUserTasks(tasks: List<PlannerTaskModel>) {
        taskDao.replaceAllTasks(tasks)
    }

    suspend fun deleteUserTasks() {
        taskDao.deleteAllTasks()
    }

    fun submitNewUser(
        user: PlannerUserInfo,
        onSuccessListener: OnSuccessListener<Any>,
        onFailureListener: OnFailureListener
    ) {
        val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
        val databaseReference = firebaseDatabase.getReference(USERS_NODE)
        databaseReference.push().setValue(user.toPlannerUserFirebase())
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun submitNewTask(
        task: PlannerTaskInfo,
        onSuccessListener: OnSuccessListener<Any>,
        onFailureListener: OnFailureListener
    ) {
        val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
        val databaseReference = firebaseDatabase.getReference(TASKS_NODE)
        databaseReference.push().setValue(task.toPlannerTaskFirebase())
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun updateTask(
        task: PlannerTaskInfo,
        onSuccessListener: OnSuccessListener<Any>,
        onFailureListener: OnFailureListener
    ) {
        val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
        val databaseReference = firebaseDatabase.reference

        task.taskFirebaseKey?.let { key ->
            databaseReference.child(TASKS_NODE).child(key)
                .setValue(task.toPlannerTaskFirebase())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener)
        }
    }

    fun deleteTask(
        task: PlannerTaskInfo,
        onSuccessListener: OnSuccessListener<Any>,
        onFailureListener: OnFailureListener
    ) {
        val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
        val databaseReference = firebaseDatabase.reference

        task.taskFirebaseKey?.let {
            databaseReference.child(TASKS_NODE).child(it).removeValue()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener)
        }
    }

}