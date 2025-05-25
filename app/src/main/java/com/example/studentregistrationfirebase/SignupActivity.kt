package com.example.studentregistrationfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var signupBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        signupBtn = findViewById(R.id.signupBtn)

        signupBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                val signInMethods = task.result?.signInMethods
                if (signInMethods != null && signInMethods.isNotEmpty()) {
                    Toast.makeText(this, "Account already exists. Redirecting to Login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { registerTask ->
                            if (registerTask.isSuccessful) {
                                val userId = auth.currentUser?.uid ?: ""
                                getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                    .edit().putString("user_id", userId).apply()

                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Signup failed: ${registerTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}
