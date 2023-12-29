package pt.ipt.walkingsensor

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import okhttp3.ResponseBody
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

        val buttonclick = findViewById<Button>(R.id.loginButton)

        buttonclick.setOnClickListener{
            val email = findViewById<TextView>(R.id.EmailText).text
            val password = findViewById<TextView>(R.id.PasswordText).text
            fazerLogin(email.toString(), password.toString()){
                //Toast.makeText(this,"Added " + it?.message + ", " + it?.token,Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun fazerLogin(email: String?, password: String?, onResult: (Utilizador?) -> Unit){
        val utilizador = Utilizador(email, password)
        val call = RetrofitInitializer().connector().obterToken(utilizador)
        call.enqueue(
            object:Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable){
                    t.printStackTrace()
                    onResult(null)
                }
                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>){
                    val resultado: APIResult? = response.body()
                    print(resultado)
                    if (resultado?.error == null) {
                        val intent=Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }

                }
            }
        )

                }

        }


