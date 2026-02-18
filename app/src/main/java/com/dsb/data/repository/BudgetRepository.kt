package com.dsb.data.repository

import android.content.Context
import com.dsb.data.db.AppDatabase
import com.dsb.data.db.BudgetConfig
import com.dsb.data.db.Expense
import com.dsb.ui.widget.WidgetUpdater
import com.dsb.util.startOfCurrentWeek
import com.dsb.util.weeksBetween
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class BudgetRepository(
    private val db: AppDatabase,
    private val appContext: Context? = null
) {

    private val expenseDao = db.expenseDao()
    private val configDao = db.budgetConfigDao()

    private suspend fun notifyWidget() {
        appContext?.let { WidgetUpdater.updateAllWidgets(it) }
    }

    suspend fun addExpense(name: String, amount: Double) {
        expenseDao.insert(Expense(name = name, amount = amount))
        notifyWidget()
    }

    fun getExpenses(): Flow<List<Expense>> = expenseDao.getAll()

    suspend fun deleteExpense(id: Long) {
        expenseDao.deleteById(id)
        notifyWidget()
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.update(expense)
        notifyWidget()
    }

    fun getBudgetConfig(): Flow<BudgetConfig?> = configDao.get()

    suspend fun setBudgetConfig(weeklyAmount: Double) {
        val existing = configDao.getOnce()
        configDao.upsert(BudgetConfig(
            weeklyAmount = weeklyAmount,
            startDate = existing?.startDate ?: System.currentTimeMillis()
        ))
        notifyWidget()
    }

    suspend fun initBudgetConfig(weeklyAmount: Double) {
        configDao.upsert(BudgetConfig(weeklyAmount = weeklyAmount, startDate = System.currentTimeMillis()))
        notifyWidget()
    }

    fun getTotalSpendable(): Flow<Double> {
        return combine(configDao.get(), expenseDao.getTotalSpent()) { config, totalSpent ->
            if (config == null) return@combine 0.0
            val weeks = weeksBetween(config.startDate, System.currentTimeMillis())
            (weeks * config.weeklyAmount) - totalSpent
        }
    }

    fun getWeeklyRemaining(): Flow<Double> {
        return combine(configDao.get(), expenseDao.getSpentSince(startOfCurrentWeek())) { config, weekSpent ->
            if (config == null) return@combine 0.0
            config.weeklyAmount - weekSpent
        }
    }
}
