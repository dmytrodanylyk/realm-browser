package com.dd.realmbrowser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RealmFilesActivity extends AppCompatActivity {

    private List<String> mIgnoreExtensionList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_list_view);

        mIgnoreExtensionList = new ArrayList<>();
        mIgnoreExtensionList.add("log");
        mIgnoreExtensionList.add("lock");

        File dataDir = new File(getApplicationInfo().dataDir, "files");
        File[] files = dataDir.listFiles();
        List<String> fileList = new ArrayList<>();
        for (File file : files) {
            String fileName = file.getName();
            if (isValid(fileName)) {
                fileList.add(fileName);
            }
        }

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(position);
            }
        });
    }

    private boolean isValid(String fileName) {
        boolean isValid = true;
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            String extension = fileName.substring(index);
            isValid = mIgnoreExtensionList.contains(extension);
        }
        return isValid;
    }

    private void onItemClicked(int position) {
        try {
            String realmFileName = mAdapter.getItem(position);
            Realm realm = Realm.getInstance(getApplicationContext(), realmFileName);
            realm.close();
            RealmModelsActivity.start(this, realmFileName);
        } catch (RealmMigrationNeededException e) {
            Toast.makeText(getApplicationContext(), "RealmMigrationNeededException", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Can't open realm instance", Toast.LENGTH_SHORT).show();
        }
    }
}
