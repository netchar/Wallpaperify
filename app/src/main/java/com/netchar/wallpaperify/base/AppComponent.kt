package com.netchar.wallpaperify.base

import android.app.Application
import dagger.Component
import javax.inject.Singleton
import dagger.BindsInstance



@Singleton
@Component(
    modules = arrayOf(AppModule::class)
)
interface AppComponent {

//    @Component.Builder
//    interface Builder {
//        @BindsInstance
//        fun application(application: App): Builder
//
//        fun build(): AppComponent
//    }

    fun inject(app: Application)
}