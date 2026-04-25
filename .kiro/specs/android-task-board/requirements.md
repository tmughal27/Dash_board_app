# Requirements Document

## Introduction

An Android mobile application that allows users to manage their daily tasks using a visual task board (Kanban-style). Users can create, organize, and track tasks across columns representing different stages (e.g., To Do, In Progress, Done). The app will be distributed via the Google Play Store.

## Glossary

- **App**: The Android task board application
- **Task**: A unit of work with a title, optional description, priority, and due date
- **Board**: The main view displaying all task columns
- **Column**: A vertical lane on the Board representing a task stage (e.g., To Do, In Progress, Done)
- **Card**: The visual representation of a Task on the Board
- **User**: The person using the App on their Android device
- **Task_Manager**: The component responsible for creating, updating, deleting, and persisting tasks
- **Board_View**: The component responsible for rendering the Board and its Columns
- **Storage**: The local persistence layer (SQLite/Room database) that stores tasks and board state

## Requirements

### Requirement 1: Task Board Display

**User Story:** As a User, I want to see all my tasks organized in columns by status, so that I can get a quick overview of my daily progress.

#### Acceptance Criteria

1. THE Board_View SHALL display a minimum of three Columns: "To Do", "In Progress", and "Done"
2. THE Board_View SHALL display each Task as a Card within its corresponding Column
3. WHEN the Board is opened, THE Board_View SHALL load and display all persisted tasks within 500ms
4. WHEN a Column contains more Cards than fit on screen, THE Board_View SHALL allow vertical scrolling within that Column
5. THE Board_View SHALL display the task count for each Column as a badge on the Column header

---

### Requirement 2: Task Creation

**User Story:** As a User, I want to create new tasks with relevant details, so that I can track what needs to be done.

#### Acceptance Criteria

1. WHEN the User taps the "Add Task" button, THE App SHALL present a task creation form
2. THE Task_Manager SHALL require a non-empty title (1–100 characters) for every Task
3. WHERE the User provides a description, THE Task_Manager SHALL store a description of up to 500 characters
4. WHERE the User sets a due date, THE Task_Manager SHALL store the due date and display it on the Card
5. WHERE the User sets a priority level, THE Task_Manager SHALL assign one of three priority levels: Low, Medium, or High
6. WHEN the User submits a valid task creation form, THE Task_Manager SHALL persist the new Task to Storage and add the Card to the "To Do" Column
7. IF the User submits a task creation form with an empty title, THEN THE App SHALL display a validation error message and retain the form data

---

### Requirement 3: Task Status Management

**User Story:** As a User, I want to move tasks between columns, so that I can reflect the current state of my work.

#### Acceptance Criteria

1. WHEN the User drags a Card to a different Column, THE Board_View SHALL move the Card to the target Column
2. WHEN the User long-presses a Card, THE App SHALL present a context menu with available status transitions
3. WHEN a Task status is changed, THE Task_Manager SHALL persist the updated status to Storage
4. WHEN a Task is moved to the "Done" Column, THE Board_View SHALL visually distinguish the Card (e.g., strikethrough title or muted color)

---

### Requirement 4: Task Editing and Deletion

**User Story:** As a User, I want to edit and delete tasks, so that I can keep my board accurate and up to date.

#### Acceptance Criteria

1. WHEN the User taps a Card, THE App SHALL open a task detail view displaying all Task fields
2. WHEN the User edits a Task and saves, THE Task_Manager SHALL validate the updated fields using the same rules as task creation and persist the changes to Storage
3. WHEN the User deletes a Task, THE App SHALL display a confirmation dialog before deletion
4. WHEN the User confirms deletion, THE Task_Manager SHALL remove the Task from Storage and THE Board_View SHALL remove the Card from the Board
5. IF the User cancels the deletion dialog, THEN THE App SHALL retain the Task without modification

---

### Requirement 5: Task Filtering and Search

**User Story:** As a User, I want to filter and search tasks, so that I can quickly find specific tasks on a busy board.

#### Acceptance Criteria

1. WHEN the User enters text in the search field, THE Board_View SHALL filter Cards to show only those whose title or description contains the search text
2. WHEN the User selects a priority filter, THE Board_View SHALL display only Cards matching the selected priority level
3. WHEN the User clears all filters, THE Board_View SHALL restore the full unfiltered Board view
4. WHILE a filter is active, THE Board_View SHALL display a visible indicator showing the active filter criteria

---

### Requirement 6: Data Persistence

**User Story:** As a User, I want my tasks to be saved locally, so that my board is preserved between app sessions.

#### Acceptance Criteria

1. THE Storage SHALL persist all Tasks and their fields (title, description, priority, due date, status, creation timestamp) to a local SQLite database via the Room persistence library
2. WHEN the App is closed and reopened, THE Board_View SHALL restore all Tasks to their last known state
3. IF a Storage write operation fails, THEN THE App SHALL display an error message and retain the unsaved data in memory for the current session
4. THE Storage SHALL complete read and write operations within 200ms for boards containing up to 500 Tasks

---

### Requirement 7: Google Play Store Distribution

**User Story:** As a developer, I want the app to meet Google Play Store requirements, so that it can be published and discovered by users.

#### Acceptance Criteria

1. THE App SHALL target Android API level 34 (Android 14) and support a minimum SDK of API level 26 (Android 8.0)
2. THE App SHALL declare only the permissions required for its functionality in the Android manifest
3. THE App SHALL be packaged as an Android App Bundle (AAB) for Play Store submission
4. THE App SHALL include a Privacy Policy URL in the Play Store listing, as required by Google Play policies
5. THE App SHALL comply with Google Play's target API level requirements at the time of submission

---

### Requirement 8: Task Reminder Notifications

**User Story:** As a User, I want to receive push notifications reminding me of tasks with due dates, so that I don't miss my daily commitments.

#### Acceptance Criteria

1. WHEN the User sets a due date and time on a Task, THE Notification_Scheduler SHALL schedule a local push notification to be delivered at that date and time
2. WHEN a scheduled notification is delivered, THE App SHALL display a notification containing the Task title and due time in the Android notification drawer
3. WHEN the User taps a delivered notification, THE App SHALL open and navigate directly to the detail view of the corresponding Task
4. WHERE the User enables notifications for a specific Task, THE Notification_Scheduler SHALL schedule a reminder for that Task's due date and time
5. WHERE the User disables notifications for a specific Task, THE Notification_Scheduler SHALL cancel any pending reminder for that Task
6. THE App SHALL provide a global notification toggle in Settings that enables or disables all Task reminder notifications
7. WHILE the global notification toggle is disabled, THE Notification_Scheduler SHALL not schedule or deliver any Task reminder notifications
8. WHEN the User updates the due date or time of a Task that has a pending reminder, THE Notification_Scheduler SHALL cancel the existing reminder and schedule a new one for the updated date and time
9. WHEN the User deletes a Task that has a pending reminder, THE Notification_Scheduler SHALL cancel the corresponding scheduled notification
10. WHEN the App is installed on a device running Android API 33 or higher, THE App SHALL request the POST_NOTIFICATIONS permission before scheduling any notifications
11. IF the User denies the POST_NOTIFICATIONS permission, THEN THE App SHALL disable notification scheduling and display an informational message explaining that notifications can be enabled later via device Settings
12. IF a notification cannot be scheduled due to a system error, THEN THE App SHALL log the error and display an in-app warning on the affected Task Card
