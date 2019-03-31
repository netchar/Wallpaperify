package com.netchar.wallpaperify.di.modules

import com.netchar.wallpaperify.data.repository.IOAuthRepository
import com.netchar.wallpaperify.data.repository.OAuthRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


/**
 * Created by Netchar on 21.03.2019.
 * e.glushankov@gmail.com
 */

@Module
abstract class OAuthModule {

    @Binds
    @Singleton
    abstract fun bindOauthRepo(repo: OAuthRepository): IOAuthRepository
}