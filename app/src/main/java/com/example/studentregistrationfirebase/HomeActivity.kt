package com.example.studentregistrationfirebase

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.studentregistrationfirebase.Model.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {
    private lateinit var studentList: ListView
    private lateinit var addBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var dbRef: DatabaseReference
    private val students = mutableListOf<Student>()
    private var currentUserDept: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        studentList = findViewById(R.id.studentList)
        addBtn = findViewById(R.id.addStudentBtn)
        logoutBtn = findViewById(R.id.logoutBtn)

        dbRef = FirebaseDatabase.getInstance().getReference("students")
        addBtn.setOnClickListener {
            startActivity(Intent(this, AddStudentActivity::class.java))
        }
        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                students.clear()
                for (snap in snapshot.children) {
                    val student = snap.getValue(Student::class.java)
                    student?.let { students.add(it) }
                }

                students.sortBy { it.dateOfRegistration }

                val userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("user_id", "")
                val currentStudent = students.find { it.studentId == userId }
                currentUserDept = currentStudent?.department

                val adapter = object : ArrayAdapter<Student>(
                    this@HomeActivity,
                    android.R.layout.simple_list_item_1,
                    students
                ) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getView(position, convertView, parent)
                        val textView = view.findViewById<TextView>(android.R.id.text1)
                        val s = students[position]
                        textView.text = "${s.name} (${s.studentId}) - ${s.department}"
                        if (s.department == currentUserDept) {
                            textView.setTextColor(Color.BLUE)
                        } else {
                            textView.setTextColor(Color.BLACK)
                        }
                        return view
                    }
                }

                studentList.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
