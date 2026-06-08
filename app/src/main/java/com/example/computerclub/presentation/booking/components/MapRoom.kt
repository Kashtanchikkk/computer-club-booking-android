package com.example.computerclub.presentation.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.computerclub.ui.theme.HomeMuted

@Composable
internal fun MapRoom(
    title: String,
    x: Int,
    y: Int,
    width: Int,
    height: Int
) {
    Box(
        modifier = Modifier
            .offset(x.dp, y.dp)
            .size(width.dp, height.dp)
            .border(1.dp, Color(0xFF6C717A), RoundedCornerShape(2.dp))
            .background(Color(0x22000000)),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = title,
            color = HomeMuted,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 5.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun MapWall(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) {
    Box(
        modifier = Modifier
            .offset(x.dp, y.dp)
            .width(width.dp)
            .height(height.dp)
            .background(Color(0xFF9B9B9B), RoundedCornerShape(2.dp))
    )
}
