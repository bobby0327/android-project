package com.classroomconnect.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole { STUDENT, TEACHER, PARENT, ADMIN }

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey val id: String,
    val name: String,
    val section: String,
    val teacherUid: String,
    val schoolYear: String = "2026-27"
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val classId: String,
    val name: String,
    val color: String
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey val id: String,
    val classId: String,
    val subjectId: String,
    val title: String,
    val instructions: String,
    val dueAtEpochMillis: Long,
    val createdByUid: String,
    val sheetRow: Int? = null,
    val isSynced: Boolean = false
)

@Entity(tableName = "submissions")
data class SubmissionEntity(
    @PrimaryKey val id: String,
    val assignmentId: String,
    val studentUid: String,
    val submittedAtEpochMillis: Long? = null,
    val driveFileId: String? = null,
    val driveFileUrl: String? = null,
    val localDraftUri: String? = null,
    val isLate: Boolean = false,
    val status: String = "DRAFT",
    val sheetRow: Int? = null,
    val isSynced: Boolean = false
)

@Entity(tableName = "grades")
data class GradeEntity(
    @PrimaryKey val id: String,
    val submissionId: String,
    val assignmentId: String,
    val studentUid: String,
    val score: Double,
    val maxScore: Double,
    val feedback: String,
    val gradedAtEpochMillis: Long,
    val sheetRow: Int? = null,
    val isSynced: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderUid: String,
    val recipientUid: String,
    val classId: String?,
    val subject: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val isRead: Boolean = false,
    val sheetRow: Int? = null,
    val isSynced: Boolean = false
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String,
    val role: UserRole,
    val classSection: String? = null,
    val childUid: String? = null
)
