package pt.ipt.walkingsensor.model

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.walkingsensor.MainActivity
import pt.ipt.walkingsensor.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

open class CustomizedAppCompact : AppCompatActivity() {

    private val SECRET: String = "SupEr1R0ND9MSecret"

    interface GetDataCallback {
        fun onGetData(data : APIResult)
        fun onError()
    }
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    fun fazerLogin(name_email: String?, password: String?, getDataCallback: GetDataCallback) {

        //var utilizador: Utilizador?
        val utilizador = if (name_email.toString().contains("@")){
            Utilizador(null,name_email , password,null)
        }else{
            Utilizador(name_email,null , password,null)
        }

        val call = RetrofitInitializer().connector().obterLogin(utilizador)

        call.enqueue(
            object: Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable){
                    t.printStackTrace()
                    getDataCallback.onError()
                }

                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>){
                    val resultado: APIResult? = response.body()
                    if (resultado != null) {
                        getDataCallback.onGetData(resultado)
                    }


                }
            }
        )

    }

    fun fazerRegisto(name: String?, email: String?, password: String?, getDataCallback: GetDataCallback){
        val utilizador = Utilizador(name, email, password,null)
        val call = RetrofitInitializer().connector().obterRegisto(utilizador)
        call.enqueue(
            object: Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable){
                    t.printStackTrace()
                }
                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>){
                    val resultado: APIResult? = response.body()
                    if (resultado != null) {
                        getDataCallback.onGetData(resultado)
                    }
                    /*print(resultado)
                    if (resultado?.error == null) {
                        val intent= Intent(this@Register, MainActivity::class.java)
                        startActivity(intent)
                    }*/
                }
            }
        )
    }

    fun postScore(token: String,level:String,score:Int, getDataCallback: GetDataCallback){
        val s = Score(token,level,score)
        Log.d("Debug", "score sending token :$token, level: $level, Score: $score")
        val call = RetrofitInitializer().connector().postScore(s)
        call.enqueue(
            object: Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable){
                    t.printStackTrace()
                }
                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>){
                    val resultado: APIResult? = response.body()
                    if (resultado != null) {
                        getDataCallback.onGetData(resultado)
                    }
                }
            }
        )
    }

    fun logOut(token:String, getDataCallback: GetDataCallback){
        val call = RetrofitInitializer().connector().logOut(token)
        call.enqueue(
            object: Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable){
                    t.printStackTrace()
                }
                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>){
                    val resultado: APIResult? = response.body()
                    if (resultado != null) {
                        getDataCallback.onGetData(resultado)
                    }
                }
            }
        )
    }

    fun editPersonalData(u: Utilizador) {
        val call = RetrofitInitializer().connector().editPersonalData(u)
        call.enqueue(
            object: Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable){
                    t.printStackTrace()
                }
                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>){
                    val resultado: APIResult? = response.body()
                    Log.d("Debug", "result comming : ${resultado?.message}")
                }
            }
        )
    }

}