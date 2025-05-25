package com.example.studentregistrationfirebase

import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuthException

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginBtn: Button
    private lateinit var resetPassword: TextView
    private lateinit var signUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedUserId = prefs.getString("user_id", null)

        if (savedUserId != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginBtn)
        resetPassword = findViewById(R.id.resetPassword)
        signUp = findViewById(R.id.noAccount)

        loginBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        prefs.edit().putString("user_id", userId).apply()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        val exception = it.exception
                        exception?.message?.let { it1 -> Log.d("Exception", it1) }

                        if (exception is FirebaseAuthException) {
                            Toast.makeText(this, "User can be authenticated. Redirecting to signup.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, SignupActivity::class.java))
                        } else {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
        resetPassword.setOnClickListener {
            val email = emailField.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        signUp.setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
