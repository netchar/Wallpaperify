package netchar.com.auth

import android.content.SharedPreferences
import com.netchar.common.di.AuthPrefs
import javax.inject.Inject

class OAuthService(private val oauthPrefs: SharedPreferences) : IOAuthService {

    private var isAuthorized = false

    private var userApiAccessToken: String = ""

    override fun isAuthorized() = isAuthorized
    override fun getUserApiAccessTokenKey() = userApiAccessToken
}