package com.example.testapp

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity(){

    private lateinit var sharedPref: SharedPreferences

    private lateinit var mediaPlayerOnWin: MediaPlayer
    private lateinit var mediaPlayerOnClickButton: MediaPlayer
    private lateinit var mediaPlayerOnLose: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        if (savedInstanceState == null) {
            // Restore the counter value
            val isSignedUp = sharedPref.getBoolean("isSignedUp", false)
            if (isSignedUp) {
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
                val navController = navHostFragment.navController
//            navController.navigateUp() // to clear previous navigation history
                navController.navigate(R.id.chooseGameFragment)
            }
        }
        mediaPlayerOnWin = MediaPlayer.create(this, R.raw.instant_win)
        mediaPlayerOnClickButton = MediaPlayer.create(this, R.raw.button_click)
        mediaPlayerOnLose = MediaPlayer.create(this, R.raw.lose)
    }

    fun getBalance(): Int = sharedPref.getInt("balance", 1000)

    fun saveBalance(balance: Int) = sharedPref.edit().putInt("balance", balance).apply()

    fun playOnClickSound() {
        val musicVolume = sharedPref.getInt("musicVolume", 5)
        mediaPlayerOnClickButton.setVolume(musicVolume.toFloat() / 7, musicVolume.toFloat() / 7)
        mediaPlayerOnClickButton.start()
    }

    fun playOnClickSoundOrVibrate() {
        val musicVolume = sharedPref.getInt("musicVolume", 5)
        if (musicVolume == 0){
            vibrate(this)
        } else {
            mediaPlayerOnClickButton.setVolume(musicVolume.toFloat() / 7, musicVolume.toFloat() / 7)
            mediaPlayerOnClickButton.start()
        }
    }

    fun playOnWin() {
        val musicVolume = sharedPref.getInt("musicVolume", 5)
        mediaPlayerOnWin.setVolume(musicVolume.toFloat() / 7, musicVolume.toFloat() / 7)
        mediaPlayerOnWin.start()
    }

    fun playOnWinOrVibrate() {
        val musicVolume = sharedPref.getInt("musicVolume", 5)
        if (musicVolume == 0){
            vibrate(this)
        } else {
            mediaPlayerOnWin.setVolume(musicVolume.toFloat() / 7, musicVolume.toFloat() / 7)
            mediaPlayerOnWin.start()
        }
    }

    fun vibrateIfMusicOff(){
        val musicVolume = sharedPref.getInt("musicVolume", 5)
        if (musicVolume == 0){
            vibrate(this)
        }
    }

    fun playOnLose() {
        val musicVolume = sharedPref.getInt("musicVolume", 5)
        mediaPlayerOnLose.setVolume(musicVolume.toFloat() / 7, musicVolume.toFloat() / 7)
        mediaPlayerOnLose.start()
    }

    fun vibrate(context: Context?) {
        val vibrationVolume = sharedPref.getInt("vibrationVolume", 5)
        // Get the Vibrator system service
        if (vibrationVolume != 0) {
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

            // Check if device has vibrator and vibrator service is available
            if (vibrator != null && vibrator.hasVibrator()) {
                // Vibrate for the specified duration
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            150 * vibrationVolume.toLong() / 7,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator.vibrate(
                        150 * vibrationVolume.toLong() / 7
                    )
                }
            }
        }
    }
}