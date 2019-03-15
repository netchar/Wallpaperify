package com.netchar.wallpaperify.di.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ViewModelFactory @Inject constructor(
    private val providedViewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    private fun <T> findAssignableModelOrNull(modelClass: Class<T>) = providedViewModels.entries.firstOrNull { modelClass.isAssignableFrom(it.key) }?.value

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vmProvider: Provider<ViewModel> = providedViewModels[modelClass] ?: findAssignableModelOrNull(modelClass) ?: throw IllegalArgumentException("unknown model class $modelClass")
        return try {
            vmProvider.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}