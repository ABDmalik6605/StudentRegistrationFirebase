package com.example.studentregistrationfirebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.studentregistrationfirebase.Model.Student
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast

class AddStudentActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var studentId: EditText
    private lateinit var name: EditText
    private lateinit var department: EditText
    private lateinit var year: EditText
    private lateinit var date: EditText
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        dbRef = FirebaseDatabase.getInstance().getReference("students")

        studentId = findViewById(R.id.studentId)
        name = findViewById(R.id.name)
        department = findViewById(R.id.department)
        year = findViewById(R.id.year)
        date = findViewById(R.id.registrationDate)
        saveBtn = findViewById(R.id.saveBtn)

        saveBtn.setOnClickListener {
            val s = Student(
                studentId.text.toString(),
                name.text.toString(),
                department.text.toString(),
                year.text.toString(),
                date.text.toString()
            )
            dbRef.child(s.studentId).setValue(s).addOnSuccessListener {
                Toast.makeText(this, "Student Added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
