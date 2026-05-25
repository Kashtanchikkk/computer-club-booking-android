package com.example.computerclub.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed

@Composable
internal fun MainHeader(name: String, onProfile: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(430.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF32080E), Color(0xFF08090D)),
                    radius = 720f
                ),
                RoundedCornerShape(28.dp)
            )
            .padding(22.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(170.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x55FF3347), Color.Transparent),
                        radius = 180f
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "CYBERCLUB",
                        color = HomeRed,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "COMPUTER CLUB",
                        color = HomeMuted,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0x6615161B), CircleShape)
                        .clickable(onClick = onProfile),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Black)
                }
            }

            Column {
                Text(
                    "ТВОЁ МЕСТО",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black
                )
                Row {
                    Text(
                        "ТВОЯ ",
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "ИГРА",
                        color = HomeRed,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    "Бронируй место и играй\nна лучшем оборудовании",
                    color = HomeMuted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
