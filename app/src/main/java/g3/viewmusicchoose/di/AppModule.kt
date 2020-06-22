package g3.viewchoosephoto.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Singleton

@Module
class AppModule constructor(
    private val applicationContext: Application
) {

    @Provides
    @Singleton
    fun provideContext(): Context = applicationContext

//    @Provides
//    @Singleton
//    fun provideLocalMediaRepository(): ILocalMediaRepository {
//        return LocalMediaRepository(context)
//    }

}