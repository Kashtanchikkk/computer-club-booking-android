package com.example.computerclub.domain.usecase

import com.example.computerclub.domain.repository.ComputerClubRepository

class LoginUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(email: String, password: String) =
        repository.login(email, password)
}

class RegisterUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke(email: String, password: String, name: String) =
        repository.register(email, password, name)
}

class LogoutUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke() = repository.logout()
}

class RestoreSessionUseCase(private val repository: ComputerClubRepository) {
    suspend operator fun invoke() = repository.loadProfile()
}
