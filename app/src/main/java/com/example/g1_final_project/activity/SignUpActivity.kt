package com.example.g1_final_project.activity

import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.os.Bundle
import com.example.g1_final_project.models.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import android.widget.Toast
import com.fxn.stash.Stash
import android.content.Intent
import android.view.View
import com.example.g1_final_project.databinding.ActivitySignUpBinding
import com.example.g1_final_project.utils.Constants
import com.google.android.gms.tasks.Task

class SignUpActivity : AppCompatActivity() {
    private var b: ActivitySignUpBinding? = null
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(b!!.root)
        supportActionBar?.title = "BiSafe"
        progressDialog = ProgressDialog(this)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Loading...")
        b!!.btnBackLogin.setOnClickListener { finish() }
        b!!.btnSignup.setOnClickListener(View.OnClickListener {
            val emailStr = b!!.email.editText!!.text.toString()
            val nameStr = b!!.name.editText!!.text.toString()
            val usernameStr = b!!.username.editText!!.text.toString()
            val passwordStr = b!!.password.editText!!.text.toString()
            if (emailStr.isEmpty()) return@OnClickListener
            if (nameStr.isEmpty()) return@OnClickListener
            if (usernameStr.isEmpty()) return@OnClickListener
            if (passwordStr.isEmpty()) return@OnClickListener
            val userModel = UserModel()
            userModel.email = emailStr
            userModel.name = nameStr
            userModel.username = usernameStr
            userModel.password = passwordStr
            progressDialog!!.show()
            Constants.auth().createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                    override fun onComplete(task: Task<AuthResult?>) {
                        if (task.isSuccessful) {
                            uploadUserModel()
                        } else {
                            Constants.auth().signOut()
                            progressDialog!!.dismiss()
                            Toast.makeText(
                                this@SignUpActivity,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    private fun uploadUserModel() {
                        Constants.databaseReference().child(Constants.auth().uid!!)
                            .setValue(userModel)
                            .addOnCompleteListener {
                                progressDialog!!.dismiss()
                                Toast.makeText(this@SignUpActivity, "Success", Toast.LENGTH_SHORT)
                                    .show()
                                Stash.put(Constants.USER_MODEL, userModel)
                                Stash.put(Constants.IS_LOGGED_IN, true)
                                val intent = Intent(
                                    this@SignUpActivity,
                                    LoginActivity::class.java
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                finish()
                                startActivity(intent)
                            }
                    }
                })
        })
    }
}