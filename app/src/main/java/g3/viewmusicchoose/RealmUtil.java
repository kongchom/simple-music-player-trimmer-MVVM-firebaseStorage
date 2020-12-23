package g3.viewmusicchoose;

import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmUtil {
    private static RealmUtil sInstance;
    private static Realm sRealm;

    public static RealmUtil getInstance() {
        if (sInstance == null) {
            sInstance = new RealmUtil();
            sRealm = Realm.getDefaultInstance();
        }
        return sInstance;
    }

    public Realm getRealm() {
        return sRealm;
    }

    public <E extends RealmObject> List<E> getList(Class<E> eClass) {
        return convertRealmToList(eClass, sRealm.where(eClass).findAll());
    }

    private <E extends RealmObject> List<E> convertRealmToList(Class<E> eClass, List<E> objSources) {
        return sRealm.copyFromRealm(objSources);
    }
}