package com.classroomconnect.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.classroomconnect.app.data.model.AssignmentEntity
import com.classroomconnect.app.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomConnectApp(
    initialRole: UserRole? = null,
    onGoogleSignIn: (UserRole) -> Unit = {}
) {
    var activeTab by remember { mutableStateOf("AUTH") } // AUTH, ADMIN, TEACHER, STUDENT, PARENT
    var subScreen by remember { mutableStateOf("LOGIN") } // Sub-screens for Figs 1-39

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SHAP - $activeTab Portal ($subScreen)") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = activeTab == "AUTH",
                    onClick = { activeTab = "AUTH"; subScreen = "LOGIN" },
                    label = { Text("Auth") },
                    icon = { Text("🔑") }
                )
                NavigationBarItem(
                    selected = activeTab == "ADMIN",
                    onClick = { activeTab = "ADMIN"; subScreen = "DASHBOARD" },
                    label = { Text("Admin") },
                    icon = { Text("⚙️") }
                )
                NavigationBarItem(
                    selected = activeTab == "TEACHER",
                    onClick = { activeTab = "TEACHER"; subScreen = "DASHBOARD" },
                    label = { Text("Teacher") },
                    icon = { Text("📚") }
                )
                NavigationBarItem(
                    selected = activeTab == "STUDENT",
                    onClick = { activeTab = "STUDENT"; subScreen = "DASHBOARD" },
                    label = { Text("Student") },
                    icon = { Text("🎓") }
                )
                NavigationBarItem(
                    selected = activeTab == "PARENT",
                    onClick = { activeTab = "PARENT"; subScreen = "DASHBOARD" },
                    label = { Text("Parent") },
                    icon = { Text("👪") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Sub-screen Selector Chips
            SubScreenSelector(activeTab, subScreen) { subScreen = it }

            Spacer(Modifier.height(8.dp))

            when (activeTab) {
                "AUTH" -> AuthScreens(subScreen) { onGoogleSignIn(it) }
                "ADMIN" -> AdminScreens(subScreen)
                "TEACHER" -> TeacherScreens(subScreen)
                "STUDENT" -> StudentScreens(subScreen)
                "PARENT" -> ParentScreens(subScreen)
            }
        }
    }
}

@Composable
private fun SubScreenSelector(activeTab: String, currentSubScreen: String, onSelect: (String) -> Unit) {
    val subScreens = when (activeTab) {
        "AUTH" -> listOf("SPLASH", "LOGIN", "REGISTER", "FORGOT_PW", "ROLE_ROUTING")
        "ADMIN" -> listOf("DASHBOARD", "USERS", "TEACHERS", "STUDENTS", "PARENTS", "CLASSES", "SUBJECTS", "REPORTS", "ANNOUNCEMENTS")
        "TEACHER" -> listOf("DASHBOARD", "HOMEWORK", "ASSIGNMENT", "LIST", "SUBMISSIONS", "GRADE", "ATTENDANCE", "NOTIFICATIONS")
        "STUDENT" -> listOf("DASHBOARD", "HOMEWORK_LIST", "HW_DETAILS", "ASSIGNMENTS", "ASSIGN_DETAILS", "SUBMIT", "STATUS", "GRADES", "ATTENDANCE", "NOTIFICATIONS")
        "PARENT" -> listOf("DASHBOARD", "CHILD_HW", "STATUS", "GRADES", "ATTENDANCE", "NOTIFICATIONS", "PERFORMANCE")
        else -> emptyList()
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(subScreens) { screen ->
            FilterChip(
                selected = currentSubScreen == screen,
                onClick = { onSelect(screen) },
                label = { Text(screen, style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}

@Composable
private fun AuthScreens(screen: String, onRoleSelect: (UserRole) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        when (screen) {
            "SPLASH" -> {
                Card(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SHAP Portal", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Text("School Homework & Assignment Portal", color = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                }
            }
            "LOGIN" -> {
                Text("Login to SHAP", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(value = "user@centurion.edu.in", onValueChange = {}, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "••••••••", onValueChange = {}, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Button(onClick = { onRoleSelect(UserRole.STUDENT) }, modifier = Modifier.fillMaxWidth()) { Text("Login with Firebase") }
            }
            "REGISTER" -> {
                Text("New User Registration", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(value = "Jordan Smith", onValueChange = {}, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "jordan@school.edu", onValueChange = {}, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "••••••••", onValueChange = {}, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Register Account") }
            }
            "FORGOT_PW" -> {
                Text("Password Recovery", style = MaterialTheme.typography.headlineMedium)
                Text("Enter your email to receive a password reset link.", color = Color.Gray)
                OutlinedTextField(value = "user@school.edu", onValueChange = {}, label = { Text("Registered Email") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Send Reset Email") }
            }
            "ROLE_ROUTING" -> {
                Text("Select Role Dashboard", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { onRoleSelect(UserRole.ADMIN) }, modifier = Modifier.fillMaxWidth()) { Text("Admin Portal") }
                Button(onClick = { onRoleSelect(UserRole.TEACHER) }, modifier = Modifier.fillMaxWidth()) { Text("Teacher Portal") }
                Button(onClick = { onRoleSelect(UserRole.STUDENT) }, modifier = Modifier.fillMaxWidth()) { Text("Student Portal") }
                Button(onClick = { onRoleSelect(UserRole.PARENT) }, modifier = Modifier.fillMaxWidth()) { Text("Parent Portal") }
            }
        }
    }
}

@Composable
private fun AdminScreens(screen: String) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Admin Portal - $screen", style = MaterialTheme.typography.titleLarge) }
        when (screen) {
            "DASHBOARD" -> {
                item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Total Users: 245"); Text("Teachers: 18 | Students: 180 | Parents: 47") } } }
            }
            "USERS" -> {
                items(listOf("Admin User", "Maya Teacher", "Jordan Student", "Mr. Smith Parent")) { name ->
                    Card(Modifier.fillMaxWidth()) { Text(name, Modifier.padding(16.dp)) }
                }
            }
            "TEACHERS" -> {
                items(listOf("Maya Lin - Mathematics", "Robert Bruce - Physics", "Elena Vance - English")) { t ->
                    Card(Modifier.fillMaxWidth()) { Text(t, Modifier.padding(16.dp)) }
                }
            }
            "STUDENTS" -> {
                items(listOf("Jordan Smith - Class 10A", "Alex Johnson - Class 10A", "Taylor Swift - Class 10B")) { s ->
                    Card(Modifier.fillMaxWidth()) { Text(s, Modifier.padding(16.dp)) }
                }
            }
            "PARENTS" -> {
                items(listOf("Mr. Smith -> Linked to Jordan Smith", "Mrs. Johnson -> Linked to Alex Johnson")) { p ->
                    Card(Modifier.fillMaxWidth()) { Text(p, Modifier.padding(16.dp)) }
                }
            }
            "CLASSES" -> {
                items(listOf("Class 10A - Section A", "Class 10B - Section B")) { c ->
                    Card(Modifier.fillMaxWidth()) { Text(c, Modifier.padding(16.dp)) }
                }
            }
            "SUBJECTS" -> {
                items(listOf("Mathematics - Code MATH101", "Physics - Code PHYS101", "English - Code ENG101")) { sub ->
                    Card(Modifier.fillMaxWidth()) { Text(sub, Modifier.padding(16.dp)) }
                }
            }
            "REPORTS" -> {
                item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Academic Performance Index: 94.2%"); Text("Submission Compliance: 91.8%") } } }
            }
            "ANNOUNCEMENTS" -> {
                item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("School Mid-Term Exams Schedule"); Text("Posted to all Students & Parents", color = Color.Gray) } } }
            }
        }
    }
}

@Composable
private fun TeacherScreens(screen: String) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Teacher Portal - $screen", style = MaterialTheme.typography.titleLarge) }
        when (screen) {
            "DASHBOARD" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Assigned Classes: 10A, 10B"); Text("Pending Grading: 3 Submissions") } } } }
            "HOMEWORK" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Upload Homework"); OutlinedTextField("Math Problem Set", {}, label = { Text("Title") }); Button({}) { Text("Publish Homework") } } } } }
            "ASSIGNMENT" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Upload Assignment PDF"); Button({}) { Text("Attach File from Drive") } } } } }
            "LIST" -> { items(listOf("Quadratic Equations", "Wave Optics Lab", "Macbeth Essay")) { a -> Card(Modifier.fillMaxWidth()) { Text(a, Modifier.padding(16.dp)) } } }
            "SUBMISSIONS" -> { items(listOf("Jordan Smith - Submitted On-Time", "Alex Johnson - Submitted Late")) { sub -> Card(Modifier.fillMaxWidth()) { Text(sub, Modifier.padding(16.dp)) } } }
            "GRADE" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Grading: Jordan Smith"); OutlinedTextField("95", {}, label = { Text("Marks / 100") }); OutlinedTextField("Excellent work!", {}, label = { Text("Feedback") }); Button({}) { Text("Submit Grade") } } } } }
            "ATTENDANCE" -> { items(listOf("Jordan Smith: Present [X]", "Alex Johnson: Absent [ ]")) { att -> Card(Modifier.fillMaxWidth()) { Text(att, Modifier.padding(16.dp)) } } }
            "NOTIFICATIONS" -> { item { Card(Modifier.fillMaxWidth()) { Text("New submission received from Jordan Smith", Modifier.padding(16.dp)) } } }
        }
    }
}

@Composable
private fun StudentScreens(screen: String) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Student Portal - $screen", style = MaterialTheme.typography.titleLarge) }
        when (screen) {
            "DASHBOARD" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Welcome Jordan!"); Text("Pending Tasks: 3 | Overall Score: 94%") } } } }
            "HOMEWORK_LIST" -> { items(listOf("Math Exercises 1-15", "Physics Observation Sheet")) { hw -> Card(Modifier.fillMaxWidth()) { Text(hw, Modifier.padding(16.dp)) } } }
            "HW_DETAILS" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Math Exercises 1-15"); Text("Solve page 142 quadratic equations and attach solutions.", color = Color.Gray) } } } }
            "ASSIGNMENTS" -> { items(listOf("Quadratic Equations Set", "Wave Optics Lab")) { a -> Card(Modifier.fillMaxWidth()) { Text(a, Modifier.padding(16.dp)) } } }
            "ASSIGN_DETAILS" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Wave Optics Lab Report"); Text("Due Date: Nov 4, 2026. Attach PDF report.", color = Color.Gray) } } } }
            "SUBMIT" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Upload Submission File"); Button({}) { Text("Select PDF / Drive File") }; Button({}) { Text("Submit Assignment") } } } } }
            "STATUS" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Submission Status: SUBMITTED (On-Time)", color = Color(0xFF2E7D32)); Text("Drive File: wave_optics_report.pdf") } } } }
            "GRADES" -> { items(listOf("Math: 95/100 (A)", "Physics: 92/100 (A)")) { g -> Card(Modifier.fillMaxWidth()) { Text(g, Modifier.padding(16.dp)) } } }
            "ATTENDANCE" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Attendance Record: 96% Present"); Text("Total Days: 120 | Present: 115") } } } }
            "NOTIFICATIONS" -> { item { Card(Modifier.fillMaxWidth()) { Text("New grade posted in Mathematics: 95/100", Modifier.padding(16.dp)) } } }
        }
    }
}

@Composable
private fun ParentScreens(screen: String) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Parent Portal - $screen", style = MaterialTheme.typography.titleLarge) }
        when (screen) {
            "DASHBOARD" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Child: Jordan Smith (Class 10A)"); Text("Overall Grade Trend: A (94.0%)") } } } }
            "CHILD_HW" -> { items(listOf("Math Quadratic Equations - Due Nov 2", "Physics Optics Lab - Due Nov 4")) { hw -> Card(Modifier.fillMaxWidth()) { Text(hw, Modifier.padding(16.dp)) } } }
            "STATUS" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Submission Compliance: 92% On-Time"); Text("1 Draft Pending") } } } }
            "GRADES" -> { items(listOf("Mathematics: 95/100 - Excellent!", "Physics: 92/100 - Great work!")) { g -> Card(Modifier.fillMaxWidth()) { Text(g, Modifier.padding(16.dp)) } } }
            "ATTENDANCE" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Child Attendance Rate: 96.0%"); Text("Absences: 5 days (Excused)") } } } }
            "NOTIFICATIONS" -> { item { Card(Modifier.fillMaxWidth()) { Text("Teacher Maya posted new grade for Jordan Smith", Modifier.padding(16.dp)) } } }
            "PERFORMANCE" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Academic Performance Analysis", style = MaterialTheme.typography.titleMedium); Text("GPA: 3.9 / 4.0 | Class Rank: 3rd"); Text("Grade Trend: Stable A Range", color = Color(0xFF2E7D32)) } } } }
        }
    }
}

private fun UserRole.description() = when (this) {
    UserRole.STUDENT -> "Track tasks, save drafts offline, and submit work."
    UserRole.TEACHER -> "Post assignments, review submissions, and enter grades."
    UserRole.PARENT -> "Follow pending tasks, grades, and teacher messages."
    UserRole.ADMIN -> "Manage users, classes, subjects, and publish announcements."
}
