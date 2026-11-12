package com.classroomconnect.app.data

import android.net.Uri
import com.classroomconnect.app.data.local.PortalDao
import com.classroomconnect.app.data.model.AssignmentEntity
import com.classroomconnect.app.data.model.GradeEntity
import com.classroomconnect.app.data.model.SubmissionEntity
import com.classroomconnect.app.data.remote.DriveGateway
import com.classroomconnect.app.data.remote.FirebaseGateway
import com.classroomconnect.app.data.remote.GoogleSheetsGateway
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortalRepository @Inject constructor(
    private val dao: PortalDao,
    private val sheets: GoogleSheetsGateway,
    private val drive: DriveGateway,
    private val firebaseGateway: FirebaseGateway
) {
    fun observeAssignments(): Flow<List<AssignmentEntity>> = dao.observeAssignments()
    fun observeSubmissions(studentUid: String): Flow<List<SubmissionEntity>> = dao.observeStudentSubmissions(studentUid)
    fun observeGrades(studentUid: String): Flow<List<GradeEntity>> = dao.observeStudentGrades(studentUid)

    suspend fun cacheAssignments(assignments: List<AssignmentEntity>) {
        dao.insertAssignments(assignments)
        assignments.forEach { assignment ->
            firebaseGateway.saveAssignmentToFirestore(assignment)
        }
    }

    suspend fun saveGrade(grade: GradeEntity) = dao.insertGrades(listOf(grade))

    suspend fun seedInitialDataIfEmpty() {
        val initialAssignments = listOf(
            AssignmentEntity(
                id = "assign_math_1",
                classId = "class_101",
                subjectId = "subj_math",
                title = "Mathematics: Quadratic Equations Set",
                instructions = "Solve problems 1-15 on page 142. Show all step-by-step working.",
                dueAtEpochMillis = System.currentTimeMillis() + 86400000L * 2,
                createdByUid = "teacher_maya"
            ),
            AssignmentEntity(
                id = "assign_physics_1",
                classId = "class_101",
                subjectId = "subj_physics",
                title = "Physics: Wave Optics Lab Report",
                instructions = "Document diffraction experiment observations and plot wavelength calculations.",
                dueAtEpochMillis = System.currentTimeMillis() + 86400000L * 4,
                createdByUid = "teacher_maya"
            ),
            AssignmentEntity(
                id = "assign_english_1",
                classId = "class_101",
                subjectId = "subj_english",
                title = "English: Shakespeare Critical Essay",
                instructions = "Write a 500-word analysis on thematic irony in Macbeth Act III.",
                dueAtEpochMillis = System.currentTimeMillis() + 86400000L * 5,
                createdByUid = "teacher_maya"
            )
        )
        dao.insertAssignments(initialAssignments)

        val initialGrades = listOf(
            GradeEntity(
                id = "grade_1",
                submissionId = "sub_math_jordan",
                assignmentId = "assign_math_0",
                studentUid = "student_jordan",
                score = 95.0,
                maxScore = 100.0,
                feedback = "Excellent working and clear step-by-step solutions!",
                gradedAtEpochMillis = System.currentTimeMillis() - 86400000L * 1
            ),
            GradeEntity(
                id = "grade_2",
                submissionId = "sub_physics_jordan",
                assignmentId = "assign_physics_0",
                studentUid = "student_jordan",
                score = 92.0,
                maxScore = 100.0,
                feedback = "Great lab report structure and accurate data plots.",
                gradedAtEpochMillis = System.currentTimeMillis() - 86400000L * 3
            )
        )
        dao.insertGrades(initialGrades)
    }

    suspend fun saveDraft(assignment: AssignmentEntity, studentUid: String, localUri: Uri?) {
        dao.insertSubmissions(listOf(SubmissionEntity(
            id = "${assignment.id}_$studentUid",
            assignmentId = assignment.id,
            studentUid = studentUid,
            localDraftUri = localUri?.toString(),
            status = "DRAFT"
        )))
    }

    suspend fun submitWorkWithAttachment(submission: SubmissionEntity, fileUri: Uri, dueAtMillis: Long): SubmissionEntity {
        // Upload file to Firebase Storage first
        val path = "submissions/${submission.assignmentId}/${submission.studentUid}_${System.currentTimeMillis()}"
        val storageUrl = firebaseGateway.uploadFileToStorage(fileUri, path)

        val submittedAt = Instant.now().toEpochMilli()
        val finalSubmission = submission.copy(
            submittedAtEpochMillis = submittedAt,
            isLate = submittedAt > dueAtMillis,
            status = "SUBMITTED",
            driveFileUrl = storageUrl ?: submission.driveFileUrl,
            isSynced = false
        )
        dao.insertSubmissions(listOf(finalSubmission))
        firebaseGateway.saveSubmissionToFirestore(finalSubmission)
        return finalSubmission
    }

    suspend fun submitWork(submission: SubmissionEntity, dueAtMillis: Long): SubmissionEntity {
        val submittedAt = Instant.now().toEpochMilli()
        val finalSubmission = submission.copy(
            submittedAtEpochMillis = submittedAt,
            isLate = submittedAt > dueAtMillis,
            status = "SUBMITTED",
            isSynced = false
        )
        dao.insertSubmissions(listOf(finalSubmission))
        firebaseGateway.saveSubmissionToFirestore(finalSubmission)
        return finalSubmission
    }

    suspend fun uploadAndSubmit(submission: SubmissionEntity, fileUri: Uri, folderId: String, dueAtMillis: Long): SubmissionEntity {
        val upload = drive.uploadSubmission(fileUri, folderId, "${submission.assignmentId}_${submission.studentUid}")
        return submitWork(submission.copy(driveFileId = upload.fileId, driveFileUrl = upload.webUrl), dueAtMillis)
    }

    suspend fun syncPending() {
        dao.unsyncedAssignments().forEach { assignment ->
            val row = sheets.appendAssignment(assignment)
            dao.updateAssignment(assignment.copy(sheetRow = row, isSynced = true))
        }
        dao.unsyncedSubmissions().forEach { submission ->
            val row = sheets.appendSubmission(submission)
            dao.updateSubmission(submission.copy(sheetRow = row, isSynced = true))
        }
    }
}
