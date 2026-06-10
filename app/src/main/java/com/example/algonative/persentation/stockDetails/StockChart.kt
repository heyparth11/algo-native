package com.example.algonative.persentation.stockDetails


import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.algonative.domain.model.Candle

@Composable
fun StockJsonChart(
    rawHistoryPoints: List<Candle>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF2172E5),
    dottedLineColor: Color = Color(0xFF2172E5).copy(alpha = 0.5f),
) {

    // 1. Sort chronologically (Oldest to Newest) so graph flows left-to-right
    val sortedData = remember(rawHistoryPoints) {
        rawHistoryPoints.sortedBy { it.date }
    }

    // 2. Map out just the closing prices for our line path plotting
    val closePrices = remember(sortedData) {
        sortedData.map { it.close }
    }

    if (closePrices.isEmpty()) return

    val maxPrice = remember(closePrices) { closePrices.maxOrNull() ?: 0.0 }
    val minPrice = remember(closePrices) { closePrices.minOrNull() ?: 0.0 }
    val priceRange = remember(maxPrice, minPrice) {
        maxOf(maxPrice - minPrice, 1.0)
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 16.dp)
    ) {
        val width = size.width
        val height = size.height
        val totalPoints = closePrices.size

        // Calculate absolute Canvas point offsets
        val coordinates = closePrices.mapIndexed { index, closePrice ->
            val x = if (totalPoints > 1) (index.toFloat() / (totalPoints - 1)) * width else 0f
            val y = height - (((closePrice - minPrice) / priceRange) * height)
            PointF(x, y.toFloat())
        }

        // Build smooth Bézier transitions
        val strokePath = Path().apply {
            if (coordinates.isNotEmpty()) {
                moveTo(coordinates.first().x, coordinates.first().y)
                for (i in 0 until coordinates.size - 1) {
                    val p1 = coordinates[i]
                    val p2 = coordinates[i + 1]
                    val controlPointX1 = p1.x + (p2.x - p1.x) / 2f
                    val controlPointY1 = p1.y
                    val controlPointX2 = p1.x + (p2.x - p1.x) / 2f
                    val controlPointY2 = p2.y

                    cubicTo(
                        controlPointX1,
                        controlPointY1,
                        controlPointX2,
                        controlPointY2,
                        p2.x,
                        p2.y
                    )
                }
            }
        }

        // Closed path loop to paint the vertical background fill gradient
        val fillPath = Path().apply {
            addPath(strokePath)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        // Draw fading color gradient block
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.35f),
                    lineColor.copy(alpha = 0.02f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = height
            )
        )

        // Draw top slick accent line
        drawPath(
            path = strokePath,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw current price threshold indicator (Uses the latest/most recent entry)
        if (coordinates.isNotEmpty()) {
            val latestPoint = coordinates.last() // The right-most data node

            drawLine(
                color = dottedLineColor,
                start = Offset(0f, latestPoint.y),
                end = Offset(latestPoint.x, latestPoint.y),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // Dynamic tracking node knob placement matching mockup design
            drawCircle(
                color = Color.Black,
                radius = 5.dp.toPx(),
                center = Offset(latestPoint.x, latestPoint.y)
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(latestPoint.x, latestPoint.y)
            )
        }
    }
}

//
//import android.graphics.PointF
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.PathEffect
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun StockChart(
//    dataPoints: List<Float>, // A list of raw stock prices
//    modifier: Modifier = Modifier,
//    lineColor: Color = Color(0xFF2172E5), // The vibrant blue line
//    dottedLineColor: Color = Color(0xFF2172E5).copy(alpha = 0.5f)
//) {
//    if (dataPoints.isEmpty()) return
//
//    // Find bounds to map data scale properly onto the Canvas size
//    val maxPrice = remember(dataPoints) { dataPoints.maxOrNull() ?: 0f }
//    val minPrice = remember(dataPoints) { dataPoints.minOrNull() ?: 0f }
//    val priceRange = remember(maxPrice, minPrice) {
//        if (maxPrice == minPrice) 1f else maxPrice - minPrice
//    }
//
//    Canvas(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(250.dp)
//            .padding(vertical = 16.dp)
//    ) {
//        val width = size.width
//        val height = size.height
//        val totalPoints = dataPoints.size
//
//        // 1. Calculate precise X and Y coordinates for every data point
//        val coordinates = dataPoints.mapIndexed { index, price ->
//            val x = (index.toFloat() / (totalPoints - 1)) * width
//            // Invert Y axis because Canvas (0,0) is at the top left
//            val y = height - (((price - minPrice) / priceRange) * height)
//            PointF(x, y)
//        }
//
//        // 2. Generate smooth cubic spline transitions (Bézier Curves)
//        val strokePath = Path().apply {
//            if (coordinates.isNotEmpty()) {
//                moveTo(coordinates.first().x, coordinates.first().y)
//
//                for (i in 0 until coordinates.size - 1) {
//                    val p1 = coordinates[i]
//                    val p2 = coordinates[i + 1]
//
//                    // Control points help create the natural rolling curve wave effect
//                    val controlPointX1 = p1.x + (p2.x - p1.x) / 2f
//                    val controlPointY1 = p1.y
//                    val controlPointX2 = p1.x + (p2.x - p1.x) / 2f
//                    val controlPointY2 = p2.y
//
//                    cubicTo(
//                        controlPointX1, controlPointY1,
//                        controlPointX2, controlPointY2,
//                        p2.x, p2.y
//                    )
//                }
//            }
//        }
//
//        // 3. Create the bottom closed path wrapper to contain the gradient fill
//        val fillPath = Path().apply {
//            addPath(strokePath)
//            // Draw lines down to bottom bounds to close the polygon loop
//            lineTo(width, height)
//            lineTo(0f, height)
//            close()
//        }
//
//        // 4. Draw the fading vertical background gradient
//        drawPath(
//            path = fillPath,
//            brush = Brush.verticalGradient(
//                colors = listOf(
//                    lineColor.copy(alpha = 0.4f), // Intense tint at top
//                    lineColor.copy(alpha = 0.05f),
//                    Color.Transparent             // Fades completely out near bottom
//                ),
//                startY = 0f,
//                endY = height
//            )
//        )
//
//        // 5. Draw the solid trend stroke line over the top of the gradient
//        drawPath(
//            path = strokePath,
//            color = lineColor,
//            style = Stroke(
//                width = 3.dp.toPx(),
//                cap = StrokeCap.Round
//            )
//        )
//
//        // 6. Draw the dashed horizontal threshold guideline (e.g., current/previous close marker)
//        // We'll mimic the picture by tracking an arbitrary point in the latter half of the data
//        if (coordinates.size > 2) {
//            val targetIndicatorPoint = coordinates[((coordinates.size) * 0.75f).toInt()]
//
//            drawLine(
//                color = dottedLineColor,
//                start = Offset(0f, targetIndicatorPoint.y),
//                end = Offset(targetIndicatorPoint.x, targetIndicatorPoint.y),
//                strokeWidth = 1.5.dp.toPx(),
//                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
//            )
//
//            // 7. Draw the target outer tracking ring indicator knob
//            drawCircle(
//                color = Color.Black,
//                radius = 5.dp.toPx(),
//                center = Offset(targetIndicatorPoint.x, targetIndicatorPoint.y)
//            )
//            drawCircle(
//                color = Color.White,
//                radius = 3.dp.toPx(),
//                center = Offset(targetIndicatorPoint.x, targetIndicatorPoint.y)
//            )
//        }
//    }
//}