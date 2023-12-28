package pt.ipt.walkingsensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.TextView
import pt.ipt.WalkingSensorGame.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonclick = findViewById<Button>(R.id.loginButton)
        val textoTestes = findViewById<TextView>(R.id.textViewTestes)
        textoTestes.text = "blahblah"
        buttonclick.setOnClickListener{
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}