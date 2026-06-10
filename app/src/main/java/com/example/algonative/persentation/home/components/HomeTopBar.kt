package com.example.algonative.persentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {

    TopAppBar(
        title = {

            SearchBarCard(
                onClick = onSearchClick
            )
        },

        navigationIcon = {

            IconButton(
                onClick = onMenuClick
            ) {

                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Notifications"
                )

//                Image(
//                    painter = painterResource(
//                        R.drawable.menu
//                    ),
//                    contentDescription = "Menu",
//                    modifier = Modifier.size(24.dp)
//                )
            }
        },

        actions = {

            NotificationButton(
                count = 2,
                onClick = onNotificationClick
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )
        }
    )
}

@Composable
fun SearchBarCard(
    onClick: () -> Unit
) {

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp,
            Color(0xFFF5F5F5)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Notifications"
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

//            TextAnimator()
        }
    }
}

@Composable
fun NotificationButton(
    count: Int,
    onClick: () -> Unit
) {

    Box {

        IconButton(
            onClick = onClick
        ) {

            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications"
            )
        }

        if (count > 0) {

            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(
                        Color(0xFFD80000),
                        CircleShape
                    )
                    .align(
                        Alignment.TopEnd
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}