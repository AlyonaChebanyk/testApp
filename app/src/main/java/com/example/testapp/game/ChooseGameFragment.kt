package com.example.testapp.game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.testapp.MainActivity
import com.example.testapp.R
import com.example.testapp.databinding.FragmentChooseGameBinding

class ChooseGameFragment : Fragment() {

    private lateinit var binding: FragmentChooseGameBinding

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        }

        // Add the OnBackPressedCallback to the activity's OnBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        sharedPref = requireActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)

        binding = FragmentChooseGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            game1CardView.setOnClickListener {
                (activity as MainActivity).playOnClickSound()
                findNavController().navigate(R.id.action_chooseGameFragment_to_game1Fragment)
            }
            game2CardView.setOnClickListener {
                (activity as MainActivity).playOnClickSound()
                findNavController().navigate(R.id.action_chooseGameFragment_to_game2Fragment)
            }
            bonusGameCardView.setOnClickListener {
                (activity as MainActivity).playOnClickSound()
                findNavController().navigate(R.id.action_chooseGameFragment_to_bonusGameFragment)
            }
        }
    }
}