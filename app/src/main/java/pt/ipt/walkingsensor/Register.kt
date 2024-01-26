package pt.ipt.walkingsensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import pt.ipt.WalkingSensorGame.R
import pt.ipt.walkingsensor.model.APIResult
import pt.ipt.walkingsensor.model.Utilizador
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val voltarButton = findViewById<ImageButton>(R.id.backButtonRegister)

        val registerButton = findViewById<ImageButton>(R.id.registerButton)
        voltarButton.setOnClickListener {
            this.finish()
        }

        registerButton.setOnClickListener {
            val name = findViewById<TextView>(R.id.name_emailTextLogin).text
            val email = findViewById<TextView>(R.id.EmailTextView).text
            val password = findViewById<TextView>(R.id.PasswordLoginInput).text
            fazerRegisto(name.toString(), email.toString(), password.toString()) {
                //Toast.makeText(this,"Added " + it?.message + ", " + it?.token,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fazerRegisto(name: String?, email: String?, password: String?, onResult: (Utilizador?) -> Unit){
        val utilizador = Utilizador(name, email, password)
        val call = pt.ipt.walkingsensor.RetrofitInitializer().connector().obterRegisto(utilizador)
        call.enqueue(
            object: Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable){
                    t.printStackTrace()
                    onResult(null)
                }
                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>){
                    val resultado: APIResult? = response.body()
                    print(resultado)
                    if (resultado?.error == null) {
                        val intent=Intent(this@Register, MainActivity::class.java)
                        startActivity(intent)
                    }

                }
            }
        )

    }
}