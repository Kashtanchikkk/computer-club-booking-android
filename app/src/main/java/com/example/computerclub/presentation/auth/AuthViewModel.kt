package com.example.computerclub.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.computerclub.domain.model.User
import com.example.computerclub.domain.usecase.LoginUseCase
import com.example.computerclub.domain.usecase.LogoutUseCase
import com.example.computerclub.domain.usecase.RegisterUseCase
import com.example.computerclub.domain.usecase.RestoreSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val authMode: AuthMode = AuthMode.Login,
    val user: User? = null,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val message: String? = null
)

enum class AuthMode { Login, Register }

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val restoreSessionUseCase: RestoreSessionUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    init {
        restoreSession()
    }

    fun updateEmail(value: String) = _state.update { it.copy(email = value) }
    fun updatePassword(value: String) = _state.update { it.copy(password = value) }
    fun updateName(value: String) = _state.update { it.copy(name = value) }

    fun switchAuthMode() {
        _state.update {
            it.copy(
                authMode = if (it.authMode == AuthMode.Login) AuthMode.Register else AuthMode.Login,
                message = null
            )
        }
    }

    fun submitAuth() {
        val current = state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, message = null) }
            val result = if (current.authMode == AuthMode.Login) {
                loginUseCase(current.email, current.password)
            } else {
                registerUseCase(current.email, current.password, current.name)
            }
            result
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            password = "",
                            message = "Вы вошли как ${user.name}"
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, message = error.message ?: "Ошибка") }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.value = AuthUiState(message = "Вы вышли из аккаунта")
        }
    }

    private fun restoreSession() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, message = null) }
            restoreSessionUseCase()
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            message = null
                        )
                    }
                }
                .onFailure {
                    _state.update { current ->
                        current.copy(isLoading = false)
                    }
                }
        }
    }

    class Factory(
        private val loginUseCase: LoginUseCase,
        private val registerUseCase: RegisterUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val restoreSessionUseCase: RestoreSessionUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AuthViewModel(loginUseCase, registerUseCase, logoutUseCase, restoreSessionUseCase) as T
    }
}
