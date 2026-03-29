package com.slots.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    init {
        if (firebaseAuth.currentUser != null) {
            _authState.value = AuthState.Success
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val account = task.await()
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                firebaseAuth.signInWithCredential(credential).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google sign in failed")
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Idle
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
