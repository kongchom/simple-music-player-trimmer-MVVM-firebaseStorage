package g3.viewmusicchoose;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

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
        return convertRealmToList(sRealm.where(eClass).findAll());
    }

    private <E extends RealmObject> List<E> convertRealmToList(List<E> objSources) {
        return sRealm.copyFromRealm(objSources);
    }
}