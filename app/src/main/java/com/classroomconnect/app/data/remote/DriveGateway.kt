package com.classroomconnect.app.data.remote

import android.net.Uri

interface DriveGateway {
    suspend fun uploadSubmission(uri: Uri, folderId: String, fileName: String): DriveUpload
}

data class DriveUpload(val fileId: String, val webUrl: String)

class GoogleDriveGateway : DriveGateway {
    override suspend fun uploadSubmission(uri: Uri, folderId: String, fileName: String): DriveUpload {
        error("Configure Google Drive OAuth and the class folder ID")
    }
}
