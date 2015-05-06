package com.dd.realmsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.dd.realmbrowser.RealmBrowser;
import com.dd.realmbrowser.RealmFilesActivity;
import com.dd.realmbrowser.RealmModelsActivity;
import io.realm.Realm;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final String REALM_FILE_NAME = "db7";
    private TextView mTxtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RealmBrowser.getInstance().getRealmModelList().add(User.class);

        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        findViewById(R.id.btnInsert).setOnClickListener(this);
        findViewById(R.id.btnRemove).setOnClickListener(this);
        findViewById(R.id.btnOpenFile).setOnClickListener(this);
        findViewById(R.id.btnOpenModel).setOnClickListener(this);

        updateTitle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInsert:
                insertUsers(100);
                updateTitle();
                break;
            case R.id.btnRemove:
                removeAllUsers();
                updateTitle();
                break;
            case R.id.btnOpenFile:
                startRealmFilesActivity();
                break;
            case R.id.btnOpenModel:
                startRealmModelsActivity();
                break;
        }
    }

    private void updateTitle() {
        Realm realm = Realm.getInstance(getApplicationContext(), REALM_FILE_NAME);
        int size = realm.allObjects(User.class).size();
        mTxtTitle.setText(String.format("Items in database: %d", size));
        realm.close();
    }

    private void removeAllUsers() {
        Realm realm = Realm.getInstance(getApplicationContext(), REALM_FILE_NAME);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.clear(User.class);
            }
        });

        realm.close();
    }

    private void insertUsers(int count) {
        Realm realm = Realm.getInstance(getApplicationContext(), REALM_FILE_NAME);

        final List<User> userList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setName("Jon Doe " + i);
            user.setIsBlocked(Math.random() > 0.5);
            user.setAge(i);
            user.setLat(49.8397473);
            user.setLon(24.0233077);
            userList.add(user);
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(userList);
            }
        });

        realm.close();
    }

    private void startRealmFilesActivity() {
        Intent intent = new Intent(this, RealmFilesActivity.class);
        startActivity(intent);
    }

    private void startRealmModelsActivity() {
        RealmModelsActivity.start(this, REALM_FILE_NAME);
    }

}
