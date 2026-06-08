package com.example.computerclub.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.computerclub.presentation.auth.AuthUiState
import com.example.computerclub.presentation.home.ComputerClubUiState

@Composable
fun StatusMessage(state: ComputerClubUiState) {
    state.message?.let { message ->
        Text(
            text = message,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun StatusMessage(state: AuthUiState) {
    state.message?.let { message ->
        Text(
            text = message,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun Double.formatPrice(): String =
    "%,.0f".format(this).replace(',', ' ')
