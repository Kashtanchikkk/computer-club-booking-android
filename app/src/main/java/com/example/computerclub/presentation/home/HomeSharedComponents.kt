package com.example.computerclub.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.computerclub.presentation.common.StatusMessage
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed

@Composable
internal fun BackHeader(title: String, subtitle: String?, onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onBack) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = HomeMuted,
                    modifier = Modifier.size(20.dp)
                )
                Text("Назад", color = HomeMuted, fontWeight = FontWeight.Bold)
            }
        }
        Text(title, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        subtitle?.let { Text(it, color = HomeMuted, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
    }
}

@Composable
internal fun PageHeader(title: String) {
    Text(title, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
}

@Composable
internal fun StepIndicator(step: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StepItem("Дата и время", active = step >= 1, modifier = Modifier.weight(1f))
        StepItem("Место", active = step >= 2, modifier = Modifier.weight(1f))
        StepItem("Подтверждение", active = false, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StepItem(text: String, active: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(5.dp).background(if (active) HomeRed else Color(0xFF3A3A3A), RoundedCornerShape(4.dp)))
        Text(text, color = if (active) HomeRed else Color(0xFF5C5C62), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

@Composable
internal fun SectionTitle(text: String) = Text(text, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)

@Composable
internal fun SectionHeader(title: String, onRefresh: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        SectionTitle(title)
        TextButton(onClick = onRefresh) { Text("Обновить", color = HomeRed) }
    }
}

@Composable
internal fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = HomeRed)) {
        Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
    }
}

@Composable
internal fun EmptyCard(text: String) {
    Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(HomeCardBg, RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
        Text(text, color = HomeMuted)
    }
}

@Composable
internal fun StatusAndLoading(state: ComputerClubUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StatusMessage(state)
        if (state.isLoading) CircularProgressIndicator(color = HomeRed)
    }
}
