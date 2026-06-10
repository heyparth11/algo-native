package com.example.algonative.persentation.navigation


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algonative.R

@Composable
fun M3BottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    // In M3, we use a Box to stack the FAB on top of the NavigationBar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp), // Height matches NavigationBar exactly
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. The Actual Navigation Bar
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier
                .height(80.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                )
        ) {
            // Left Side Items
            M3NavItem(
                "Market",
                R.drawable.nav_market,
                selectedTab == "market"
            ) { onTabSelected("market") }
            M3NavItem(
                "Watchlist",
                R.drawable.nav_watchlist,
                selectedTab == "watchlist"
            ) { onTabSelected("watchlist") }

            // 2. THE GAP: An empty item to leave room for the FAB
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = { Spacer(Modifier.width(40.dp)) },
                enabled = false
            )

            // Right Side Items
            M3NavItem(
                "Portfolio",
                R.drawable.nav_portfolio,
                selectedTab == "portfolio"
            ) { onTabSelected("portfolio") }
            M3NavItem(
                "Order",
                R.drawable.nav_order,
                selectedTab == "order"
            ) { onTabSelected("order") }
        }

        AppLogo(
            onClick = { onTabSelected("robo") },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
        )
    }
}

@Composable
fun AppLogo(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .requiredSize(100.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFA9C0FF),
                            Color.White
                        )
                    ),
                    shape = CircleShape
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        color = Color(0xFF003EC4),
                        shape = CircleShape
                    )
                    .padding(9.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(
                        R.drawable.logo
                    ),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
fun RowScope.M3NavItem(
    label: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontSize = 10.sp) },
        icon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF2563EB),
            selectedTextColor = Color(0xFF2563EB),
            unselectedIconColor = Color(0xFF94A3B8),
            unselectedTextColor = Color(0xFF94A3B8),
            indicatorColor = Color.Transparent // Removes the M3 "pill" background
        )
    )
}
