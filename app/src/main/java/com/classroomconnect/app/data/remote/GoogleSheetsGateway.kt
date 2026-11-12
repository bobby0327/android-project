package com.classroomconnect.app.data.remote

import com.classroomconnect.app.data.model.AssignmentEntity
import com.classroomconnect.app.data.model.GradeEntity
import com.classroomconnect.app.data.model.MessageEntity
import com.classroomconnect.app.data.model.SubmissionEntity

interface GoogleSheetsGateway {
    suspend fun appendAssignment(assignment: AssignmentEntity): Int
    suspend fun appendSubmission(submission: SubmissionEntity): Int
    suspend fun updateGrade(grade: GradeEntity): Int
    suspend fun appendMessage(message: MessageEntity): Int
    suspend fun pullAssignments(classId: String): List<AssignmentEntity>
}

/** Production implementation should use Sheets v4 batchUpdate with the teacher's class spreadsheet ID. */
class SheetsApiGateway : GoogleSheetsGateway {
    override suspend fun appendAssignment(assignment: AssignmentEntity): Int = error("Configure Google Sheets credentials and spreadsheet ID")
    override suspend fun appendSubmission(submission: SubmissionEntity): Int = error("Configure Google Sheets credentials and spreadsheet ID")
    override suspend fun updateGrade(grade: GradeEntity): Int = error("Configure Google Sheets credentials and spreadsheet ID")
    override suspend fun appendMessage(message: MessageEntity): Int = error("Configure Google Sheets credentials and spreadsheet ID")
    override suspend fun pullAssignments(classId: String): List<AssignmentEntity> = emptyList()
}
