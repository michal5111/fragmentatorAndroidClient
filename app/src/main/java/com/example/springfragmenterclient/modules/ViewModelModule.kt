package com.example.springfragmenterclient.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.springfragmenterclient.activities.FragmentRequestViewModel
import com.example.springfragmenterclient.activities.SelectedLineViewModel
import com.example.springfragmenterclient.activities.SelectedMovieViewModel
import com.example.springfragmenterclient.fragments.SearchMovieViewModel
import com.example.springfragmenterclient.fragments.SearchPhraseViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        viewModels[modelClass]?.get() as T
}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModuleFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(FragmentRequestViewModel::class)
    internal abstract fun fragmentRequestViewModel(viewModel: FragmentRequestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SelectedLineViewModel::class)
    internal abstract fun selectedLineViewModel(viewModel: SelectedLineViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SelectedMovieViewModel::class)
    internal abstract fun selectedMovieViewModel(viewModel: SelectedMovieViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchMovieViewModel::class)
    internal abstract fun searchMovieViewModel(viewModel: SearchMovieViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchPhraseViewModel::class)
    internal abstract fun searchPhraseViewModel(viewModel: SearchPhraseViewModel): ViewModel
}