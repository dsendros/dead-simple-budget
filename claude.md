# Budgeting App

A very simple Android budgeting app built with Kotlin + Jetpack Compose (Material 3).

## Key features

**Phase 1** (implemented): The app tracks a rolling budget.
  - Every week, it adds some amount of money to the total spendable budget (calculated, not scheduled)
  - The user can add an expense, noting the name and amount. The app will deduct the expense from the total spendable budget
  - The user can edit or delete expenses from the history screen
  - The user can see a history of their expenses, including date, amount, and name
  - The user can quickly see how much of their weekly budget remains
  - The user can view a chart of how their total spendable amount has changed over time
  - Onboarding screen on first launch to set weekly budget amount
  - Settings screen to edit weekly budget amount

**Phase 2** (implemented): Widget (Jetpack Glance)
  - Home screen widget showing total spendable budget and weekly remaining
  - Tap widget to open app with Add Expense dialog
  - Widget auto-updates when expenses or budget config change

## Tech stack

- **Language**: Kotlin
- **UI**: Jetpack Compose, Material 3, Material Icons Extended
- **Database**: Room (SQLite) for expenses and budget config
- **Preferences**: DataStore (onboarding flag)
- **Navigation**: Compose Navigation with bottom nav bar
- **Chart**: Custom Canvas drawing (no external charting library)
- **Min SDK**: 26 (Android 8.0)
- **Widget**: Jetpack Glance (`glance-appwidget` + `glance-material3`)
- **Architecture**: Single-module, repository pattern, no ViewModels (Flows collected directly in composables)

## Project structure

```
app/src/main/java/com/dsb/
├── data/
│   ├── db/             # Room database, DAOs, entities (Expense, BudgetConfig)
│   └── repository/     # BudgetRepository
├── ui/
│   ├── theme/          # Material 3 theme with dynamic colors
│   ├── onboarding/     # First-launch setup screen
│   ├── home/           # Main screen (total spendable + weekly remaining)
│   ├── addexpense/     # Add expense dialog
│   ├── history/        # Expense history list (tap to edit, delete button)
│   ├── chart/          # Budget-over-time line chart
│   ├── settings/       # Edit weekly budget amount
│   ├── widget/         # Glance home screen widget (BudgetWidget, Receiver, Updater)
│   └── Navigation.kt   # Nav graph + bottom bar
├── util/               # Date/week helpers
└── MainActivity.kt     # Entry point, onboarding gate
```

## Build & deploy

```bash
export JAVA_HOME=~/android-studio-panda1-patch1-linux/android-studio/jbr
cd ~/dead-simple-budget && ./gradlew installDebug
```

ADB is at `~/Android/Sdk/platform-tools/adb`.
