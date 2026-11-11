package com.example.metruyenchu.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// BOTTOM NAVIGATION ITEM
// ==========================================================

data class BottomNavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)


// ==========================================================
// BOTTOM NAVIGATION BAR
// ==========================================================

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {

    val items = listOf(

        BottomNavigationItem(
            route = "bookmark",
            title = "Tủ Truyện",
            icon = Icons.Default.BarChart
        ),

        BottomNavigationItem(
            route = "home",
            title = "Khám Phá",
            icon = Icons.Default.Explore
        ),

        BottomNavigationItem(
            route = "ranking",
            title = "Xếp Hạng",
            icon = Icons.Default.Star
        ),

        BottomNavigationItem(
            route = "account",
            title = "Tài Khoản",
            icon = Icons.Default.AccountCircle
        )
    )


    // ======================================================
    // THANH BOTTOM
    // ======================================================

    NavigationBar(
        modifier = Modifier
            .height(95.dp),

        containerColor = Color(0xFF292929),

        tonalElevation = 0.dp
    ) {

        items.forEach { item ->

            val selected = when (item.route) {

                "bookmark" ->
                    currentRoute == "bookmark" ||
                            currentRoute == "history"

                else ->
                    currentRoute == item.route
            }


            // ==================================================
            // ITEM
            // ==================================================

            NavigationBarItem(

                selected = selected,

                onClick = {
                    onItemClick(item.route)
                },

                // ==============================================
                // ICON + TEXT
                // ==============================================

                icon = {

                    Column(
                        modifier = Modifier
                            .padding(top = 6.dp),

                        horizontalAlignment = Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        // ======================================
                        // ICON
                        // ======================================

                        Icon(
                            imageVector = item.icon,

                            contentDescription = item.title,

                            tint = if (selected) {
                                Color.White
                            } else {
                                Color(0xFF888888)
                            },

                            modifier = Modifier.size(
                                if (selected) {
                                    23.dp
                                } else {
                                    20.dp
                                }
                            )
                        )

                        Spacer(
                            modifier = Modifier.height(1.dp)
                        )

                        // ======================================
                        // CHỮ
                        // ======================================

                        Text(
                            text = item.title,

                            color = if (selected) {
                                Color.White
                            } else {
                                Color(0xFF888888)
                            },

                            fontSize = 10.sp,

                            fontWeight = if (selected) {
                                FontWeight.Medium
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                },

                // ==================================================
                // QUAN TRỌNG:
                // XÓA Ô NỀN/INDICATOR MẶC ĐỊNH
                // ==================================================

                colors = NavigationBarItemDefaults.colors(

                    // Không có ô nền khi selected
                    indicatorColor = Color.Transparent,

                    selectedIconColor = Color.White,

                    unselectedIconColor =
                        Color(0xFF888888),

                    selectedTextColor = Color.White,

                    unselectedTextColor =
                        Color(0xFF888888)
                )
            )
        }
    }
}