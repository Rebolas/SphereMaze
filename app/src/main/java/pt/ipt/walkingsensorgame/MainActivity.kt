package pt.ipt.walkingsensorgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pt.ipt.WalkingSensorGame.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonclick = findViewById<Button>(R.id.Playbutton)
        buttonclick.setOnClickListener{
            val intent= Intent(this,Level1::class.java)
            startActivity(intent)
        }
    }
}