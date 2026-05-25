package com.example.computerclub.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.computerclub.domain.model.SeatType
import com.example.computerclub.presentation.common.formatPrice
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed
import com.example.computerclub.ui.theme.HomeStroke

@Composable
internal fun EquipmentDetailsCard(types: List<SeatType>) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = HomeCardBg.copy(alpha = 0.82f)),
        border = BorderStroke(1.dp, HomeStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("ХАРАКТЕРИСТИКИ МЕСТ", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            if (types.isEmpty()) {
                Text("Характеристики загружаются", color = HomeMuted, fontWeight = FontWeight.Bold)
            } else {
                types.forEach { type ->
                    EquipmentRow(type)
                }
            }
        }
    }
}

@Composable
private fun EquipmentRow(type: SeatType) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(type.name, color = Color.White, fontWeight = FontWeight.Black)
            Text("${type.pricePerHour.formatPrice()} ₽/ч", color = HomeRed, fontWeight = FontWeight.Black)
        }
        Text("${type.processor} · ${type.gpu}", color = HomeMuted, fontWeight = FontWeight.Bold)
        Text("${type.ram} · ${type.monitor}", color = HomeMuted, fontWeight = FontWeight.Bold)
    }
}
