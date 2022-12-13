package com.example.g1_final_project.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.example.g1_final_project.models.UserModel
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.g1_final_project.databinding.FragmentSettingsBinding
import com.example.g1_final_project.utils.Constants

class SettingsFragment : Fragment() {
    private var b: FragmentSettingsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        b = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = b!!.root
        b!!.email.editText!!.setText(Constants.userModel().email)
        b!!.name.editText!!.setText(Constants.userModel().name)
        b!!.username.editText!!.setText(Constants.userModel().username)
        b!!.password.editText!!.setText(Constants.userModel().password)
        b!!.btnSubmit.setOnClickListener(View.OnClickListener {
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
            Constants.databaseReference().child(Constants.auth().uid!!)
                .setValue(userModel)
            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
        })
        return root
    }
}