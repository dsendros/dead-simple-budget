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

**Phase 3** (implemented): Goals, Infusions, and UX improvements
  - Renamed "Total Spendable" to "Total Budget" throughout app and widget
  - Text inputs (expense name, goal title, infusion note) auto-capitalize first letter
  - One-time infusions: ad hoc additions to total budget, added via Settings, displayed in History
  - Savings goals: set a goal with title and target amount, fund it from total budget
  - Goals screen with progress bars, tap to fund, delete support
  - Chart integrates infusions into the budget-over-time timeline
  - Total budget formula: `(weeks * weeklyAmount) + totalInfused - totalSpent - totalGoalsFunded`

## Tech stack

- **Language**: Kotlin
- **UI**: Jetpack Compose, Material 3, Material Icons Extended
- **Database**: Room (SQLite) for expenses, budget config, infusions, and goals
- **Preferences**: DataStore (onboarding flag)
- **Navigation**: Compose Navigation with bottom nav bar (5 tabs: Home, History, Chart, Goals, Settings)
- **Chart**: Custom Canvas drawing (no external charting library)
- **Min SDK**: 26 (Android 8.0)
- **Widget**: Jetpack Glance (`glance-appwidget` + `glance-material3`)
- **Architecture**: Single-module, repository pattern, no ViewModels (Flows collected directly in composables)

## Project structure

```
app/src/main/java/com/dsb/
├── data/
│   ├── db/             # Room database, DAOs, entities (Expense, BudgetConfig, Infusion, Goal)
│   └── repository/     # BudgetRepository
├── ui/
│   ├── theme/          # Material 3 theme with dynamic colors
│   ├── onboarding/     # First-launch setup screen
│   ├── home/           # Main screen (total budget + weekly remaining)
│   ├── addexpense/     # Add expense dialog
│   ├── history/        # Transaction history (expenses + infusions, sorted by date)
│   ├── chart/          # Budget-over-time line chart (expenses + infusions)
│   ├── goals/          # Goals screen (list, add, fund, delete)
│   ├── settings/       # Edit weekly budget amount + add infusions
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

## TODO

**App icon** - Find a better app icon. Something that reflects death and budgeting. Maybe a skull with dollar signs for eyes?
