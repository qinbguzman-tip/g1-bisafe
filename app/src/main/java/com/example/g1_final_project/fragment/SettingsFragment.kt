package com.example.g1_final_project.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.example.g1_final_project.models.UserModel
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.g1_final_project.R
import com.example.g1_final_project.databinding.FragmentSettingsBinding
import com.example.g1_final_project.utils.Constants
import kotlinx.android.synthetic.*

class SettingsFragment : Fragment() {
    private var b: FragmentSettingsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        b = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = b!!.root
        b!!.emailContainer.editText!!.setText(Constants.userModel().email)
        b!!.nameContainer.editText!!.setText(Constants.userModel().name)
        b!!.usernameContainer.editText!!.setText(Constants.userModel().username)
        b!!.passwordContainer.editText!!.setText(Constants.userModel().password)
        b!!.btnSubmit.setOnClickListener(View.OnClickListener {
            var builder = AlertDialog.Builder(activity)
            builder.setTitle("Apply Changes")
            builder.setMessage("Are you sure you want to apply changes?")
            builder.setPositiveButton("Yes") { dialog, id ->
                val emailStr = b!!.emailContainer.editText!!.text.toString()
                val nameStr = b!!.nameContainer.editText!!.text.toString()
                val usernameStr = b!!.usernameContainer.editText!!.text.toString()
                val passwordStr = b!!.passwordContainer.editText!!.text.toString()
                if (emailStr.isEmpty()) {
                    b!!.emailContainer.error = "Enter an email"
                } else {
                    b!!.emailContainer.error = null
                }
                if (nameStr.isEmpty()) {
                    b!!.nameContainer.error = "Enter a name"
                } else {
                    b!!.nameContainer.error = null
                }
                if (usernameStr.isEmpty()) {
                    b!!.usernameContainer.error = "Enter a username"
                } else {
                    b!!.usernameContainer.error = null
                }
                if (passwordStr.isEmpty()) {
                    b!!.passwordContainer.error = "Enter a password"
                } else {
                    b!!.passwordContainer.error = null
                }

                val userModel = UserModel()
                userModel.email = emailStr
                userModel.name = nameStr
                userModel.username = usernameStr
                userModel.password = passwordStr

                Constants.databaseReference().child(Constants.auth().uid!!)
                    .setValue(userModel)
                Toast.makeText(
                    requireContext(),
                    "Your profile has been updated.",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.cancel()
            }
            builder.setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
            var alert = builder.create()
            alert.show()

        })
        return root
    }
}