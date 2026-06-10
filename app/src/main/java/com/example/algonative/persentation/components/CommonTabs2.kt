package com.example.algonative.persentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


enum class Destination(
    val route: String,
    val label: String,
    val contentDescription: String
) {
    CHART("chart", "Chart", "Charts"),
    STOCKINFO("stockinfo", "Stock Info", "StockInfo"),
    FO("fo", "F&O", "fo"),
    NEWS("news", "News", "News")
}

@Composable
fun CommonTabs2(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val startDestination = Destination.CHART

    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    PrimaryScrollableTabRow(
        selectedTabIndex = selectedDestination,
        modifier = Modifier.padding(4.dp),
        containerColor = Color.Transparent,
        edgePadding = 0.dp,
        contentColor = Color.Black,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedDestination),
                color = Color.Black
            )
        },
        divider = {}
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            Tab(
                interactionSource = remember { MutableInteractionSource() },
                selected = selectedDestination == index,
                onClick = {
                    selectedDestination = index
                },
                text = {
                    Text(
                        text = destination.label,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}