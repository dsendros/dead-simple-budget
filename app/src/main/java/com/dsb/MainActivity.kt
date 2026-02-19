package com.dsb

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.dsb.data.db.AppDatabase
import com.dsb.data.repository.BudgetRepository
import com.dsb.ui.MainNavigation
import com.dsb.ui.onboarding.OnboardingScreen
import com.dsb.ui.theme.DSBTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val ComponentActivity.dataStore by preferencesDataStore(name = "settings")
private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")

class MainActivity : ComponentActivity() {

    private var showAddExpenseFromWidget by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        showAddExpenseFromWidget = intent?.getBooleanExtra("show_add_expense", false) == true

        val db = AppDatabase.getInstance(this)
        val repository = BudgetRepository(db, applicationContext)

        setContent {
            DSBTheme {
                val scope = rememberCoroutineScope()
                val onboardingComplete by dataStore.data
                    .map { prefs -> prefs[ONBOARDING_COMPLETE] ?: false }
                    .collectAsState(initial = null)

                when (onboardingComplete) {
                    null -> Image(
                        painter = painterResource(R.drawable.ic_splash),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    false -> OnboardingScreen(
                        onComplete = { weeklyAmount ->
                            scope.launch {
                                repository.initBudgetConfig(weeklyAmount)
                                dataStore.edit { it[ONBOARDING_COMPLETE] = true }
                            }
                        }
                    )
                    true -> {
                        val shouldShow = showAddExpenseFromWidget
                        if (shouldShow) showAddExpenseFromWidget = false
                        MainNavigation(repository, showAddExpenseOnLaunch = shouldShow)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra("show_add_expense", false)) {
            showAddExpenseFromWidget = true
        }
    }
}
