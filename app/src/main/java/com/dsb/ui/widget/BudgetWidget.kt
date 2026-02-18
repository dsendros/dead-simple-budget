package com.dsb.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.dsb.MainActivity
import com.dsb.data.db.AppDatabase
import com.dsb.data.repository.BudgetRepository
import kotlinx.coroutines.flow.first
import java.util.Locale

class BudgetWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getInstance(context)
        val repository = BudgetRepository(db)

        val totalSpendable = repository.getTotalSpendable().first()
        val weeklyRemaining = repository.getWeeklyRemaining().first()

        provideContent {
            GlanceTheme {
                WidgetContent(context, totalSpendable, weeklyRemaining)
            }
        }
    }
}

private val Green = ColorProvider(androidx.compose.ui.graphics.Color(0xFF2E7D32))
private val Red = ColorProvider(androidx.compose.ui.graphics.Color(0xFFC62828))
private val Blue = ColorProvider(androidx.compose.ui.graphics.Color(0xFF1565C0))
private val LabelColor = ColorProvider(androidx.compose.ui.graphics.Color(0xFF666666))

@Composable
private fun WidgetContent(context: Context, totalSpendable: Double, weeklyRemaining: Double) {
    val intent = Intent(context, MainActivity::class.java).apply {
        putExtra("show_add_expense", true)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .padding(12.dp)
            .clickable(actionStartActivity(intent)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Spendable",
                style = TextStyle(fontSize = 11.sp, color = LabelColor)
            )
            Text(
                text = String.format(Locale.US, "$%.2f", totalSpendable),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (totalSpendable >= 0) Green else Red
                )
            )
        }
        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Weekly Remaining",
                style = TextStyle(fontSize = 11.sp, color = LabelColor)
            )
            Text(
                text = String.format(Locale.US, "$%.2f", weeklyRemaining),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (weeklyRemaining >= 0) Blue else Red
                )
            )
        }
    }
}
