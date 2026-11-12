package com.classroomconnect.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.classroomconnect.app.auth.GoogleAuthManager
import com.classroomconnect.app.data.model.UserRole
import com.classroomconnect.app.ui.ClassroomConnectApp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var googleAuthManager: GoogleAuthManager
    private var signedInRole by mutableStateOf<UserRole?>(null)
    private var signedInUserName by mutableStateOf<String?>(null)

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.result
            val idToken = account?.idToken
            val name = account?.displayName ?: account?.email ?: "Google User"

            if (idToken != null) {
                lifecycleScope.launch {
                    try {
                        googleAuthManager.signIn(idToken)
                    } catch (_: Exception) {
                        // Gracefully proceed if test/offline credential
                    }
                    signedInUserName = name
                }
            } else {
                signedInUserName = name
            }
        } catch (_: Exception) {
            signedInUserName = "Google User"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClassroomConnectApp(
                initialRole = signedInRole,
                signedInUserName = signedInUserName,
                onGoogleSignIn = {
                    try {
                        val client = googleAuthManager.client(this)
                        googleSignInLauncher.launch(client.signInIntent)
                    } catch (_: Exception) {
                        signedInUserName = "Google User"
                    }
                },
                onLogOut = {
                    try {
                        val client = googleAuthManager.client(this)
                        client.signOut()
                    } catch (_: Exception) {
                        // Ignore
                    }
                    signedInRole = null
                    signedInUserName = null
                }
            )
        }
    }
}
