package pt.ipt.walkingsensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pt.ipt.WalkingSensorGame.R

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val voltarButton = findViewById<Button>(R.id.RegisterButton)
        voltarButton.setOnClickListener {
            val intent = Intent(this@Register, LandingActivity::class.java)
            startActivity(intent)
        }
    }
}