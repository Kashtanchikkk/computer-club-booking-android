package com.example.computerclub.presentation.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.computerclub.ui.theme.HomeCardBg
import com.example.computerclub.ui.theme.HomeMuted

@Composable
internal fun MapLegend() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        LegendItem("свободно", Color(0xFFF6F0DA), Modifier.weight(1f))
        LegendItem("занято", Color(0xFF4A4A4F), Modifier.weight(1f))
        LegendItem("недоступно", Color(0xFF2E3138), Modifier.weight(1f))
        LegendItem("выбрано", Color(0xFFFFFF66), Modifier.weight(1f))
    }
}

@Composable
private fun LegendItem(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .background(HomeCardBg, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(3.dp))
        )

        Text(
            text = text,
            color = HomeMuted,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}
