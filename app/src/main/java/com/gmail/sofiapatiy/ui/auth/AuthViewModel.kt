package com.gmail.sofiapatiy.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.sofiapatiy.AppConstants.Companion.DB_URL
import com.gmail.sofiapatiy.AppConstants.Companion.USERS_NODE
import com.gmail.sofiapatiy.data.model.firebase.PlannerUserFirebase
import com.gmail.sofiapatiy.ktx.asBase64Encoded
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
    private val databaseReference = firebaseDatabase.getReference(USERS_NODE)

    private val _users = MutableStateFlow<Map<String, PlannerUserFirebase>?>(null)

    private val firebaseEventListener = object : ValueEventListener {

        // direct parse -Map of users- from Firebase
        private val userTypeIndicator =
            object : GenericTypeIndicator<Map<String, PlannerUserFirebase>>() {}

        override fun onDataChange(snapshot: DataSnapshot) {
            runCatching {
                val firebaseUsers = snapshot.getValue(userTypeIndicator)
                _users.update { firebaseUsers }
            }.onFailure {
                Log.e("firebase_data_parser", "$it")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("firebase_data_cancel", "$error")
        }
    }

    val userLogin = MutableStateFlow<String?>(null)
    val userPassword = MutableStateFlow<String?>(null)

    val validUserId = combine(
        _users,
        userLogin,
        userPassword
    ) { users, enteredLogin, enteredPassword ->
        users ?: return@combine null
        enteredLogin ?: return@combine null
        enteredPassword ?: return@combine null

        val encodedPassword = enteredPassword.asBase64Encoded().trim()

        return@combine users.entries.firstOrNull { firebaseUser ->
            firebaseUser.value.username == enteredLogin.trim() &&
                    firebaseUser.value.password == encodedPassword
        }?.key // firebase user key serve as auth parameter
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val isLoginEnabled = validUserId.map { user ->
        user != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    override fun onCleared() {
        databaseReference.removeEventListener(firebaseEventListener)
        super.onCleared()
    }

    init {
        databaseReference.addValueEventListener(firebaseEventListener)
    }
}