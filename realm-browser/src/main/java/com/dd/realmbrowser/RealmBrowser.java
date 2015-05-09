package com.dd.realmbrowser;

import io.realm.RealmObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RealmBrowser {

    private static final RealmBrowser sInstance = new RealmBrowser();
    private List<Class<? extends RealmObject>> mRealmModelList;

    private RealmBrowser() {
        mRealmModelList = new ArrayList<>();
    }

    public List<Class<? extends RealmObject>> getRealmModelList() {
        return mRealmModelList;
    }

    @SafeVarargs
    public final void addRealmModel(Class<? extends RealmObject>... arr) {
        mRealmModelList.addAll(Arrays.asList(arr));
    }

    public static RealmBrowser getInstance() {
        return sInstance;
    }

}
