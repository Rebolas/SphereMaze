package pt.ipt.walkingsensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import pt.ipt.WalkingSensorGame.R
import pt.ipt.walkingsensor.model.APIResult
import pt.ipt.walkingsensor.model.Utilizador
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
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
            val password = findViewById<TextView>(R.id.PasswordLoginInput).text
            fazerLogin(name_email.toString(), password.toString()){
                Toast.makeText(this,"login efectuado com sucesso" ,Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun fazerLogin(name_email: String?, password: String?, onResult: (Utilizador?) -> Unit){
        //var utilizador: Utilizador?
        val utilizador = if (name_email.toString().contains("@")){
            Utilizador(null,name_email , password)
        }else{
            Utilizador(name_email,null , password)
        }
        val call = RetrofitInitializer().connector().obterLogin(utilizador)
        call.enqueue(
            object:Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable){
                    t.printStackTrace()
                    onResult(null)
                }
                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>){
                    val resultado: APIResult? = response.body()

                    if (resultado?.error == null) {
                        val intent=Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }

                }
            }
        )

                }

        }


