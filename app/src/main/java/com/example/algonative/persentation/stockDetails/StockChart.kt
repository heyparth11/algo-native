package com.example.algonative.persentation.stockDetails

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algonative.domain.model.Candle
import kotlin.math.roundToInt

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

    var activeX by remember { mutableStateOf<Float?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    val totalPoints = closePrices.size

    val selectedIndex = remember(activeX, canvasWidth, totalPoints) {
        val x = activeX
        if (x != null && canvasWidth > 0 && totalPoints > 0) {
            val fraction = (x / canvasWidth).coerceIn(0f, 1f)
            (fraction * (totalPoints - 1)).roundToInt().coerceIn(0, totalPoints - 1)
        } else {
            null
        }
    }

    val selectedCandle = remember(selectedIndex, sortedData) {
        selectedIndex?.let { idx ->
            if (idx < sortedData.size) sortedData[idx] else null
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    canvasWidth = size.width.toFloat()
                    canvasHeight = size.height.toFloat()
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val anyPressed = event.changes.any { it.pressed }
                            if (anyPressed) {
                                val change = event.changes.firstOrNull { it.pressed } ?: event.changes.first()
                                activeX = change.position.x
                                isDragging = true
                                change.consume()
                            } else {
                                activeX = null
                                isDragging = false
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            if (width == 0f || height == 0f) return@Canvas

            // Calculate absolute Canvas point offsets
            val coordinates = closePrices.mapIndexed { index, closePrice ->
                val x = if (totalPoints > 1) (index.toFloat() / (totalPoints - 1)) * width else 0f
                val y = height - (((closePrice - minPrice) / priceRange) * height)
                PointF(x, y.toFloat())
            }

            if (coordinates.isEmpty()) return@Canvas

            // Build smooth Bézier transitions
            val strokePath = Path().apply {
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

            // Draw selection vertical line and highlight dot if user is hovering/interacting
            if (selectedIndex != null && selectedIndex < coordinates.size) {
                val selectedPoint = coordinates[selectedIndex]
                
                // Draw vertical dashed line
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.7f),
                    start = Offset(selectedPoint.x, 0f),
                    end = Offset(selectedPoint.x, height),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // Draw highlight dot (blue outer, white inner)
                drawCircle(
                    color = lineColor,
                    radius = 6.dp.toPx(),
                    center = Offset(selectedPoint.x, selectedPoint.y)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(selectedPoint.x, selectedPoint.y)
                )
            } else if (coordinates.isNotEmpty()) {
                // Draw default current price threshold indicator (Uses the latest/most recent entry)
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

        // Float tooltip over selected point
        if (selectedIndex != null && selectedCandle != null && canvasWidth > 0 && canvasHeight > 0) {
            val fraction = selectedIndex.toFloat() / (totalPoints - 1).coerceAtLeast(1)
            val selectedPointX = fraction * canvasWidth
            val yFraction = ((selectedCandle.close - minPrice) / priceRange).toFloat()
            val selectedPointY = canvasHeight - (yFraction * canvasHeight)

            val density = LocalDensity.current

            val tooltipX = with(density) { selectedPointX.toDp() }
            val tooltipY = with(density) { selectedPointY.toDp() }
            val canvasWidthDp = with(density) { canvasWidth.toDp() }

            // Define tooltip size parameters to calculate perfect positioning bounds
            val tooltipWidth = 100.dp
            val tooltipHalfWidth = 50.dp
            
            // Constrain X offset to keep the tooltip fully visible on screen
            val offsetX = (tooltipX - tooltipHalfWidth).coerceIn(0.dp, maxOf(0.dp, canvasWidthDp - tooltipWidth))
            
            // Determine Y offset (show below point if too close to top edge)
            val offsetY = if (tooltipY - 55.dp < 0.dp) {
                tooltipY + 15.dp
            } else {
                tooltipY - 55.dp
            }

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .width(tooltipWidth),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$${"%.2f".format(selectedCandle.close)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = selectedCandle.date,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 9.sp
                    )
                }
            }
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