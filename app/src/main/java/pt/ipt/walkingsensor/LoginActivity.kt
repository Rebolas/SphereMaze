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
            val username = findViewById<TextView>(R.id.EmailText).text
            val password = findViewById<TextView>(R.id.PasswordText).text
            fazerLogin(username.toString(), password.toString()){
                Toast.makeText(this,"login efectuado com sucesso" ,Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun fazerLogin(username: String?, password: String?, onResult: (Utilizador?) -> Unit){
        val utilizador = Utilizador(null, password, username)
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


