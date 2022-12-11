package com.example.g1_final_project.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.g1_final_project.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)
        supportActionBar?.title = "BiSafe"

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val login = Intent(this, MainActivity::class.java)
            startActivity(login)
        }

        findViewById<Button>(R.id.btnCreate).setOnClickListener {
            val register = Intent(this, RegisterActivity::class.java)
            startActivity(register)
        }
    }
}