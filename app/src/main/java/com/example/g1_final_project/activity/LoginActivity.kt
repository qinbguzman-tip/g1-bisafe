package com.example.g1_final_project.activity

import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.os.Bundle
import android.content.Intent
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.example.g1_final_project.models.UserModel
import com.fxn.stash.Stash
import com.example.g1_final_project.databinding.ActivityLoginBinding
import com.example.g1_final_project.utils.Constants
import com.google.android.gms.tasks.Task

class LoginActivity : AppCompatActivity() {
    private var b: ActivityLoginBinding? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b!!.root)
        supportActionBar?.title = "BiSafe"
        progressDialog = ProgressDialog(this)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Loading...")
        b!!.btnCreate.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    SignUpActivity::class.java
                )
            )
        }
        b!!.btnLogin.setOnClickListener(View.OnClickListener {
            val emailStr = b!!.emailContainer.editText!!.text.toString()
            val passwordStr = b!!.passwordContainer.editText!!.text.toString()
            if (emailStr.isEmpty()) {
                val etlEmail = b!!.emailContainer
                etlEmail.error = "Enter email"
            } else if (passwordStr.isEmpty()) {
                val etlPassword = b!!.passwordContainer
                etlPassword.error = "Enter password"
            } else {
                progressDialog!!.show()
                Constants.auth().signInWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                        override fun onComplete(task: Task<AuthResult?>) {
                            if (task.isSuccessful) {
                                b!!.emailContainer.error = null
                                b!!.passwordContainer.error = null
                                userModel
                            } else {
                                b!!.emailContainer.error = "Invalid email"
                                b!!.passwordContainer.error = "Invalid password"

                                Constants.auth().signOut()
                                progressDialog!!.dismiss()
                                Toast.makeText(
                                    this@LoginActivity,
                                    task.exception!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        private val userModel: Unit
                            private get() {
                                Constants.databaseReference().child(
                                    Constants.auth().uid!!
                                )
                                    .get().addOnSuccessListener { dataSnapshot ->
                                        val userModel: UserModel?
                                        if (dataSnapshot.exists()) {
                                            userModel = dataSnapshot.getValue(UserModel::class.java)
                                        } else {
                                            userModel = UserModel()
                                            userModel.email = emailStr
                                            userModel.name = "nameStr"
                                            userModel.username = "usernameStr"
                                            userModel.password = passwordStr
                                            Constants.databaseReference().child(
                                                Constants.auth().uid!!
                                            )
                                                .setValue(userModel)
                                        }
                                        progressDialog!!.dismiss()
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Welcome, " + Constants.userModel().username + "!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Stash.put(Constants.USER_MODEL, userModel)
                                        Stash.put(Constants.IS_LOGGED_IN, true)
                                        val intent = Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        )
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        finish()
                                        startActivity(intent)
                                    }
                            }
                    })
            }

        })
    }
}