package com.gmail.sofiapatiy.ui.auth.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.sofiapatiy.AppConstants.Companion.DB_URL
import com.gmail.sofiapatiy.AppConstants.Companion.USERS_NODE
import com.gmail.sofiapatiy.data.convertors.toPlannerUserModel
import com.gmail.sofiapatiy.data.model.firebase.PlannerUserFirebase
import com.gmail.sofiapatiy.data.model.ui.PlannerUserInfo
import com.gmail.sofiapatiy.data.network.OperationStatus
import com.gmail.sofiapatiy.ktx.isEmailValid
import com.gmail.sofiapatiy.repository.PlannerRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class SignupViewModel @Inject constructor(
    @Named("Coroutine.Context.IO") private val coroutineContext: CoroutineContext,
    private val repository: PlannerRepository
) : ViewModel() {
    private val _operationStatus = MutableStateFlow<OperationStatus?>(null)
    val operationStatus = _operationStatus.asStateFlow()

    val userLogin = MutableStateFlow<String?>(null)
    val userEmail = MutableStateFlow<String?>(null)
    val userPassword = MutableStateFlow<String?>(null)
    val userConfirmedPassword = MutableStateFlow<String?>(null)

    private val firebaseDatabase = FirebaseDatabase.getInstance(DB_URL)
    private val databaseReference = firebaseDatabase.getReference(USERS_NODE)

    private val _users = repository.getAllUsers()

    val isUserAlreadyExists = combine(
        _users,
        userLogin,
    ) { users, enteredLogin ->
        enteredLogin ?: return@combine false

        return@combine users.any { user ->
            user.username == enteredLogin
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val isSignupEnabled = combine(
        userLogin,
        userEmail,
        userPassword,
        userConfirmedPassword,
        isUserAlreadyExists
    ) { enteredLogin, enteredEmail, enteredPassword, confirmedPassword, isUserExists ->
        enteredLogin ?: return@combine false
        enteredEmail ?: return@combine false
        enteredPassword ?: return@combine false
        confirmedPassword ?: return@combine false

        return@combine (!isUserExists) and
                enteredEmail.isEmailValid() and
                (enteredPassword == confirmedPassword)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val plannerUserInfo = combine(
        userLogin,
        userEmail,
        userPassword,
    ) { enteredLogin, enteredEmail, enteredPassword ->
        enteredLogin ?: return@combine null
        enteredEmail ?: return@combine null
        enteredPassword ?: return@combine null

        return@combine PlannerUserInfo(
            username = enteredLogin,
            email = enteredEmail,
            password = enteredPassword
        )
    }

    private val firebaseEventListener = object : ValueEventListener {

        // direct parse -Map of users- from Firebase
        private val taskTypeIndicator =
            object : GenericTypeIndicator<Map<String, PlannerUserFirebase>>() {}

        override fun onDataChange(snapshot: DataSnapshot) {
            runCatching {
                val firebaseUsers = snapshot.getValue(taskTypeIndicator)
                val databaseReadyUsers = firebaseUsers?.entries
                    ?.map { plannerUserFirebase ->
                        plannerUserFirebase.value.toPlannerUserModel()
                    }

                viewModelScope.launch(coroutineContext) {
                    when (databaseReadyUsers) {
                        null -> repository.deleteUsers()
                        else -> repository.refreshUsers(databaseReadyUsers)
                    }
                }
            }.onFailure {
                Log.e("firebase_data_parser", "$it")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("firebase_data_cancel", "$error")
        }

    }

    fun submitNewUser() {
        viewModelScope.launch(coroutineContext) {
            runCatching {
                plannerUserInfo.firstOrNull()?.let { newUser ->
                    repository.submitNewUser(
                        user = newUser,
                        onSuccessListener = {
                            _operationStatus.update { OperationStatus.Success }
                        },
                        onFailureListener = { e ->
                            _operationStatus.update { OperationStatus.Failure(e) }
                        }

                    )
                }
            }.onFailure {
                Log.e("submit_new_task", "$it")
            }
        }
    }

    override fun onCleared() {
        databaseReference.removeEventListener(firebaseEventListener)
        super.onCleared()
    }

    init {
        databaseReference.addValueEventListener(firebaseEventListener)
    }
}