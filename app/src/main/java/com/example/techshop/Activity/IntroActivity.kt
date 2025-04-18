package com.example.techshop.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.techshop.R
import com.example.techshop.ui.screen.ForgotPasswordDialog
import com.example.techshop.ui.screen.LoginScreen
import com.example.techshop.ui.screen.RegisterScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class IntroActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { signInTask ->
                        if (signInTask.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Đăng nhập Firebase thất bại: ${signInTask.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("GoogleSignIn", "Firebase Auth failed: ${signInTask.exception?.message}", signInTask.exception)
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(
                    this,
                    "Lỗi đăng nhập Google: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("GoogleSignIn", "Google Sign-In failed: ${e.statusCode} - ${e.message}", e)
            }
        } else {
            Toast.makeText(
                this,
                "Đăng nhập bị hủy hoặc thất bại.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setContent {
            var showLogin by remember { mutableStateOf(true) }
            var showForgotPassword by remember { mutableStateOf(false) }

            if (showLogin) {
                LoginScreen(
                    onLoginSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onSwitchToRegister = { showLogin = false },
                    onForgotPassword = { showForgotPassword = true },
                    onGoogleSignIn = { signInWithGoogle() }
                )
            } else {
                RegisterScreen(
                    onRegisterSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onSwitchToLogin = { showLogin = true }
                )
            }

            if (showForgotPassword) {
                ForgotPasswordDialog(
                    onDismiss = { showForgotPassword = false },
                    onResetPassword = { email ->
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showForgotPassword = false
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Gửi email thất bại: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                )
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }
}