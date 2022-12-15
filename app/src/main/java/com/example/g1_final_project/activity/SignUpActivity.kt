package com.example.g1_final_project.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.os.Bundle
import com.example.g1_final_project.models.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import android.widget.Toast
import com.fxn.stash.Stash
import android.content.Intent
import android.util.Patterns
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

        emailFocusListener()
        nameFocusListener()
        usernameFocusListener()
        passwordFocusListener()

        supportActionBar?.title = "BiSafe"
        progressDialog = ProgressDialog(this)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Loading...")
        b!!.btnBackLogin.setOnClickListener { finish() }
        b!!.btnSignup.setOnClickListener(View.OnClickListener {
            val validEmail = b!!.emailContainer.helperText == null
            val validName = b!!.emailContainer.helperText == null
            val validUsername = b!!.usernameContainer.helperText == null
            val validPassword = b!!.passwordContainer.helperText == null

            b!!.emailContainer.helperText == validEmail()
            b!!.nameContainer.helperText == validName()
            b!!.usernameContainer.helperText == validUsername()
            b!!.passwordContainer.helperText == validPassword()

            if (validEmail && validName && validUsername && validPassword) {
                val emailStr = b!!.emailContainer.editText!!.text.toString()
                val nameStr = b!!.nameContainer.editText!!.text.toString()
                val usernameStr = b!!.usernameContainer.editText!!.text.toString()
                val passwordStr = b!!.passwordContainer.editText!!.text.toString()
                val userModel = UserModel()

//                b!!.emailContainer.helperText = "Required"
//                b!!.nameContainer.helperText = "Required"
//                b!!.usernameContainer.helperText = "Required"
//                b!!.emailContainer.helperText = "Required"

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
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Registered successfully",
                                        Toast.LENGTH_SHORT
                                    )
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
            }
        })
    }

    private fun emailFocusListener() {
        b?.emailEditText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                b!!.emailContainer.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = b?.emailEditText?.text.toString()
        val etEmail = b?.emailContainer
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            etEmail?.error = "Invalid Email Address"
        } else {
            etEmail?.error = null
        }
        return null
    }

    private fun nameFocusListener() {
        b?.nameEditText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                b!!.nameContainer.helperText = validName()
            }
        }
    }

    private fun validName(): String? {
        val nameText = b?.nameEditText?.text.toString()
        val etName = b?.nameContainer
        if (nameText.isEmpty()) {
            etName?.error = "Required"
        } else {
            etName?.error = null
        }
        return null
    }

    private fun usernameFocusListener() {
        b?.usernameEditText?.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                b!!.usernameContainer.helperText = validUsername()
            }
        }
    }

    private fun validUsername(): String? {
        val usernameText = b?.usernameEditText?.text.toString()
        val etUsername = b?.usernameContainer
        if (usernameText.isEmpty()) {
            etUsername?.error = "Required"
        } else if (usernameText.contains(" ")) {
            etUsername?.error = "Must not contain any spaces"
        } else {
            etUsername?.error = null
        }
        return null
    }

    private fun passwordFocusListener() {
        b?.passwordEditText?.setOnFocusChangeListener { _, focused ->
            if (focused) {
                b!!.passwordContainer.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = b?.passwordEditText?.text.toString()
        val etPassword = b?.passwordContainer
        if (passwordText.length < 8) {
            etPassword?.error = "Minimum of 8 Characters"
        } else {
            etPassword?.error = null
        }
        return null
    }
}