package pt.ipt.walkingsensor

//import android.R

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.WalkingSensorGame.R
import pt.ipt.walkingsensor.service.BackgroundMusic


class LandingActivity : AppCompatActivity() {
    private var isMusicPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        //          TEST CODE
        val btnPlay = findViewById<Button>(R.id.btnPlayInBg)

        btnPlay.setOnClickListener {
            if (isMusicPlaying) {
                stopService(
                    Intent(
                        this@LandingActivity,
                        BackgroundMusic::class.java
                    )
                )
                isMusicPlaying = false
            } else {
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
        val loginButton = findViewById<Button>(R.id.LoginButLanding)
        loginButton.setOnClickListener {
            val intent = Intent(this@LandingActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val registerButton = findViewById<Button>(R.id.RegisterButLanding)
        registerButton.setOnClickListener {
            val intent = Intent(this@LandingActivity, Register::class.java)
            startActivity(intent)
        }

        val sobreButton = findViewById<Button>(R.id.ButtonSobre)
        sobreButton.setOnClickListener {
            val intent = Intent(this@LandingActivity, SobreNos::class.java)
            startActivity(intent)
        }
    }

}
