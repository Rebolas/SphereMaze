package pt.ipt.walkingsensor

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import pt.ipt.WalkingSensorGame.R
import pt.ipt.walkingsensor.model.CustomizedAppCompact
import pt.ipt.walkingsensor.model.Utilizador

class PersonalDataActivity : CustomizedAppCompact() {
    private var lastLevel: Int = 0
    private lateinit var name:String
    private lateinit var token:String
    private lateinit var email:String
    val defaultPW = "********"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)


        name = intent.getStringExtra("name")!!
        token = intent.getStringExtra("token")!!
        email = intent.getStringExtra("email")!!
        lastLevel =  intent.getIntExtra("lastLevel",0)

        val buttonvoltardados = findViewById<Button>(R.id.ButtonVoltarSobre)
        buttonvoltardados.setOnClickListener {
            this.finish()
        }
        val nameTextView = findViewById<TextView>(R.id.textViewNome)
        val textViewEmail = findViewById<TextView>(R.id.textViewEmail)
        val nameEdit = findViewById<EditText>(R.id.editUsernamePerfil)
        val emailEdit = findViewById<EditText>(R.id.editEmailPerfil)
        val passEdit = findViewById<TextInputEditText>(R.id.PasswordRegisterInput)
        val buttonEditarDados = findViewById<Button>(R.id.buttonEditarDados)
        val textViewNivelDados = findViewById<TextView>(R.id.textViewNivelDados)

        textViewNivelDados.text = "Level : $lastLevel"

        //setupData

        nameTextView.text = name
        nameEdit.setText(name)

        textViewEmail.text = email
        emailEdit.setText(email)

        passEdit.setText(defaultPW)


        //setup buttons
        buttonEditarDados.setOnClickListener{
        if (!nameEdit.isVisible) {
            //Editable
            nameEdit.visibility = View.VISIBLE
            nameTextView.visibility = View.INVISIBLE

            emailEdit.visibility = View.VISIBLE
            textViewEmail.visibility = View.INVISIBLE

            passEdit.isEnabled = true

        }else{

            //SAVE DATA
            val name = nameEdit.text.toString()
            val email = emailEdit.text.toString()
            var pass: String? = passEdit.text.toString()

            nameEdit.visibility = View.INVISIBLE
            nameTextView.visibility = View.VISIBLE
            nameTextView.text = name

            emailEdit.visibility = View.INVISIBLE
            textViewEmail.visibility = View.VISIBLE
            textViewEmail.text = email

            passEdit.isEnabled = false

            if (pass == defaultPW){
                pass = null
            }

            val u = Utilizador(email,name,pass,token)
            editPersonalData(u)
            }

        }
    }

}