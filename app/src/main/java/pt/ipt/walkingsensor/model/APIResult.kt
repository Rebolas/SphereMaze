package pt.ipt.walkingsensor.model

import com.google.gson.annotations.SerializedName
data class APIResult (
    @SerializedName("message") val message: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("error") val error: String?,
    @SerializedName("last_level") val lastLevel: Int?
)
