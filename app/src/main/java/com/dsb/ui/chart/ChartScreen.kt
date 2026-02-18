package com.dsb.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.dsb.data.db.Expense
import com.dsb.data.repository.BudgetRepository
import com.dsb.util.formatShortDate
import com.dsb.util.weeksBetween
import java.util.Locale

data class ChartPoint(val dateMillis: Long, val budget: Double)

@Composable
fun ChartScreen(repository: BudgetRepository) {
    val expenses by repository.getExpenses().collectAsState(initial = emptyList())
    val config by repository.getBudgetConfig().collectAsState(initial = null)

    val chartPoints = remember(expenses, config) {
        buildChartData(expenses, config?.weeklyAmount ?: 0.0, config?.startDate ?: 0L)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Budget Over Time",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (chartPoints.size < 2) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add some expenses to see the chart",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val lineColor = MaterialTheme.colorScheme.primary
            val zeroColor = MaterialTheme.colorScheme.outlineVariant
            val textColor = MaterialTheme.colorScheme.onSurfaceVariant

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                val paddingLeft = 60f
                val paddingBottom = 40f
                val paddingTop = 20f
                val paddingRight = 20f
                val chartWidth = size.width - paddingLeft - paddingRight
                val chartHeight = size.height - paddingTop - paddingBottom

                val minY = chartPoints.minOf { it.budget }
                val maxY = chartPoints.maxOf { it.budget }
                val yRange = if (maxY == minY) 1.0 else maxY - minY
                val minX = chartPoints.first().dateMillis.toFloat()
                val maxX = chartPoints.last().dateMillis.toFloat()
                val xRange = if (maxX == minX) 1f else maxX - minX

                fun toScreenX(millis: Long) =
                    paddingLeft + ((millis - minX) / xRange) * chartWidth
                fun toScreenY(value: Double) =
                    paddingTop + chartHeight - ((value - minY) / yRange).toFloat() * chartHeight

                // Zero line if visible
                if (minY < 0 && maxY > 0) {
                    val zeroY = toScreenY(0.0)
                    drawLine(zeroColor, Offset(paddingLeft, zeroY), Offset(size.width - paddingRight, zeroY))
                }

                // Line path
                val path = Path()
                chartPoints.forEachIndexed { i, point ->
                    val x = toScreenX(point.dateMillis)
                    val y = toScreenY(point.budget)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, lineColor, style = Stroke(width = 3f))

                // Dots
                chartPoints.forEach { point ->
                    drawCircle(lineColor, 5f, Offset(toScreenX(point.dateMillis), toScreenY(point.budget)))
                }

                // Y-axis labels
                val paint = android.graphics.Paint().apply {
                    color = textColor.hashCode()
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
                listOf(minY, (minY + maxY) / 2, maxY).forEach { value ->
                    drawContext.canvas.nativeCanvas.drawText(
                        String.format(Locale.US, "$%.0f", value),
                        paddingLeft - 8f,
                        toScreenY(value) + 10f,
                        paint
                    )
                }

                // X-axis labels
                val labelPaint = android.graphics.Paint().apply {
                    color = textColor.hashCode()
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val step = maxOf(1, chartPoints.size / 5)
                chartPoints.filterIndexed { i, _ -> i % step == 0 || i == chartPoints.lastIndex }
                    .forEach { point ->
                        drawContext.canvas.nativeCanvas.drawText(
                            formatShortDate(point.dateMillis),
                            toScreenX(point.dateMillis),
                            size.height - 4f,
                            labelPaint
                        )
                    }
            }
        }
    }
}

private fun buildChartData(expenses: List<Expense>, weeklyAmount: Double, startDate: Long): List<ChartPoint> {
    if (startDate == 0L || weeklyAmount == 0.0) return emptyList()

    val now = System.currentTimeMillis()
    // Build a point for start date and after each expense
    val sorted = expenses.sortedBy { it.date }
    val points = mutableListOf<ChartPoint>()

    // Starting point
    val startBudget = weeksBetween(startDate, startDate) * weeklyAmount
    points.add(ChartPoint(startDate, startBudget))

    var runningExpenses = 0.0
    for (expense in sorted) {
        runningExpenses += expense.amount
        val totalBudget = weeksBetween(startDate, expense.date) * weeklyAmount
        points.add(ChartPoint(expense.date, totalBudget - runningExpenses))
    }

    // Current point
    val currentBudget = weeksBetween(startDate, now) * weeklyAmount - runningExpenses
    if (points.last().dateMillis < now - 3600_000) {
        points.add(ChartPoint(now, currentBudget))
    }

    return points
}
