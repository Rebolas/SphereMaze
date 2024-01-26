package pt.ipt.walkingsensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pt.ipt.WalkingSensorGame.R

class SobreNos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre_nos)

        val voltarLandButton = findViewById<Button>(R.id.ButtonVoltarSobre)
        voltarLandButton.setOnClickListener {
            this.finish()
        }
    }
}