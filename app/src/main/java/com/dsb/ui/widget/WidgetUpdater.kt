package com.dsb.ui.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

object WidgetUpdater {
    suspend fun updateAllWidgets(context: Context) {
        BudgetWidget().updateAll(context)
    }
}
