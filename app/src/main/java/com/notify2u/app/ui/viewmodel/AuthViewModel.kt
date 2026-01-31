package com.notify2u.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notify2u.app.data.local.UserDao
import com.notify2u.app.data.local.UserEntity
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val userDao: UserDao) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userProfile = MutableStateFlow<UserEntity?>(null)
    val userProfile: StateFlow<UserEntity?> = _userProfile.asStateFlow()

    init {
        viewModelScope.launch {
            userDao.getCurrentUser().collect { user ->
                _userProfile.value = user
                _isLoggedIn.value = user != null
            }
        }
    }

    fun login(email: String, displayName: String, role: String = "USER") {
        viewModelScope.launch {
            try {
                // Link with actual Firebase Auth for cloud sync UID
                val authResult = Firebase.auth.signInAnonymously().await()
                val uid = authResult.user?.uid ?: "mock_id_${System.currentTimeMillis()}"

                val user = UserEntity(
                    id = uid,
                    email = email,
                    displayName = displayName,
                    photoUrl = null,
                    role = role
                )
                userDao.insertUser(user)
            } catch (e: Exception) {
                // Fallback to mock ID if offline/error
                val user = UserEntity(
                    id = "mock_id_${System.currentTimeMillis()}",
                    email = email,
                    displayName = displayName,
                    photoUrl = null,
                    role = role
                )
                userDao.insertUser(user)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            userDao.clearUser()
        }
    }
}
