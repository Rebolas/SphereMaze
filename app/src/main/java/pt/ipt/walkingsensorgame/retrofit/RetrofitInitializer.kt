package pt.ipt.walkingsensorgame.retrofit

import pt.ipt.walkingsensorgame.retrofit.service.APIConnector
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
    class RetrofitInitializer {
        private val retrofit = Retrofit.Builder()
            .baseUrl("a23043.pythonanywhere.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun connector() = retrofit.create(APIConnector::class.java)
    }

