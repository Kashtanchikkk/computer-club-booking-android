package com.example.computerclub.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.computerclub.presentation.home.HomeTab
import com.example.computerclub.ui.theme.HomeFieldBg
import com.example.computerclub.ui.theme.HomeMuted
import com.example.computerclub.ui.theme.HomeRed

@Composable
internal fun HomeBottomBar(
    selectedTab: HomeTab,
    isAdmin: Boolean,
    onTabSelected: (HomeTab) -> Unit,
    onBookingSelected: () -> Unit
) {
    NavigationBar(containerColor = HomeFieldBg) {
        NavigationBarItem(
            selected = selectedTab == HomeTab.Main,
            onClick = { onTabSelected(HomeTab.Main) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
            label = { Text("Главная") },
            colors = bottomBarItemColors()
        )
        NavigationBarItem(
            selected = selectedTab == HomeTab.Seats,
            onClick = onBookingSelected,
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Брони") },
            label = { Text("Брони") },
            colors = bottomBarItemColors()
        )
        if (isAdmin) {
            NavigationBarItem(
                selected = selectedTab == HomeTab.Admin,
                onClick = { onTabSelected(HomeTab.Admin) },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Админ панель") },
                label = { Text("Админ панель") },
                colors = bottomBarItemColors()
            )
        } else {
            NavigationBarItem(
                selected = selectedTab == HomeTab.Profile,
                onClick = { onTabSelected(HomeTab.Profile) },
                icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                label = { Text("Профиль") },
                colors = bottomBarItemColors()
            )
        }
    }
}

@Composable
private fun bottomBarItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = HomeRed,
    selectedTextColor = HomeRed,
    unselectedIconColor = HomeMuted,
    unselectedTextColor = HomeMuted,
    indicatorColor = HomeFieldBg
)
