package netchar.com.auth.di

import android.content.SharedPreferences
import com.netchar.common.di.AuthPrefs
import com.netchar.common.di.OAuthInterceptor
import com.netchar.common.utils.IBuildConfig
import dagger.Module
import dagger.Provides
import netchar.com.auth.AuthInterceptor
import netchar.com.auth.IOAuthService
import netchar.com.auth.OAuthService
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
object OAuthModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideOauthService(@AuthPrefs oauthPrefs: SharedPreferences): IOAuthService = OAuthService(oauthPrefs)

    @JvmStatic
    @Provides
    @Singleton
    @OAuthInterceptor
    fun provideOAuthInterceptor(oAuthRepository: IOAuthService, buildConfig: IBuildConfig): Interceptor = AuthInterceptor(oAuthRepository, buildConfig)
}