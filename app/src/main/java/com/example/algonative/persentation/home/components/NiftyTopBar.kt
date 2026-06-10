package com.example.algonative.persentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NiftyTopBar(
    modifier: Modifier = Modifier
) {

    val items = listOf(
        MarketIndex("NIFTY 50", 24800.25, "+0.45", true),
        MarketIndex("BANKNIFTY", 55780.40, "+0.72", true),
        MarketIndex("SENSEX", 81220.15, "-0.18", false),
        MarketIndex("FINNIFTY", 26110.85, "+0.52", true),
        MarketIndex("MIDCAP", 18452.20, "+0.31", true),
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(items) { item ->

            MarketIndexItem(
                item = item
            )
        }
    }
}

data class MarketIndex(
    val title: String,
    val price: Double,
    val change: String,
    val isPositive: Boolean
)

@Composable
fun MarketIndexItem(
    item: MarketIndex
) {

    Column {

        Text(
            text = item.title,
            fontSize = 12.sp,
            color = Color(0xFF817E7E)
        )

        Row {

            Text(
                text = String.format("%.2f", item.price),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = "(${item.change}%)",
                fontSize = 10.sp,
                color =
                    if (item.isPositive)
                        Color(0xFF003EC4)
                    else
                        Color(0xFFFB2D3F)
            )
        }
    }
}

@Composable
fun NiftyTopBarLoading() {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(5) {

            Column {

                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                )
            }
        }
    }
}