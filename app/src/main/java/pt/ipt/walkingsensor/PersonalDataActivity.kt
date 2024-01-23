package pt.ipt.walkingsensor

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import pt.ipt.WalkingSensorGame.R

class PersonalDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)

        val buttonvoltardados = findViewById<Button>(R.id.ButtonVoltarSobre)
        buttonvoltardados.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val nameTextView = findViewById<TextView>(R.id.textViewNome)
        val textViewEmail = findViewById<TextView>(R.id.textViewEmail)
        val nameEdit = findViewById<EditText>(R.id.editUsernamePerfil)
        val emailEdit = findViewById<EditText>(R.id.editEmailPerfil)

        val buttonEditarDados=findViewById<Button>(R.id.buttonEditarDados)
        buttonEditarDados.setOnClickListener{
        if (!nameEdit.isVisible) {
            nameEdit.visibility = View.VISIBLE
            textViewEmail.visibility = View.INVISIBLE

            emailEdit.visibility = View.VISIBLE
            textViewEmail.visibility = View.INVISIBLE
        }else{
            nameEdit.visibility = View.INVISIBLE
            nameTextView.visibility = View.VISIBLE
            nameTextView.text = nameEdit.text

            emailEdit.visibility = View.INVISIBLE
            textViewEmail.visibility = View.VISIBLE
            textViewEmail.text = emailEdit.text
        }
        }
    }
}