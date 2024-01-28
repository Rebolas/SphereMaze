package pt.ipt.walkingsensor.service
import pt.ipt.walkingsensor.model.APIResult
import pt.ipt.walkingsensor.model.Utilizador
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIConnector {

    @GET("get_all_users")
    fun obterUtilizadores(): Call<List<Utilizador>>

    @GET("get_all_scores")
    fun obterScores(): Call<List<Int>>

    @POST("login")
    fun obterLogin(@Body utilizador: Utilizador?): Call<APIResult>

    @POST("create_account")
    fun obterRegisto(@Body utilizador: Utilizador?): Call<APIResult>

    @POST("logout")
    fun logOut(@Body token: String?): Call<APIResult>

    @POST("editPersonalData")
    fun editPersonalData(@Body utilizador: Utilizador?): Call<APIResult>


    //fun obterToken(@Field("email")email:String?, @Field("password")password:String?): Call<APIResult>
   /* @GET("API/getNotes.php")
    fun list(): Call<List<Utilizador>>*/
}