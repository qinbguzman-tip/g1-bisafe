package com.example.g1_final_project.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.g1_final_project.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_register)

        supportActionBar?.title = "BiSafe"
        val intent = Intent(this, LoginActivity::class.java)

//        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
//            startActivity(intent)
//            finish()
//        }
//
//        findViewById<Button>(R.id.btnBackLogin).setOnClickListener {
//            startActivity(intent)
//            finish()
//        }
    }
}