package pt.ipt.walkingsensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text
import pt.ipt.WalkingSensorGame.R
import pt.ipt.walkingsensor.levels.Level1
import pt.ipt.walkingsensor.model.CustomizedAppCompact
import kotlin.properties.Delegates

//fade in animation
// https://stackoverflow.com/questions/28961478/android-studio-fading-splash-into-main


class MainActivity : CustomizedAppCompact() {
    private lateinit var name:String
    private lateinit var token:String
    private lateinit var email:String
    private var lastLevel by Delegates.notNull<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        name = intent.getStringExtra("name")!!
        token = intent.getStringExtra("token")!!
        email = intent.getStringExtra("email")!!
        lastLevel =  intent.getIntExtra("lastLevel",0)


        val lastLevelView = findViewById<TextView>(R.id.textViewLastlevel)
        lastLevelView.text = "Level : $lastLevel"

        val usernameTextView = findViewById<TextView>(R.id.usernameTextViewMain)
        usernameTextView.text = name

        val logoutButton = findViewById<Button>(R.id.LogoutButton)
        logoutButton.setOnClickListener {
            closeActivity()
        }

        val buttonPerfil = findViewById<ImageButton>(R.id.imageButtonPerfil)
        buttonPerfil.setOnClickListener {
            val intent = Intent(this, PersonalDataActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("token", token)
            intent.putExtra("email", email)
            intent.putExtra("lastLevel",lastLevel)
            startActivity(intent)
        }

        val buttonscoremain = findViewById<ImageButton>(R.id.scoreButton)
        buttonscoremain.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            startActivity(intent)
        }

            val buttonclick = findViewById<ImageButton>(R.id.playButton)
            buttonclick.setOnClickListener {
                when(lastLevel){
                    0 ->{
                        val intent = Intent(this, Level1::class.java)
                        startActivity(intent)
                    }
                }


                //overridePendingTransition(R.drawable.fade_in, R.drawable.fade_out);
            }
        }

    private fun closeActivity() {
        logOut(token)
        this.finish()
    }
}
