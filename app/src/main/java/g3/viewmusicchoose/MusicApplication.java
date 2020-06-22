package g3.viewmusicchoose;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import lib.managerstorage.ManagerStorage;

public class MusicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ManagerStorage.init();
        SharePrefUtils.init(getApplicationContext());

        Realm.init(this);
        RealmConfiguration config2 = new RealmConfiguration.Builder()
                .name("default2.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config2);
    }


}
