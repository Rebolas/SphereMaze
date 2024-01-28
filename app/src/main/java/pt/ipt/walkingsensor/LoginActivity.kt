package pt.ipt.walkingsensor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import pt.ipt.WalkingSensorGame.R
import pt.ipt.walkingsensor.model.APIResult
import pt.ipt.walkingsensor.model.CustomizedAppCompact

class LoginActivity : CustomizedAppCompact() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<ImageButton>(R.id.loginButton)
        val voltarButton = findViewById<ImageButton>(R.id.backButtonLogin)
        voltarButton.setOnClickListener {
            //val intent = Intent(this@LoginActivity, LandingActivity::class.java)
            //startActivity(intent)
            this.finish()
        }

        loginButton.setOnClickListener{
            val name_email = findViewById<TextView>(R.id.name_emailTextLogin).text
            val password = hashPassword(findViewById<TextView>(R.id.PasswordLoginInput).text.toString().trim())
            val result = fazerLogin(name_email.toString(), password, object : GetDataCallback{

                override fun onGetData(data: APIResult) {
                    Log.d("Debug","API message : ${data.message}")
                    if (data.error == null){
                        val intent= Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("name", data?.name)
                        intent.putExtra("token", data?.token)
                        intent.putExtra("email", data?.email)
                        intent.putExtra("lastLevel", data?.lastLevel)
                        startActivity(intent)
                    }else{
                        Log.d("Debug","API error : ${data.error}")
                    }
                }

                override fun onError() {}
            })
        }
    }

}


