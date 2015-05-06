package com.dd.realmbrowser;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RealmBrowserActivity extends AppCompatActivity implements RealmAdapter.Listener {

    private static final String EXTRAS_REALM_FILE_NAME = "EXTRAS_REALM_FILE_NAME";
    private static final String EXTRAS_REALM_MODEL_INDEX = "REALM_MODEL_INDEX";

    private Realm mRealm;
    private Class<? extends RealmObject> mRealmObjectClass;
    private RealmAdapter mAdapter;
    private TextView mTxtIndex;
    private TextView mTxtColumn1;
    private TextView mTxtColumn2;
    private TextView mTxtColumn3;
    private List<Field> mSelectedFieldList;
    private List<Field> mFieldsList;

    public static void start(Activity activity, int realmModelIndex, String realmFileName) {
        Intent intent = new Intent(activity, RealmBrowserActivity.class);
        intent.putExtra(EXTRAS_REALM_MODEL_INDEX, realmModelIndex);
        intent.putExtra(EXTRAS_REALM_FILE_NAME, realmFileName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_realm_browser);

        String realmFileName = getIntent().getStringExtra(EXTRAS_REALM_FILE_NAME);
        int index = getIntent().getIntExtra(EXTRAS_REALM_MODEL_INDEX, 0);

        mRealm = Realm.getInstance(getApplicationContext(), realmFileName);

        mRealmObjectClass = RealmBrowser.getInstance().getRealmModelList().get(index);
        RealmResults<? extends RealmObject> realmObjects = mRealm.allObjects(mRealmObjectClass);

        mSelectedFieldList = new ArrayList<>();
        mFieldsList = new ArrayList<>();
        mFieldsList.addAll(Arrays.asList(mRealmObjectClass.getDeclaredFields()));

        mAdapter = new RealmAdapter(this, realmObjects, mSelectedFieldList, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        mTxtIndex = (TextView) findViewById(R.id.txtIndex);
        mTxtColumn1 = (TextView) findViewById(R.id.txtColumn1);
        mTxtColumn2 = (TextView) findViewById(R.id.txtColumn2);
        mTxtColumn3 = (TextView) findViewById(R.id.txtColumn3);

        selectDefaultFields();
        updateColumnTitle(mSelectedFieldList);
    }


    @Override
    protected void onDestroy() {
        if (mRealm != null) {
            mRealm.close();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_columns) {
            showColumnsDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRowItemClicked() {

    }

    private void selectDefaultFields() {
        for (Field field : mFieldsList) {
            if (mSelectedFieldList.size() < 3) {
                mSelectedFieldList.add(field);
            }
        }
    }

    private void updateColumnTitle(List<Field> columnsList) {
        mTxtIndex.setText("#");

        LinearLayout.LayoutParams layoutParams2 = createLayoutParams();
        LinearLayout.LayoutParams layoutParams3 = createLayoutParams();

        if (columnsList.size() > 0) {
            mTxtColumn1.setText(columnsList.get(0).getName());

            if (columnsList.size() > 1) {
                mTxtColumn2.setText(columnsList.get(1).getName());
                layoutParams2.weight = 1;

                if (columnsList.size() > 2) {
                    mTxtColumn3.setText(columnsList.get(2).getName());
                    layoutParams3.weight = 1;
                } else {
                    layoutParams3.weight = 0;
                }
            } else {
                layoutParams2.weight = 0;
            }
        }

        mTxtColumn2.setLayoutParams(layoutParams2);
        mTxtColumn3.setLayoutParams(layoutParams3);
    }

    private LinearLayout.LayoutParams createLayoutParams() {
        return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showColumnsDialog() {
        final String[] items = new String[mFieldsList.size()];
        for (int i = 0; i < items.length; i++) {
            Field field = mFieldsList.get(i);
            items[i] = field.getName();
        }

        boolean[] checkedItems = new boolean[mFieldsList.size()];
        for (int i = 0; i < checkedItems.length; i++) {
            checkedItems[i] = mSelectedFieldList.contains(mFieldsList.get(i));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Columns to display");
        builder.setMultiChoiceItems(items, checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        Field field = mFieldsList.get(indexSelected);
                        if (isChecked) {
                            mSelectedFieldList.add(field);
                        } else if (mSelectedFieldList.contains(field)) {
                            mSelectedFieldList.remove(field);
                            if (mSelectedFieldList.isEmpty()) {
                                selectDefaultFields();
                            }
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        updateColumnTitle(mSelectedFieldList);
                        mAdapter.setFieldList(mSelectedFieldList);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
