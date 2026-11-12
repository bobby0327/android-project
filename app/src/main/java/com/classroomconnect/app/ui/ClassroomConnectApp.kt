package com.classroomconnect.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.classroomconnect.app.R
import com.classroomconnect.app.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomConnectApp(
    initialRole: UserRole? = null,
    signedInUserName: String? = null,
    onGoogleSignIn: () -> Unit = {},
    onLogOut: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    var subScreen by remember { mutableStateOf("DASHBOARD") }

    LaunchedEffect(initialRole, signedInUserName) {
        if (initialRole != null) {
            viewModel.userRole = initialRole
        }
        if (signedInUserName != null) {
            viewModel.signedInUserName = signedInUserName
        }
    }

    val userRole = viewModel.userRole
    val currentUserName = viewModel.signedInUserName

    if (currentUserName == null && userRole == null) {
        FirstGoogleLoginScreen(
            onGoogleSignIn = {
                viewModel.loginWithGoogle(signedInUserName)
                onGoogleSignIn()
            }
        )
    } else if (userRole == null) {
        CleanRoleSelectionScreen(
            userName = currentUserName ?: "Google User",
            onSelectRole = { role ->
                viewModel.selectRole(role)
                subScreen = "DASHBOARD"
            }
        )
    } else {
        val portalName = when (userRole) {
            UserRole.TEACHER -> "TEACHER"
            UserRole.STUDENT -> "STUDENT"
            else -> "PORTAL"
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_logo),
                                contentDescription = "App Logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("${userRole.name.lowercase().replaceFirstChar { it.uppercase() }} Portal")
                        }
                    },
                    actions = {
                        TextButton(onClick = { 
                            viewModel.signOut()
                            onLogOut()
                        }) {
                            Text("Log Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                SubScreenSelector(portalName, subScreen) { subScreen = it }

                Spacer(Modifier.height(8.dp))

                when (userRole) {
                    UserRole.TEACHER -> TeacherScreens(subScreen, currentUserName, viewModel)
                    UserRole.STUDENT -> StudentScreens(subScreen, currentUserName, viewModel)
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun FirstGoogleLoginScreen(onGoogleSignIn: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFBFAF8)).padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = "SHAP Emblem",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(20.dp))
        Text("classroom.", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(
            "School Homework & Assignment Portal",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF787681)
        )
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Log in with Google", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Teachers use school Google Workspace accounts. Students use personal Gmail.",
            color = Color(0xFF9E9E9E),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun CleanRoleSelectionScreen(userName: String, onSelectRole: (UserRole) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFBFAF8)).padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = "SHAP Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text("Welcome, $userName!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text("Select your account role to continue:", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF787681))
        Spacer(Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onSelectRole(UserRole.TEACHER) },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("📚 Teacher Portal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Post homework/tasks for Section A or B and manage grades", color = Color(0xFF49454F))
            }
        }
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onSelectRole(UserRole.STUDENT) },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("🎓 Student Portal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("View homework by section, attach PDF/picture files, and submit work", color = Color(0xFF49454F))
            }
        }
    }
}

@Composable
private fun SubScreenSelector(activeTab: String, currentSubScreen: String, onSelect: (String) -> Unit) {
    val subScreens = when (activeTab) {
        "TEACHER" -> listOf("DASHBOARD", "HOMEWORK", "ASSIGNMENT", "LIST", "SUBMISSIONS", "GRADE", "ATTENDANCE", "NOTIFICATIONS")
        "STUDENT" -> listOf("DASHBOARD", "HOMEWORK_LIST", "HW_DETAILS", "ASSIGNMENTS", "ASSIGN_DETAILS", "SUBMIT", "STATUS", "GRADES", "ATTENDANCE", "NOTIFICATIONS")
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
private fun TeacherScreens(screen: String, userName: String?, viewModel: DashboardViewModel) {
    val name = userName ?: "Teacher Maya"
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    var taskTitle by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Mathematics") }
    var targetSection by remember { mutableStateOf("Section A") }
    var taskInstructions by remember { mutableStateOf("") }
    var publishedMessage by remember { mutableStateOf<String?>(null) }

    val subjects = listOf("Mathematics", "Physics", "English", "Computer Science")
    val sections = listOf("Section A", "Section B", "All Sections")

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Teacher Portal - $screen", style = MaterialTheme.typography.titleLarge) }

        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Assign Task / Post Homework", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    Text("Target Section / Class:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(sections) { sec ->
                            FilterChip(
                                selected = targetSection == sec,
                                onClick = { targetSection = sec },
                                label = { Text(sec) }
                            )
                        }
                    }

                    Text("Select Subject:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(subjects) { subject ->
                            FilterChip(
                                selected = selectedSubject == subject,
                                onClick = { selectedSubject = subject },
                                label = { Text(subject) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = taskInstructions,
                        onValueChange = { taskInstructions = it },
                        label = { Text("Instructions / Details") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (taskTitle.isNotBlank()) {
                                viewModel.postAssignment("$selectedSubject: $taskTitle", taskInstructions.ifBlank { "Complete and submit via Drive." }, section = targetSection)
                                publishedMessage = "Task '$taskTitle' posted for $targetSection ($selectedSubject) and saved live to Database!"
                                taskTitle = ""
                                taskInstructions = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publish Task to $targetSection & Save")
                    }
                    publishedMessage?.let { msg ->
                        Text(msg, color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        when (screen) {
            "DASHBOARD" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Welcome, $name!"); Text("Assigned Classes: Section A, Section B"); Text("Total Class Assignments: ${assignments.size}") } } } }
            "HOMEWORK", "ASSIGNMENT" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Active Class Assignments (${assignments.size})") } } } }
            "LIST" -> { items(assignments, key = { it.id }) { a -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(a.title, fontWeight = FontWeight.Bold); Text(a.instructions, color = Color.Gray) } } } }
            "SUBMISSIONS" -> { items(listOf("Jordan Smith (Sec A) - Submitted On-Time", "Alex Johnson (Sec B) - Submitted Late")) { sub -> Card(Modifier.fillMaxWidth()) { Text(sub, Modifier.padding(16.dp)) } } }
            "GRADE" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Grading: Jordan Smith (Section A)"); OutlinedTextField("95", {}, label = { Text("Marks / 100") }); OutlinedTextField("Excellent work!", {}, label = { Text("Feedback") }); Button({}) { Text("Submit Grade") } } } } }
            "ATTENDANCE" -> { items(listOf("Jordan Smith (Section A): Present [X]", "Alex Johnson (Section B): Absent [ ]")) { att -> Card(Modifier.fillMaxWidth()) { Text(att, Modifier.padding(16.dp)) } } }
            "NOTIFICATIONS" -> { item { Card(Modifier.fillMaxWidth()) { Text("New submission received from Jordan Smith (Section A)", Modifier.padding(16.dp)) } } }
        }
    }
}

@Composable
private fun StudentScreens(screen: String, userName: String?, viewModel: DashboardViewModel) {
    val name = userName ?: "Jordan Smith"
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    var studentSection by remember { mutableStateOf("Section A") }
    var selectedSubject by remember { mutableStateOf("All Subjects") }
    var submittedTaskMessage by remember { mutableStateOf<String?>(null) }
    
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedFileUri = uri
        selectedFileName = uri?.lastPathSegment ?: "Unknown File"
    }

    val studentSections = listOf("Section A", "Section B")
    val filterSubjects = listOf("All Subjects", "Mathematics", "Physics", "English", "Computer Science")

    val filteredAssignments = assignments.filter { a ->
        val matchesSection = a.classId.contains(studentSection, ignoreCase = true) ||
                a.title.contains(studentSection, ignoreCase = true) ||
                a.classId == "All Sections" ||
                a.title.contains("All Sections", ignoreCase = true)
        val matchesSubject = if (selectedSubject == "All Subjects") true else a.title.contains(selectedSubject, ignoreCase = true)
        matchesSection && matchesSubject
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Student Portal - $screen", style = MaterialTheme.typography.titleLarge) }

        // Dedicated PDF / Picture File Attachment & Submission Section
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Submit Task with File Attachment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    Text("1. Select Your Enrolled Section:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(studentSections) { sec ->
                            FilterChip(
                                selected = studentSection == sec,
                                onClick = { studentSection = sec },
                                label = { Text(sec) }
                            )
                        }
                    }

                    Text("2. Select Preferred Subject:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(filterSubjects) { subject ->
                            FilterChip(
                                selected = selectedSubject == subject,
                                onClick = { selectedSubject = subject },
                                label = { Text(subject) }
                            )
                        }
                    }

                    Button(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Browse Device Files / Pictures")
                    }

                    if (selectedFileUri != null) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("📄", style = MaterialTheme.typography.headlineSmall)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(selectedFileName ?: "File attached", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Status: Ready for Cloud Upload", color = Color(0xFF1976D2), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    } else {
                        Text("No file selected", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                    
                    Button(
                        onClick = {
                            val target = filteredAssignments.firstOrNull() ?: assignments.firstOrNull()
                            if (target != null && selectedFileUri != null) {
                                viewModel.submitWorkWithFile(target, fileUri = selectedFileUri!!)
                                submittedTaskMessage = "Uploading file and submitting '${target.title}' to Database!"
                            } else if (target != null) {
                                // Fallback if no file picked
                                viewModel.submitWork(target)
                                submittedTaskMessage = "Submitted '${target.title}' without file to Database!"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Selected File to Database")
                    }
                    submittedTaskMessage?.let { msg ->
                        Text(msg, color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        when (screen) {
            "DASHBOARD" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Welcome, $name! ($studentSection)"); Text("Active Tasks for $studentSection: ${filteredAssignments.size}") } } } }
            "HOMEWORK_LIST", "ASSIGNMENTS" -> { items(filteredAssignments, key = { it.id }) { a -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(a.title, fontWeight = FontWeight.Bold); Text(a.instructions, color = Color.Gray) } } } }
            "HW_DETAILS", "ASSIGN_DETAILS" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Math Exercises 1-15 ($studentSection)"); Text("Solve page 142 quadratic equations and attach solutions.", color = Color.Gray) } } } }
            "SUBMIT" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Upload Submission File for $studentSection"); Button({}) { Text("Select PDF / Drive File") } } } } }
            "STATUS" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Submission Status: SUBMITTED (On-Time)", color = Color(0xFF2E7D32)); Text("Drive File: wave_optics_report.pdf ($studentSection)") } } } }
            "GRADES" -> { items(listOf("Math ($studentSection): 95/100 (A)", "Physics ($studentSection): 92/100 (A)")) { g -> Card(Modifier.fillMaxWidth()) { Text(g, Modifier.padding(16.dp)) } } }
            "ATTENDANCE" -> { item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Attendance Record ($studentSection): 96% Present"); Text("Total Days: 120 | Present: 115") } } } }
            "NOTIFICATIONS" -> { item { Card(Modifier.fillMaxWidth()) { Text("New grade posted for $studentSection in Mathematics: 95/100", Modifier.padding(16.dp)) } } }
        }
    }
}
