package com.example.g1_final_project.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.g1_final_project.R
import com.example.g1_final_project.databinding.FragmentHomeBinding
import com.example.g1_final_project.databinding.FragmentLoginBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val binding = FragmentHomeBinding.bind(view)

        binding.btnStartJourney.setOnClickListener() {
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_journeyFragment)
        }

        return binding.root
    }

}