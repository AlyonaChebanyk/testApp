package com.example.testapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.testapp.databinding.FragmentSplashBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashFragment : Fragment(){

    private lateinit var binding: FragmentSplashBinding

    private lateinit var sharedPref: SharedPreferences
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var status: Boolean = true

    private val uiScope = CoroutineScope(Dispatchers.Main + Job())
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        sharedPref = requireActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        ioScope.launch {
            delay(1500)
            uiScope.launch {
                val isStatusSaved = sharedPref.contains("status")
                if (!isStatusSaved) {
                    remoteConfig.fetchAndActivate()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val updated = task.result
                                Timber.d("Config params updated: $updated")
                            }
                            status = remoteConfig.getBoolean("status")
                            sharedPref.edit().putBoolean("status", status).apply()
                            if (status) {
                                findNavController().navigate(
                                    R.id.action_splashFragment_to_webViewFragment,
                                )
                            } else {
                                findNavController().navigate(R.id.action_splashFragment_to_chooseGameFragment)
                            }
                        }
                } else {
                    status = remoteConfig.getBoolean("status")
                    if (status) {
                        findNavController().navigate(
                            R.id.action_splashFragment_to_webViewFragment,
                        )
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_chooseGameFragment)
                    }
                }
            }
        }
    }
}