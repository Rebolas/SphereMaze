package pt.ipt.walkingsensor

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import pt.ipt.walkingsensor.service.APIConnector
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
    class RetrofitInitializer {
        private val gson:Gson = GsonBuilder().setLenient().create()

        private val retrofit = Retrofit.Builder()
            .baseUrl("http://a23043.pythonanywhere.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        fun connector() = retrofit.create(APIConnector::class.java)
    }

