package ir.amozkade.advancedAsisstiveTouche.helper.api

import ir.mobitrain.applicationcore.api.JWT
import java.io.IOException

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject


class TokenAuthenticator @Inject constructor(@ir.amozkade.advancedAsisstiveTouche.JWT private val jwt:String, private val refreshTokenService: RefreshTokenService): Authenticator {

    interface RefreshTokenService {
        //old jwt token
        @GET("Authentication/RefreshToken")
        fun refreshToken(@Query("oldToken") oldToken: String): Call<String>
    }

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code() == 401) {
            val refreshCall = refreshTokenService.refreshToken(jwt)
            refreshCall.request().headers().newBuilder().add("Content-Type", "text/plain; charset=UTF-8")
            //make it as retrofit synchronous call
            val refreshResponse = refreshCall.execute()
            return if (refreshResponse.isSuccessful && refreshResponse.code() == 200) {
                refreshResponse.body()?.let { newJWT ->
                    JWT.instance.setJWT(newJWT)
                    response.request().newBuilder()
                            .header("Authorization", "Bearer " + JWT.instance.computedJWT)
                            .build()
                }
            } else {
                return null
            }
        }
        return null
    }

}
