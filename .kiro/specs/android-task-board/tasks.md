# Implementation Plan: Android Task Board

## Overview

Implement a native Android Kanban-style task board app using Kotlin, Jetpack Compose, MVVM + Repository architecture, Room for local persistence, and AlarmManager-based local notifications. Tasks are broken down incrementally so each step builds on the previous and ends with everything wired together.

## Tasks

- [x] 1. Project setup and core data layer
  - Configure `build.gradle` with dependencies: Room, Jetpack Compose, Kotlin Coroutines, Kotest, Hilt (or manual DI)
  - Set `targetSdk = 34`, `minSdk = 26` in `build.gradle`
  - Define `Priority`, `TaskStatus` enums
  - Define `Task` Room entity with all fields (title, description, priority, status, dueDate, notificationsEnabled, createdAt)
  - Define `TaskDao` with `observeAll()`, `getById()`, `insert()`, `update()`, `delete()`
  - Define `AppDatabase` Room database class
  - _Requirements: 6.1, 7.1_

- [x] 2. Task validation and domain models
  - [x] 2.1 Implement `TaskValidator` with title (1–100 chars) and description (≤500 chars) rules
    - Return `ValidationResult.Valid` or `ValidationResult.Invalid(errors)`
    - _Requirements: 2.2, 2.3, 2.7_

  - [ ]* 2.2 Write property test for task form validation (Property 3)
    - **Property 3: Form validation accepts only valid inputs**
    - **Validates: Requirements 2.2, 2.3, 2.7**
    - Use `Arb.string()` generators for title and description; assert accept iff lengths in valid range
    - Tag: `// Feature: android-task-board, Property 3: Task form validation accepts only valid inputs`

  - [x] 2.3 Define `TaskForm`, `BoardState`, `ValidationResult`, `ValidationError` data/sealed classes
    - _Requirements: 2.1, 2.2_

- [x] 3. Repository and storage layer
  - [x] 3.1 Implement `TaskRepositoryImpl` wrapping `TaskDao`
    - All suspend functions wrap Room calls in `try/catch` and return `Result<T>`
    - Expose `getAllTasks(): Flow<List<Task>>`
    - _Requirements: 6.1, 6.3_

  - [ ]* 3.2 Write property test for task field round-trip through storage (Property 4)
    - **Property 4: Field round-trip through storage**
    - **Validates: Requirements 2.4, 2.5, 2.6, 6.1, 6.2**
    - Use `Arb.task()` generator; insert then retrieve by ID and assert equality
    - Tag: `// Feature: android-task-board, Property 4: Task field round-trip through storage`

  - [ ]* 3.3 Write property test for task edit round-trip (Property 5)
    - **Property 5: Task edit round-trip**
    - **Validates: Requirements 4.2**
    - Use `Arb.task()` and `Arb.taskForm()`; save edited task, retrieve, assert fields match
    - Tag: `// Feature: android-task-board, Property 5: Task edit round-trip`

  - [ ]* 3.4 Write property test for deletion removes task from storage (Property 6)
    - **Property 6: Deletion removes task from storage**
    - **Validates: Requirements 4.4, 4.5**
    - Use `Arb.list(Arb.task())`; delete one task, assert absent, assert others unchanged
    - Tag: `// Feature: android-task-board, Property 6: Deletion removes task from storage`

- [ ] 4. Checkpoint — Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 5. BoardViewModel and filtering logic
  - [ ] 5.1 Implement `BoardStateMapper` that groups tasks by `TaskStatus` into `BoardState`
    - Compute `columns` map and `taskCounts` map
    - _Requirements: 1.1, 1.2, 1.5_

  - [ ]* 5.2 Write property test for task appears in correct column (Property 1)
    - **Property 1: Task appears in correct column**
    - **Validates: Requirements 1.2, 3.1, 3.3**
    - Use `Arb.list(Arb.task())`; assert each task in `boardState.columns[task.status]` only
    - Tag: `// Feature: android-task-board, Property 1: Task appears in correct column`

  - [ ]* 5.3 Write property test for column badge count equals task count (Property 2)
    - **Property 2: Column badge count equals task count**
    - **Validates: Requirements 1.5**
    - Use `Arb.list(Arb.task())`; assert `taskCounts[col] == tasks.count { it.status == col }`
    - Tag: `// Feature: android-task-board, Property 2: Column badge count equals task count`

  - [ ] 5.4 Implement `BoardViewModel` with `boardState`, `searchQuery`, `activeFilter` StateFlows
    - Implement `onSearchQueryChanged()`, `onPriorityFilterChanged()`, `onClearFilters()`, `onTaskMoved()`
    - Apply search and priority filter reactively via `combine` on the tasks flow
    - _Requirements: 1.1, 1.3, 1.5, 3.1, 3.3, 5.1, 5.2, 5.3, 5.4_

  - [ ]* 5.5 Write property test for search filter returns only matching tasks (Property 7)
    - **Property 7: Search filter returns only matching tasks**
    - **Validates: Requirements 5.1**
    - Use `Arb.list(Arb.task())` and `Arb.string()`; assert all results contain query (case-insensitive), no non-matches
    - Tag: `// Feature: android-task-board, Property 7: Search filter returns only matching tasks`

  - [ ]* 5.6 Write property test for priority filter returns only matching tasks (Property 8)
    - **Property 8: Priority filter returns only matching tasks**
    - **Validates: Requirements 5.2**
    - Use `Arb.list(Arb.task())` and `Arb.enum<Priority>()`; assert all results match priority
    - Tag: `// Feature: android-task-board, Property 8: Priority filter returns only matching tasks`

  - [ ]* 5.7 Write property test for clearing filters restores full task list (Property 9)
    - **Property 9: Clearing filters restores full task list**
    - **Validates: Requirements 5.3**
    - Use `Arb.list(Arb.task())` with random filters applied then cleared; assert result equals original list
    - Tag: `// Feature: android-task-board, Property 9: Clearing filters restores full task list`

- [ ] 6. TaskViewModel and task CRUD
  - [ ] 6.1 Implement `TaskViewModel` with `taskDetail` and `validationState` StateFlows
    - Implement `loadTask()`, `saveTask()` (calls `TaskValidator`, then repository), `deleteTask()`
    - Map `Result.failure` to `UiEvent` error state
    - _Requirements: 2.1, 2.6, 2.7, 4.1, 4.2, 4.3, 4.4, 4.5, 6.3_

  - [ ]* 6.2 Write unit tests for `TaskViewModel`
    - Test save with valid form persists task and emits success
    - Test save with invalid form emits validation errors and retains form data
    - Test delete emits confirmation, then removes task on confirm
    - Test storage error emits error UiEvent
    - _Requirements: 2.7, 4.3, 4.5, 6.3_

- [ ] 7. Notification scheduling
  - [ ] 7.1 Implement `NotificationSchedulerImpl` using `AlarmManager`
    - `schedule(task)`: set exact alarm for `task.dueDate`; no-op if global toggle off or dueDate null
    - `cancel(taskId)`: cancel pending alarm for task
    - `cancelAll()`: cancel all pending alarms
    - Handle `POST_NOTIFICATIONS` permission check for API 33+
    - _Requirements: 8.1, 8.2, 8.6, 8.7, 8.10, 8.11, 8.12_

  - [ ]* 7.2 Write property test for notification scheduled for every task with due date (Property 10)
    - **Property 10: Notification scheduled for every task with due date and notifications enabled**
    - **Validates: Requirements 8.1, 8.4**
    - Use `Arb.task(withDueDate = true)`; assert scheduler called with correct time
    - Tag: `// Feature: android-task-board, Property 10: Notification scheduled for every task with due date and notifications enabled`

  - [ ]* 7.3 Write property test for notification content contains task title and due time (Property 11)
    - **Property 11: Notification content contains task title and due time**
    - **Validates: Requirements 8.2**
    - Use `Arb.task(withDueDate = true)`; assert notification contains title and formatted due time
    - Tag: `// Feature: android-task-board, Property 11: Notification content contains task title and due time`

  - [ ]* 7.4 Write property test for notification lifecycle follows task lifecycle (Property 12)
    - **Property 12: Notification lifecycle follows task lifecycle**
    - **Validates: Requirements 8.5, 8.8, 8.9**
    - Use `Arb.task(withDueDate = true)`; assert cancel/reschedule called correctly on disable, due date update, and delete
    - Tag: `// Feature: android-task-board, Property 12: Notification lifecycle follows task lifecycle`

  - [ ]* 7.5 Write property test for global notification toggle suppresses all scheduling (Property 13)
    - **Property 13: Global notification toggle suppresses all scheduling**
    - **Validates: Requirements 8.7**
    - Use `Arb.task(withDueDate = true)` with global toggle off; assert no schedule call made
    - Tag: `// Feature: android-task-board, Property 13: Global notification toggle suppresses all scheduling`

  - [ ] 7.6 Implement `NotificationBroadcastReceiver` to build and show the notification on alarm fire
    - Display task title and due time in notification drawer
    - Deep-link PendingIntent to task detail screen
    - _Requirements: 8.2, 8.3_

- [ ] 8. Checkpoint — Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 9. Board UI — Board screen and task cards
  - [ ] 9.1 Implement `TaskCard` composable
    - Display title, priority badge, due date, and task count badge on column header
    - Apply strikethrough/muted style when `status == DONE`
    - Show notification error warning badge when `notificationError == true`
    - _Requirements: 1.2, 1.5, 2.4, 3.4, 8.12_

  - [ ] 9.2 Implement `BoardScreen` composable
    - Render three `LazyColumn` lanes in a horizontally scrollable `Row`
    - Show column headers with task count badges
    - Wire to `BoardViewModel` state
    - _Requirements: 1.1, 1.3, 1.4, 1.5_

  - [ ] 9.3 Add drag-and-drop and long-press context menu for status transitions
    - Use Compose drag-and-drop API for move; long-press context menu as fallback
    - Call `BoardViewModel.onTaskMoved()` on drop/selection
    - _Requirements: 3.1, 3.2, 3.3_

  - [ ] 9.4 Add search bar and priority filter controls to `BoardScreen`
    - Wire to `BoardViewModel.onSearchQueryChanged()` and `onPriorityFilterChanged()`
    - Show active filter indicator while any filter is active
    - _Requirements: 5.1, 5.2, 5.3, 5.4_

- [ ] 10. Task form and detail UI
  - [ ] 10.1 Implement `TaskFormScreen` composable (create and edit)
    - Fields: title, description, priority selector, due date picker, notifications toggle
    - Show inline validation errors on submit; retain form data on error
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 4.1, 4.2, 8.4, 8.5_

  - [ ] 10.2 Implement `TaskDetailScreen` composable
    - Display all task fields; provide Edit and Delete actions
    - Show confirmation dialog before deletion
    - _Requirements: 4.1, 4.3, 4.4, 4.5_

  - [ ] 10.3 Implement `SettingsScreen` composable with global notification toggle
    - Persist toggle state; wire to `NotificationScheduler.cancelAll()` when disabled
    - _Requirements: 8.6, 8.7_

- [ ] 11. Navigation and deep linking
  - [ ] 11.1 Set up Jetpack Navigation with routes for Board, TaskForm (create/edit), TaskDetail, Settings
    - Handle deep-link intent from notification tap to `TaskDetailScreen`
    - Show "Task not found" message if task no longer exists
    - _Requirements: 4.1, 8.3_

- [ ] 12. Android manifest and Play Store configuration
  - [ ] 12.1 Declare required permissions in `AndroidManifest.xml`
    - `SCHEDULE_EXACT_ALARM`, `POST_NOTIFICATIONS` (API 33+), `RECEIVE_BOOT_COMPLETED`
    - No extra permissions beyond what is required
    - _Requirements: 7.1, 7.2_

  - [ ] 12.2 Configure AAB build in `build.gradle` and verify `targetSdk = 34`, `minSdk = 26`
    - _Requirements: 7.1, 7.3_

  - [ ] 12.3 Add Privacy Policy URL placeholder to `strings.xml` / Play Store metadata
    - _Requirements: 7.4, 7.5_

- [ ] 13. Integration wiring and final checkpoint
  - [ ] 13.1 Wire `TaskRepository` into `TaskViewModel` and `BoardViewModel` via DI
    - Ensure notification scheduling is triggered from repository on insert/update/delete
    - _Requirements: 2.6, 3.3, 4.4, 6.1, 8.1, 8.8, 8.9_

  - [ ]* 13.2 Write Room integration test: insert 500 tasks, verify read completes within 200ms
    - _Requirements: 6.4_

  - [ ]* 13.3 Write end-to-end board load test: seed DB, open board, verify all tasks displayed
    - _Requirements: 1.3, 6.2_

- [ ] 14. Final checkpoint — Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for a faster MVP
- Each task references specific requirements for traceability
- Property tests use Kotest `kotest-property` with minimum 100 iterations per property
- All property tests must be tagged with `// Feature: android-task-board, Property N: <text>`
