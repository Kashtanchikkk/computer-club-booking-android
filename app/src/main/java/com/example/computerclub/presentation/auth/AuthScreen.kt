package com.example.computerclub.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.computerclub.presentation.common.StatusMessage
import com.example.computerclub.ui.theme.HomeAuthBorder
import com.example.computerclub.ui.theme.HomeAuthTop
import com.example.computerclub.ui.theme.HomeBg
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeInputBorder
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed
import com.example.computerclub.ui.theme.HomeSegmentBg

@Composable
fun AuthScreen(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSwitchMode: () -> Unit
) {
    val isRegister = state.authMode == AuthMode.Register

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(HomeAuthTop, HomeBg)))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(22.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Computer Club", color = Color.White, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text(
                    "Бронирование игровых мест",
                    color = HomeMuted,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = HomeCardBg),
                border = BorderStroke(1.dp, HomeAuthBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HomeSegmentBg, RoundedCornerShape(16.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ModeButton("Вход", selected = !isRegister, modifier = Modifier.weight(1f), onClick = {
                            if (isRegister) onSwitchMode()
                        })
                        ModeButton("Регистрация", selected = isRegister, modifier = Modifier.weight(1f), onClick = {
                            if (!isRegister) onSwitchMode()
                        })
                    }
                    if (isRegister) {
                        AuthField(state.name, onNameChange, "Имя")
                    }
                    AuthField(state.email, onEmailChange, "Email", KeyboardType.Email)
                    AuthField(state.password, onPasswordChange, "Пароль", KeyboardType.Password, true)

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HomeRed, contentColor = Color.White)
                    ) {
                        Text(if (isRegister) "Зарегистрироваться" else "Войти", fontWeight = FontWeight.Black)
                    }

                    TextButton(onClick = onSwitchMode, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            if (isRegister) "Уже есть аккаунт? Войти" else "Нет аккаунта? Зарегистрироваться",
                            color = HomeRed,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            StatusMessage(state)
        }
    }
}

@Composable
private fun ModeButton(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) HomeRed else Color.Transparent,
            contentColor = if (selected) Color.White else HomeMuted
        ),
        elevation = null
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(label) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = HomeRed,
            unfocusedBorderColor = HomeInputBorder,
            focusedLabelColor = HomeRed,
            unfocusedLabelColor = HomeMuted,
            cursorColor = HomeRed,
            focusedContainerColor = HomeCardBg,
            unfocusedContainerColor = HomeCardBg
        )
    )
}
