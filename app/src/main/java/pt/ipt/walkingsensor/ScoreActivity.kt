package pt.ipt.walkingsensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pt.ipt.WalkingSensorGame.R

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)


        val voltarButtonScore = findViewById<Button>(R.id.ButtonVoltarSobre)
        voltarButtonScore.setOnClickListener {
            this.finish()
        }
    }
}