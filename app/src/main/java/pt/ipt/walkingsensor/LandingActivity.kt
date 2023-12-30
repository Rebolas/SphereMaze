package pt.ipt.walkingsensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pt.ipt.WalkingSensorGame.R

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

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
    }
}