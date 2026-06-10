package com.example.algonative.persentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CommonTabs(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        edgePadding = 20.dp,
        containerColor = Color.Transparent,

        // Remove bottom indicator
        indicator = {},

        // Remove divider line
        divider = {}
    ) {

        tabs.forEachIndexed { index, title ->

            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onTabSelected(index)
                    }
                    .padding(end = 24.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = if (selectedTab == index)
                        MaterialTheme.colorScheme.onBackground
                    else
                        Color.Gray,
                    fontWeight = if (selectedTab == index)
                        FontWeight.Bold
                    else
                        FontWeight.Normal
                )
            }
        }
    }
}