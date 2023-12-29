package pt.ipt.walkingsensor.model

import com.google.gson.annotations.SerializedName
data class APIResult (
    @SerializedName("message") val message: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("error") val error: String?
)
