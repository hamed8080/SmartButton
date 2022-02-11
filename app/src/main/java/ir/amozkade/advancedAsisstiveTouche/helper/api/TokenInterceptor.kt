package ir.amozkade.advancedAsisstiveTouche.helper.api

import ir.mobitrain.applicationcore.api.JWT
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class TokenInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (request.header("No-Authentication") == null) {
            JWT.instance.computedJWT?.let { jwt ->
                val finalToken = "Bearer $jwt"
                request = request.newBuilder()
                        .addHeader("Authorization", finalToken)
                        .build()
            }
        }
        return chain.proceed(request)
    }
}