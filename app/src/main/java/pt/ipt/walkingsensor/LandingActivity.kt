package pt.ipt.walkingsensor

//import android.R

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.WalkingSensorGame.R
import pt.ipt.walkingsensor.service.BackgroundMusic


class LandingActivity : AppCompatActivity() {
    private var isMusicPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val soundButton = findViewById<ImageButton>(R.id.soundButton)
        val soundText = findViewById<TextView>(R.id.SoundLandingTextView)
        soundButton.setOnClickListener {
            if (isMusicPlaying) {
                soundText.text = "Sound off"
                stopService(
                    Intent(
                        this@LandingActivity,
                        BackgroundMusic::class.java
                    )
                )
                isMusicPlaying = false
            } else {
                soundText.text = "Sound On"
                Log.d("mylog", "Button Click")
                startService(
                    Intent(
                        this@LandingActivity,
                        BackgroundMusic::class.java
                    )
                )
                isMusicPlaying = true
            }
        }

        startService(
            Intent(
                this@LandingActivity,
                BackgroundMusic::class.java
            )
        )
        isMusicPlaying = true

        /// END OF TEST CODE


        val loginButton = findViewById<ImageButton>(R.id.loginLandingButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@LandingActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val registerButton = findViewById<ImageButton>(R.id.registerLandingButton)
        registerButton.setOnClickListener {
            val intent = Intent(this@LandingActivity, Register::class.java)
            startActivity(intent)
        }

        val sobreButton = findViewById<ImageButton>(R.id.AboutUsLandingButton)
        sobreButton.setOnClickListener {
            val intent = Intent(this@LandingActivity, SobreNos::class.java)
            startActivity(intent)
        }
    }

}
