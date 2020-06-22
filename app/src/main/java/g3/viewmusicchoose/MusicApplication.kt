package g3.viewmusicchoose

import android.app.Application
import g3.viewchoosephoto.di.AppComponent
import g3.viewchoosephoto.di.AppModule
import g3.viewchoosephoto.di.DaggerAppComponent
import io.realm.Realm
import io.realm.RealmConfiguration
import lib.managerstorage.ManagerStorage.Companion.init
import timber.log.Timber

class MusicApplication : Application() {

    lateinit var appComponent: AppComponent
    private set

    companion object {
        lateinit var instance: MusicApplication
        private set
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        instance = this
        init()
        SharePrefUtils.init(applicationContext)
        Realm.init(this)
        val config2 = RealmConfiguration.Builder()
            .name("default2.realm")
            .schemaVersion(0)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config2)
        appComponent = buildAppComponent()
    }

    private fun buildAppComponent(): AppComponent {
        return DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

}