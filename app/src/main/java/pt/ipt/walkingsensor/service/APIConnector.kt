package pt.ipt.walkingsensor.service
import pt.ipt.walkingsensor.model.Utilizador
import retrofit2.Call
import retrofit2.http.GET
interface APIConnector {
    @GET("get_all_users")
    fun obterUtilizadores(): Call<List<Utilizador>>

    @GET("get_all_scores")
    fun obterScores(): Call<List<Int>>


   /* @GET("API/getNotes.php")
    fun list(): Call<List<Utilizador>>*/
}