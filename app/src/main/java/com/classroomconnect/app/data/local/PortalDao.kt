package com.classroomconnect.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.classroomconnect.app.data.model.AssignmentEntity
import com.classroomconnect.app.data.model.ClassEntity
import com.classroomconnect.app.data.model.GradeEntity
import com.classroomconnect.app.data.model.MessageEntity
import com.classroomconnect.app.data.model.SubjectEntity
import com.classroomconnect.app.data.model.SubmissionEntity
import com.classroomconnect.app.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PortalDao {
    @Query("SELECT * FROM assignments ORDER BY dueAtEpochMillis ASC")
    fun observeAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE id = :assignmentId")
    fun observeAssignment(assignmentId: String): Flow<AssignmentEntity?>

    @Query("SELECT * FROM submissions WHERE studentUid = :studentUid ORDER BY submittedAtEpochMillis DESC")
    fun observeStudentSubmissions(studentUid: String): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM grades WHERE studentUid = :studentUid ORDER BY gradedAtEpochMillis DESC")
    fun observeStudentGrades(studentUid: String): Flow<List<GradeEntity>>

    @Query("SELECT * FROM messages WHERE recipientUid = :userUid ORDER BY createdAtEpochMillis DESC")
    fun observeMessages(userUid: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM assignments WHERE isSynced = 0")
    suspend fun unsyncedAssignments(): List<AssignmentEntity>

    @Query("SELECT * FROM submissions WHERE isSynced = 0")
    suspend fun unsyncedSubmissions(): List<SubmissionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<ClassEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<AssignmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<SubmissionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrades(grades: List<GradeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateSubmission(submission: SubmissionEntity)

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)
}
