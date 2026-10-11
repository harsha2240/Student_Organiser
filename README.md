# Student Organizer App

An Android student organizer application built with **Kotlin**, **Jetpack Compose**, and **Navigation Compose**.

## Student Details

- **Name:** G.V.N. SREEHARSHA
- **SRN:** PES1UG24CS163
- **University:** PES University
- **Program:** B.Tech, Computer Science and Engineering

## Project Overview

Student Organizer App, branded as **CampusFlow**, provides a simple interface for viewing courses, tracking assignments, and organizing academic tasks. It uses sample data and in-memory Compose state to represent application data.

## Features

- **Dashboard:** Displays a welcome message, course information, quick statistics, and pending assignments.
- **Course Overview:** Browse courses including Machine Learning, DBMS, AAD, and DSA.
- **Assignments:** View assignments and filter them by all, pending, or completed.
- **Add Assignment:** Enter assignment details, including title, course, due date, priority, and description. Required fields are validated before saving.
- **Profile:** View and edit student profile information and see an academic overview.
- **Settings:** Includes dark-mode controls, notification preferences, and a default-screen selection.
- **Reusable UI Components:** Uses shared composable functions for course cards, assignment cards, navigation, and screen layouts.
- **Custom UI:** Features a navy-and-teal color scheme with Material Design components.

## Technologies Used

- Kotlin
- Android Studio
- Jetpack Compose
- Material 3
- Navigation Compose
- Gradle

## Data Handling

This project uses sample data and in-memory Compose state. Assignment data and profile edits may reset when the application process restarts. Notification preferences are UI controls; actual scheduled notifications are not implemented.

## Project Structure

```text
student_organiser_app/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── example/
│           │           └── studentorganizer/
│           │               └── MainActivity.kt
│           └── res/
├── gradle/
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

*Note: The project tree is illustrative. Your actual package structure may contain additional files or folders.*

## Running the Project

1. Clone or download this repository.
2. Open the project folder in Android Studio.
3. Allow Gradle sync to complete.
4. Select an Android emulator or a connected Android device.
5. Click **Run** to launch the application.

## Demo Video

The application testing video is included in the repository as `StudentOrganizerApp_DemoVideo.mp4`.

## Notes

- The app uses sample data for demonstration.
- Database persistence and backend API integration are not implemented.
- The application demonstrates Compose UI, state management, callbacks, form validation, and navigation.

---

**Developed by G.V.N. SREEHARSHA**  
**SRN:** PES1UG24CS163  
**PES University**
