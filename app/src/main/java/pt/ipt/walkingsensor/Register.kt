package pt.ipt.walkingsensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import pt.ipt.WalkingSensorGame.R
import pt.ipt.walkingsensor.model.APIResult
import pt.ipt.walkingsensor.model.CustomizedAppCompact
import pt.ipt.walkingsensor.model.Utilizador
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : CustomizedAppCompact() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val voltarButton = findViewById<ImageButton>(R.id.backButtonRegister)
        val registerButton = findViewById<ImageButton>(R.id.registerButton)
        voltarButton.setOnClickListener {
            this.finish()
        }

        registerButton.setOnClickListener {
            var validRegistry = true
            val name = findViewById<TextView>(R.id.name_emailTextLogin).text
            val email = findViewById<TextView>(R.id.EmailTextView).text
            val password = findViewById<TextInputEditText>(R.id.PasswordRegisterInput).text.toString()

            //Log.d("Debug","password $password")
            // Conditions for the username
            if (validRegistry && !name.matches(Regex("[A-Za-z0-9_-]+"))){
                // Username does not meet the requirements
                validRegistry = false
                Toast.makeText(this, "Username can only contain uppercase, lowercase, numbers, hyphens, and underscores.", Toast.LENGTH_LONG).show()
            }


            // Conditions for the email (assuming you want some basic checks)
            if (validRegistry && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Invalid email
                validRegistry = false
                Toast.makeText(this, "Invalid email address.", Toast.LENGTH_LONG).show()
            }

            // Check if password length is between 8 and 16 characters
            if (validRegistry && password.length !in 8..16) {
                // Password length does not meet the requirements
                validRegistry = false
                Toast.makeText(this, "Password must be between 8 and 16 characters.", Toast.LENGTH_LONG).show()
            }

            // Check if password contains at least one uppercase letter
            if (validRegistry && !password.any { it.isUpperCase() }) {
                // Password does not contain an uppercase letter
                validRegistry = false
                Toast.makeText(this, "Password must include at least one uppercase letter.", Toast.LENGTH_LONG).show()
            }

            // Check if password contains at least one lowercase letter
            if (validRegistry && !password.any { it.isLowerCase() }) {
                // Password does not contain a lowercase letter
                validRegistry = false
                Toast.makeText(this, "Password must include at least one lowercase letter.", Toast.LENGTH_LONG).show()
            }

            // Check if password contains at least one digit
            if (validRegistry && !password.any { it.isDigit() }) {
                // Password does not contain a digit
                validRegistry = false
                Toast.makeText(this, "Password must include at least one digit.", Toast.LENGTH_LONG).show()
            }


            if (validRegistry){
                fazerRegisto(name.toString(), email.toString(), hashPassword(password), object : GetDataCallback{
                    override fun onGetData(data: APIResult) {
                        Log.d("Debug","API message : ${data.message}")
                        if (data.error == null){
                            Toast.makeText(baseContext, "Success on register!", Toast.LENGTH_LONG).show()
                            endActivity()
                        }
                    }

                    override fun onError() {
                        Log.d("Debug", "Some error occurred on Register")
                    }


                })
            }

        }
    }
    fun endActivity(){
        this.finish()
    }


}