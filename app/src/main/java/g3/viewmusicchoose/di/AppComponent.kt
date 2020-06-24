package g3.viewchoosephoto.di

import dagger.Component
import g3.viewmusicchoose.ui.MainMusicActivity
import g3.viewmusicchoose.ui.featured.ui.FeaturedFragment
import javax.inject.Singleton


@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(activity: MainMusicActivity)

    fun inject(fragment: FeaturedFragment)
}