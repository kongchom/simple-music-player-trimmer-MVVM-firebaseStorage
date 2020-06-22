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

    public <E extends RealmObject> List<E> getListWithCondition(Class<E> eClass,
                                                                String fieldName, int value) {
        return convertRealmToList(eClass, sRealm.where(eClass).greaterThan(fieldName, value).findAll());
    }

    public <E extends RealmObject> List<E> getThemeOffline(Class<E> eClass,
                                                           String fieldName, int idTheme) {
        return convertRealmToList(eClass, sRealm.where(eClass).equalTo(fieldName, idTheme).findAll());
    }

    public <E extends RealmObject> void saveData(E obj) {
        beginTransaction();
        sRealm.copyToRealmOrUpdate(obj);
        commitTransaction();
    }

    public <E extends RealmObject> void saveListData(List<E> objs) {
        beginTransaction();
        sRealm.copyToRealmOrUpdate(objs);
        commitTransaction();
    }

    public <E extends RealmObject> RealmQuery getList(Class<E> eClass, Map<String, String> condition) {
//        LogUtils.i("TAG11",condition+"/////");
        RealmQuery realmQuery = sRealm.where(eClass);
        if (condition != null) {
            for (Object o : condition.entrySet()) {
                Map.Entry thisEntry = (Map.Entry) o;
                Object key = thisEntry.getKey();
                Object value = thisEntry.getValue();
                if (value instanceof Long) {
                    realmQuery.equalTo((String) key, (Long) value);
                } else if (value instanceof String) {
                    realmQuery.equalTo((String) key, (String) value);
                } else if (value instanceof Integer) {
                    realmQuery.equalTo((String) key, (Integer) value);
                } else if (value instanceof Boolean) {
                    realmQuery.equalTo((String) key, (Boolean) value);
                }
            }
        }
        return realmQuery;
    }

    public <E extends RealmObject> E getListFirst(Class<E> eClass, Map<String, String> condition) {
        return (E) getList(eClass, condition).findFirst();
    }

    public <E extends RealmObject> List<E> getListData(Class<E> eClass, Map<String, String> condition) {
        return convertRealmToList(eClass, getList(eClass, condition).findAll());
    }

    public <E extends RealmObject> void deleteObject(Class<E> eClass, Map<String, String> condition) {
        beginTransaction();
//        LogUtils.i("TAG11",getList(eClass, condition).findAll()+"//");
        getList(eClass, condition).findAll().deleteAllFromRealm();
        commitTransaction();
    }
    public <E extends RealmObject> void deleteObject(Class<E> eClass) {
        beginTransaction();
        RealmResults<E> eClassTheme = sRealm.where(eClass).findAll();
        eClassTheme.deleteAllFromRealm();
        commitTransaction();
    }



    private <E extends RealmObject> List<E> convertRealmToList(Class<E> eClass, List<E> objSources) {
        return sRealm.copyFromRealm(objSources);
    }

    public void beginTransaction() {
        if (sRealm.isInTransaction()) {
            sRealm.cancelTransaction();
        }
        sRealm.beginTransaction();
    }

    public void commitTransaction() {
        sRealm.commitTransaction();
    }
}