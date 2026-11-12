package com.classroomconnect.app.data.remote

import android.net.Uri
import android.util.Log
import com.classroomconnect.app.data.model.AssignmentEntity
import com.classroomconnect.app.data.model.SubmissionEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseGateway @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val messaging: FirebaseMessaging,
    private val remoteConfig: FirebaseRemoteConfig
) {
    suspend fun subscribeToClass(classId: String) = messaging.subscribeToTopic("class_$classId").await()
    suspend fun subscribeToUser(userId: String) = messaging.subscribeToTopic("user_$userId").await()

    suspend fun saveAssignmentToFirestore(assignment: AssignmentEntity) {
        val data = hashMapOf(
            "id" to assignment.id,
            "classId" to assignment.classId,
            "subjectId" to assignment.subjectId,
            "title" to assignment.title,
            "instructions" to assignment.instructions,
            "dueAtEpochMillis" to assignment.dueAtEpochMillis,
            "createdByUid" to assignment.createdByUid
        )
        try {
            firestore.collection("assignments").document(assignment.id).set(data).await()
            Log.d("FirestoreSync", "Successfully saved assignment: ${assignment.id}")
        } catch (e: Throwable) {
            Log.e("FirestoreSync", "Error saving assignment: ${e.message}", e)
        }
    }

    suspend fun saveSubmissionToFirestore(submission: SubmissionEntity) {
        val data = hashMapOf(
            "id" to submission.id,
            "assignmentId" to submission.assignmentId,
            "studentUid" to submission.studentUid,
            "submittedAtEpochMillis" to (submission.submittedAtEpochMillis ?: System.currentTimeMillis()),
            "driveFileUrl" to (submission.driveFileUrl ?: ""),
            "isLate" to submission.isLate,
            "status" to submission.status
        )
        try {
            firestore.collection("submissions").document(submission.id).set(data).await()
            Log.d("FirestoreSync", "Successfully saved submission: ${submission.id}")
        } catch (e: Throwable) {
            Log.e("FirestoreSync", "Error saving submission: ${e.message}", e)
        }
    }

    suspend fun uploadFileToStorage(uri: Uri, path: String): String? {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "Upload failed: ${e.message}", e)
            null
        }
    }

    suspend fun fetchSchoolCalendar(): Map<String, Any> {
        remoteConfig.fetchAndActivate().await()
        return remoteConfig.getString("school_calendar").let { emptyMap() }
    }
}
