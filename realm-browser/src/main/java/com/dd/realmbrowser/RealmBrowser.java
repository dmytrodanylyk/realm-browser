package com.dd.realmbrowser;

import io.realm.RealmObject;

import java.util.ArrayList;
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

    public static RealmBrowser getInstance() {
        return sInstance;
    }
}
