package com.dsb.data.repository

import android.content.Context
import com.dsb.data.db.AppDatabase
import com.dsb.data.db.BudgetConfig
import com.dsb.data.db.Expense
import com.dsb.data.db.Goal
import com.dsb.data.db.Infusion
import com.dsb.ui.widget.WidgetUpdater
import com.dsb.util.startOfCurrentWeek
import com.dsb.util.weeksBetween
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek

class BudgetRepository(
    private val db: AppDatabase,
    private val appContext: Context? = null
) {

    private val expenseDao = db.expenseDao()
    private val configDao = db.budgetConfigDao()
    private val infusionDao = db.infusionDao()
    private val goalDao = db.goalDao()

    private suspend fun notifyWidget() {
        appContext?.let { WidgetUpdater.updateAllWidgets(it) }
    }

    // Expenses

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

    // Budget config

    fun getBudgetConfig(): Flow<BudgetConfig?> = configDao.get()

    suspend fun setBudgetConfig(weeklyAmount: Double, startDayOfWeek: Int = 1) {
        val existing = configDao.getOnce()
        configDao.upsert(BudgetConfig(
            weeklyAmount = weeklyAmount,
            startDate = existing?.startDate ?: System.currentTimeMillis(),
            startDayOfWeek = startDayOfWeek
        ))
        notifyWidget()
    }

    suspend fun initBudgetConfig(weeklyAmount: Double) {
        configDao.upsert(BudgetConfig(weeklyAmount = weeklyAmount, startDate = System.currentTimeMillis()))
        notifyWidget()
    }

    // Infusions

    fun getInfusions(): Flow<List<Infusion>> = infusionDao.getAll()

    suspend fun addInfusion(amount: Double, note: String) {
        infusionDao.insert(Infusion(amount = amount, note = note))
        notifyWidget()
    }

    suspend fun deleteInfusion(id: Long) {
        infusionDao.deleteById(id)
        notifyWidget()
    }

    // Goals

    fun getGoals(): Flow<List<Goal>> = goalDao.getAll()

    suspend fun addGoal(title: String, targetAmount: Double) {
        goalDao.insert(Goal(title = title, targetAmount = targetAmount))
    }

    suspend fun fundGoal(goal: Goal, amount: Double) {
        goalDao.update(goal.copy(fundedAmount = goal.fundedAmount + amount))
        notifyWidget()
    }

    suspend fun deleteGoal(id: Long) {
        goalDao.deleteById(id)
        notifyWidget()
    }

    // Budget calculations

    fun getTotalSpendable(): Flow<Double> {
        return combine(
            configDao.get(),
            expenseDao.getTotalSpent(),
            infusionDao.getTotalInfused(),
            goalDao.getTotalFunded()
        ) { config, totalSpent, totalInfused, totalGoalsFunded ->
            if (config == null) return@combine 0.0
            val day = DayOfWeek.of(config.startDayOfWeek)
            val weeks = weeksBetween(config.startDate, System.currentTimeMillis(), day)
            (weeks * config.weeklyAmount) + totalInfused - totalSpent - totalGoalsFunded
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun getWeeklyRemaining(): Flow<Double> {
        return configDao.get().flatMapLatest { config ->
            if (config == null) return@flatMapLatest flowOf(0.0)
            val day = DayOfWeek.of(config.startDayOfWeek)
            expenseDao.getSpentSince(startOfCurrentWeek(day)).map { weekSpent ->
                config.weeklyAmount - weekSpent
            }
        }
    }
}
