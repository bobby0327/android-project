package com.classroomconnect.app.ui

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroomconnect.app.data.PortalRepository
import com.classroomconnect.app.data.model.AssignmentEntity
import com.classroomconnect.app.data.model.GradeEntity
import com.classroomconnect.app.data.model.SubmissionEntity
import com.classroomconnect.app.data.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: PortalRepository
) : ViewModel() {
    var userRole by mutableStateOf<UserRole?>(null)
    var signedInUserName by mutableStateOf<String?>(null)

    val assignments: StateFlow<List<AssignmentEntity>> = repository.observeAssignments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val grades: StateFlow<List<GradeEntity>> = repository.observeGrades("student_jordan")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun loginWithGoogle(userName: String?) {
        signedInUserName = if (!userName.isNullOrBlank()) userName else "Google User"
    }

    fun selectRole(role: UserRole) {
        userRole = role
        if (signedInUserName == null) {
            signedInUserName = "Google User"
        }
    }

    fun signOut() {
        userRole = null
        signedInUserName = null
    }

    fun postAssignment(title: String, instructions: String, section: String = "Section A") {
        viewModelScope.launch {
            val newAssignment = AssignmentEntity(
                id = "assignment_${System.currentTimeMillis()}",
                classId = if (section.isBlank()) "Section A" else section,
                subjectId = "subject_math",
                title = if (title.contains(section, ignoreCase = true)) title else "$title [$section]",
                instructions = instructions,
                dueAtEpochMillis = System.currentTimeMillis() + 86400000L * 3,
                createdByUid = "teacher_maya"
            )
            repository.cacheAssignments(listOf(newAssignment))
        }
    }

    fun submitWorkWithFile(assignment: AssignmentEntity, studentUid: String = "student_jordan", fileUri: Uri) {
        viewModelScope.launch {
            val submission = SubmissionEntity(
                id = "${assignment.id}_$studentUid",
                assignmentId = assignment.id,
                studentUid = studentUid,
            )
            repository.submitWorkWithAttachment(submission, fileUri, assignment.dueAtEpochMillis)
        }
    }

    fun submitWork(assignment: AssignmentEntity, studentUid: String = "student_jordan", fileName: String = "homework_solution.pdf") {
        viewModelScope.launch {
            val submission = SubmissionEntity(
                id = "${assignment.id}_$studentUid",
                assignmentId = assignment.id,
                studentUid = studentUid,
                driveFileId = fileName,
                driveFileUrl = "https://t-1000-5266b.firebasestorage.app/submissions/$fileName"
            )
            repository.submitWork(submission, assignment.dueAtEpochMillis)
        }
    }
}
