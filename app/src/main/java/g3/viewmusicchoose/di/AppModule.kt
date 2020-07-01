package g3.viewchoosephoto.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import g3.viewmusicchoose.repo.featured.IMusicRepository
import g3.viewmusicchoose.repo.featured.LocalMusicRepository
import javax.inject.Singleton

@Module
class AppModule constructor(
    private val applicationContext: Application
) {

    @Provides
    @Singleton
    fun provideContext(): Context = applicationContext

    @Provides
    @Singleton
    fun provideMusicRepository(): IMusicRepository {
        return LocalMusicRepository(applicationContext)
    }
}