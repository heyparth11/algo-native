package com.example.algonative.persentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CommonTabs2(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.padding(4.dp),
        containerColor = Color.Transparent,
        edgePadding = 0.dp,
        contentColor = Color.Black,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTab),
                color = Color.Black
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                interactionSource = remember { MutableInteractionSource() },
                selected = selectedTab == index,
                onClick = {
                    onTabSelected(index)
                },
                text = {
                    Text(
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}