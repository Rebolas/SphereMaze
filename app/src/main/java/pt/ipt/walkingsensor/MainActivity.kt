package pt.ipt.walkingsensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import pt.ipt.WalkingSensorGame.R
import pt.ipt.walkingsensor.levels.Level1

//fade in animation
// https://stackoverflow.com/questions/28961478/android-studio-fading-splash-into-main


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent (this@MainActivity, SettingsActivity::class.java )
            startActivity(intent)
        }

        val logoutButton = findViewById<Button>(R.id.LogoutButton)
        logoutButton.setOnClickListener {
            closeActivity()
        }

        val buttonPerfil = findViewById<ImageButton>(R.id.imageButtonPerfil)
        buttonPerfil.setOnClickListener {
            val intent = Intent(this, PersonalDataActivity::class.java)
            startActivity(intent)
        }

        val buttonscoremain = findViewById<ImageButton>(R.id.scoreButton)
        buttonscoremain.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            startActivity(intent)
        }

            val buttonclick = findViewById<ImageButton>(R.id.playButton)
            buttonclick.setOnClickListener {
                val intent = Intent(this, Level1::class.java)
                startActivity(intent)

                //overridePendingTransition(R.drawable.fade_in, R.drawable.fade_out);
            }
        }

    private fun closeActivity() {
        this.finish()
    }
}
