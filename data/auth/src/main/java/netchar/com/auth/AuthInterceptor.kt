package netchar.com.auth

import com.netchar.common.utils.IBuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
        private val authService: IOAuthService,
        private val buildConfigPrefs: IBuildConfig
)
    : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", getRequestHeader())
                .build()
        return chain.proceed(request)
    }

    private fun getRequestHeader() = if (authService.isAuthorized()) {
        "Bearer ${authService.getUserApiAccessTokenKey()}"
    } else {
        "Client-ID ${buildConfigPrefs.getApiAccessKey()}"
    }
}