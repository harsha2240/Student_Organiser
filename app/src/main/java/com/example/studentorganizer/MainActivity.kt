package com.example.studentorganizer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studentorganizer.ui.theme.StudentorganizerTheme
import java.util.UUID

private val Navy = Color(0xFF17243B)
private val Teal = Color(0xFF55C7B2)
private val Page = Color(0xFFF4F7FA)
private val MutedText = Color(0xFF737B89)

private object Routes {
    const val DASHBOARD = "dashboard"
    const val ASSIGNMENTS = "assignments"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val ADD_ASSIGNMENT = "add_assignment"
}

data class Assignment(
    val title: String,
    val course: String,
    val dueDate: String,
    val priority: String,
    val description: String = "",
    val reminderEnabled: Boolean = true,
    val completed: Boolean = false,
    val id: String = UUID.randomUUID().toString()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentorganizerTheme {
                CampusFlowApp()
            }
        }
    }
}

@Composable
fun CampusFlowApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    var assignments by remember {
        mutableStateOf(
            listOf(
                Assignment("Machine Learning Project", "ML", "Tomorrow", "High"),
                Assignment("Database Lab", "DBMS", "Friday", "Medium"),
                Assignment("Kotlin Exercises", "AAD", "Monday", "Low")
            )
        )
    }

    var darkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var defaultScreen by remember { mutableStateOf(Routes.DASHBOARD) }

    var studentName by remember { mutableStateOf("Student Name") }
    var studentId by remember { mutableStateOf("PES1UG24CS163") }
    var major by remember { mutableStateOf("Computer Science Engineering") }

    val scheme = if (darkMode) {
        darkColorScheme(
            primary = Teal,
            secondary = Teal,
            background = Navy,
            surface = Color(0xFF26344B),
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Navy,
            secondary = Teal,
            background = Page,
            surface = Color.White,
            onBackground = Navy,
            onSurface = Navy
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute != Routes.ADD_ASSIGNMENT

    MaterialTheme(colorScheme = scheme) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBottomBar) {
                    CampusBottomBar(navController, currentRoute)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = defaultScreen,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Routes.DASHBOARD) {
                    DashboardScreen(
                        studentName = studentName,
                        assignments = assignments,
                        onAddAssignment = {
                            navController.navigate(Routes.ADD_ASSIGNMENT)
                        },
                        onViewAll = {
                            navController.navigate(Routes.ASSIGNMENTS)
                        },
                        onToggleAssignment = { id ->
                            assignments = assignments.map {
                                if (it.id == id) it.copy(completed = !it.completed) else it
                            }
                        }
                    )
                }

                composable(Routes.ASSIGNMENTS) {
                    AssignmentsScreen(
                        assignments = assignments,
                        onAddAssignment = {
                            navController.navigate(Routes.ADD_ASSIGNMENT)
                        },
                        onToggleAssignment = { id ->
                            assignments = assignments.map {
                                if (it.id == id) it.copy(completed = !it.completed) else it
                            }
                        }
                    )
                }

                composable(Routes.PROFILE) {
                    ProfileScreen(
                        studentName = studentName,
                        studentId = studentId,
                        major = major,
                        onSaveProfile = { name, id, newMajor ->
                            studentName = name
                            studentId = id
                            major = newMajor
                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        },
                        onExportSummary = {
                            val summary = """
                                Academic Summary
                                Name: $studentName
                                Student ID: $studentId
                                Major: $major
                                GPA: 3.82
                                Completed Credits: 84
                                Semester: Fall 2026
                            """.trimIndent()

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Academic Summary")
                                putExtra(Intent.EXTRA_TEXT, summary)
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, "Export academic summary")
                            )
                        }
                    )
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        darkMode = darkMode,
                        onDarkModeChange = { darkMode = it },
                        notificationsEnabled = notificationsEnabled,
                        onNotificationsChange = { notificationsEnabled = it },
                        defaultScreen = defaultScreen,
                        onDefaultScreenChange = { defaultScreen = it },
                        onOpenDefaultScreen = {
                            navController.navigate(defaultScreen) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Routes.ADD_ASSIGNMENT) {
                    AddAssignmentScreen(
                        onCancel = { navController.popBackStack() },
                        onSave = { assignment ->
                            assignments = assignments + assignment
                            navController.popBackStack()
                            Toast.makeText(context, "Assignment added", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CampusBottomBar(navController: NavHostController, currentRoute: String?) {
    val tabs = listOf(
        Triple(Routes.DASHBOARD, "⌂", "Home"),
        Triple(Routes.ASSIGNMENTS, "✓", "Tasks"),
        Triple(Routes.PROFILE, "●", "Profile"),
        Triple(Routes.SETTINGS, "⚙", "Settings")
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        tabs.forEach { (route, iconText, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Routes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Text(iconText, fontSize = 20.sp) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun PageTitle(title: String, subtitle: String = "") {
    Text(
        text = title,
        fontSize = 27.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    if (subtitle.isNotBlank()) {
        Spacer(Modifier.height(6.dp))
        Text(subtitle, color = MutedText, fontSize = 14.sp)
    }
}

@Composable
fun DashboardScreen(
    studentName: String,
    assignments: List<Assignment>,
    onAddAssignment: () -> Unit,
    onViewAll: () -> Unit,
    onToggleAssignment: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        PageTitle("CampusFlow", "Welcome back, $studentName!")

        Spacer(Modifier.height(25.dp))
        Text("Your Overview", fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OverviewCard("Courses", "4", Modifier.weight(1f))
            OverviewCard(
                "Pending",
                assignments.count { !it.completed }.toString(),
                Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(25.dp))
        Text("Your Courses", fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("Machine Learning", "DBMS", "AAD", "DSA").forEach { course ->
                CourseCard(course)
            }
        }
        Spacer(Modifier.height(25.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Upcoming Work",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onViewAll) { Text("See all") }
        }

        Spacer(Modifier.height(8.dp))
        val upcoming = assignments.filter { !it.completed }.take(2)

        if (upcoming.isEmpty()) {
            Text(
                "All caught up! No pending assignments.",
                color = MutedText,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            upcoming.forEach { assignment ->
                AssignmentCard(assignment = assignment) {
                    onToggleAssignment(assignment.id)
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onAddAssignment,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Navy),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("+ Add Assignment")
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun CourseCard(course: String) {
    Card(
        modifier = Modifier.width(180.dp),
        colors = CardDefaults.cardColors(containerColor = Navy),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(course, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.65f },
                modifier = Modifier.fillMaxWidth(),
                color = Teal,
                trackColor = Color(0xFF42516A)
            )
            Spacer(Modifier.height(6.dp))
            Text("65% complete", color = Teal, fontSize = 12.sp)
        }
    }
}

@Composable
fun OverviewCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Navy)
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(value, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Teal)
            Spacer(Modifier.height(5.dp))
            Text(title, color = Color.White, fontSize = 13.sp)
        }
    }
}

@Composable
fun AssignmentCard(assignment: Assignment, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = assignment.completed,
                onCheckedChange = { onToggle() }
            )
            Column(Modifier.weight(1f)) {
                Text(
                    assignment.title,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${assignment.course} • Due ${assignment.dueDate}",
                    fontSize = 12.sp,
                    color = MutedText
                )
            }
            Text(
                assignment.priority.uppercase(),
                color = when (assignment.priority.lowercase()) {
                    "high" -> Color(0xFFD94F4F)
                    "medium" -> Color(0xFFB47A19)
                    else -> Teal
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AssignmentsScreen(
    assignments: List<Assignment>,
    onAddAssignment: () -> Unit,
    onToggleAssignment: (String) -> Unit
) {
    var filter by remember { mutableStateOf("All") }

    val visibleAssignments = assignments.filter {
        when (filter) {
            "Pending" -> !it.completed
            "Completed" -> it.completed
            else -> true
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        PageTitle("My Tasks", "Keep your semester on track")
        Spacer(Modifier.height(15.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("All", "Pending", "Completed").forEach { option ->
                FilterChip(
                    selected = filter == option,
                    onClick = { filter = option },
                    label = { Text(option) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(visibleAssignments, key = { it.id }) { assignment ->
                AssignmentCard(assignment) {
                    onToggleAssignment(assignment.id)
                }
            }
        }

        Button(
            onClick = onAddAssignment,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Navy)
        ) {
            Text("+ Add Assignment")
        }
    }
}

@Composable
fun ProfileScreen(
    studentName: String,
    studentId: String,
    major: String,
    onSaveProfile: (String, String, String) -> Unit,
    onExportSummary: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember(studentName) { mutableStateOf(studentName) }
    var editedId by remember(studentId) { mutableStateOf(studentId) }
    var editedMajor by remember(major) { mutableStateOf(major) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageTitle("My Profile", "Your academic snapshot")
        Spacer(Modifier.height(30.dp))

        Box(
            Modifier.size(95.dp).background(Teal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                studentName.firstOrNull()?.uppercase() ?: "S",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Navy
            )
        }

        Spacer(Modifier.height(15.dp))
        Text(studentName, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text("Student ID: $studentId", color = MutedText)
        Text(major, color = MutedText)

        Spacer(Modifier.height(25.dp))
        OverviewCard("GPA", "3.82", Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OverviewCard("Completed Credits", "84", Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OverviewCard("Semester", "Fall 2026", Modifier.fillMaxWidth())

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { showEditDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Profile")
        }
        OutlinedButton(
            onClick = onExportSummary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export Academic Summary")
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Student Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editedId,
                        onValueChange = { editedId = it },
                        label = { Text("Student ID") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editedMajor,
                        onValueChange = { editedMajor = it },
                        label = { Text("Major") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (
                            editedName.isNotBlank() &&
                            editedId.isNotBlank() &&
                            editedMajor.isNotBlank()
                        ) {
                            onSaveProfile(
                                editedName.trim(),
                                editedId.trim(),
                                editedMajor.trim()
                            )
                            showEditDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    defaultScreen: String,
    onDefaultScreenChange: (String) -> Unit,
    onOpenDefaultScreen: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        PageTitle("Settings", "Make CampusFlow yours")
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Dark Mode", fontWeight = FontWeight.SemiBold)
                Text("Use a darker appearance", color = MutedText, fontSize = 12.sp)
            }
            Switch(
                checked = darkMode,
                onCheckedChange = onDarkModeChange
            )
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Push Notifications", fontWeight = FontWeight.SemiBold)
                Text("Enable reminder preference", color = MutedText, fontSize = 12.sp)
            }
            Checkbox(
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsChange
            )
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))
        Text("Default Start Screen", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))

        listOf(Routes.DASHBOARD, Routes.ASSIGNMENTS).forEach { route ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = defaultScreen == route,
                    onClick = { onDefaultScreenChange(route) }
                )
                Text(if (route == Routes.DASHBOARD) "Dashboard" else "Assignments")
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onOpenDefaultScreen,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Selected Start Screen")
        }
    }
}

@Composable
fun AddAssignmentScreen(
    onCancel: () -> Unit,
    onSave: (Assignment) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var description by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }

    val formIsValid = title.isNotBlank() &&
            course.isNotBlank() &&
            dueDate.isNotBlank()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        PageTitle("New Assignment", "Add your next task")
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Assignment Title *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = course,
            onValueChange = { course = it },
            label = { Text("Course Name / Code *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = dueDate,
            onValueChange = { dueDate = it },
            label = { Text("Due Date *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("e.g. 20 Oct") }
        )

        Spacer(Modifier.height(15.dp))
        Text("Priority", fontWeight = FontWeight.Bold)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            listOf("Low", "Medium", "High").forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = priority == option,
                        onClick = { priority = option }
                    )
                    Text(option, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description / Notes") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = reminderEnabled,
                onCheckedChange = { reminderEnabled = it }
            )
            Text("Enable Reminder Notification")
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    onSave(
                        Assignment(
                            title = title.trim(),
                            course = course.trim(),
                            dueDate = dueDate.trim(),
                            priority = priority,
                            description = description.trim(),
                            reminderEnabled = reminderEnabled
                        )
                    )
                },
                enabled = formIsValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Assignment")
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CampusFlowPreview() {
    StudentorganizerTheme {
        DashboardScreen(
            studentName = "Student Name",
            assignments = listOf(
                Assignment("ML Project", "ML", "Tomorrow", "High"),
                Assignment("Database Lab", "DBMS", "Friday", "Medium")
            ),
            onAddAssignment = {},
            onViewAll = {},
            onToggleAssignment = {}
        )
    }
}
