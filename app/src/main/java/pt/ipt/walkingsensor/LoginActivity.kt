package pt.ipt.walkingsensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
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

        val loginButton = findViewById<Button>(R.id.loginButton)
        val voltarButton = findViewById<Button>(R.id.voltarButtLog)
        voltarButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, LandingActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener{
            val name_email = findViewById<TextView>(R.id.name_emailText).text
            val password = findViewById<TextView>(R.id.PasswordLoginInput).text
            fazerLogin(name_email.toString(), password.toString()){
                Toast.makeText(this,"login efectuado com sucesso" ,Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun fazerLogin(name_email: String?, password: String?, onResult: (Utilizador?) -> Unit){
        var utilizador: Utilizador? = null
        if (name_email.toString().contains("@")){
            utilizador = Utilizador(null,name_email , password)
        }else{
            utilizador = Utilizador(name_email,null , password)
        }
        val call = pt.ipt.walkingsensor.RetrofitInitializer().connector().obterLogin(utilizador)
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


