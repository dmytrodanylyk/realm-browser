package com.dd.realmbrowser;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dd.realmbrowser.utils.L;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RealmBrowserActivity extends AppCompatActivity {

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

        mAdapter = new RealmAdapter(realmObjects, mSelectedFieldList);
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
                            if(mSelectedFieldList.isEmpty()) {
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

    class RealmAdapter extends RecyclerView.Adapter<RealmAdapter.ViewHolder> {
        private RealmResults<? extends RealmObject> mRealmObjects;
        private List<Field> mFieldList;

        public RealmAdapter(RealmResults<? extends RealmObject> realmObjects, List<Field> fieldList) {
            mRealmObjects = realmObjects;
            mFieldList = fieldList;
        }

        public void setFieldList(List<Field> fieldList) {
            mFieldList = fieldList;
        }

        @Override
        public RealmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_realm_browser, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position % 2 == 0) {
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.grey));
            } else {
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.white));
            }

            LinearLayout.LayoutParams layoutParams2 = createLayoutParams();
            LinearLayout.LayoutParams layoutParams3 = createLayoutParams();

            if (mFieldList.size() > 0) {
                holder.txtIndex.setText(String.valueOf(position));

                RealmObject realmObject = mRealmObjects.get(position);

                String methodName1 = createMethodName(mFieldList.get(0));
                if(methodName1 != null) {
                    holder.txtColumn1.setText(invokeMethod(realmObject, methodName1));
                }

                if (mFieldList.size() > 1) {
                    String methodName2 = createMethodName(mFieldList.get(1));
                    holder.txtColumn2.setText(invokeMethod(realmObject, methodName2));
                    layoutParams2.weight = 1;

                    if (mFieldList.size() > 2) {
                        String methodName3 = createMethodName(mFieldList.get(2));
                        holder.txtColumn3.setText(invokeMethod(realmObject, methodName3));
                        layoutParams3.weight = 1;
                    } else {
                        layoutParams3.weight = 0;
                    }
                } else {
                    layoutParams2.weight = 0;
                }
            } else {
                holder.txtColumn1.setText(null);
                holder.txtColumn2.setText(null);
                holder.txtColumn3.setText(null);
            }

            holder.txtColumn2.setLayoutParams(layoutParams2);
            holder.txtColumn3.setLayoutParams(layoutParams3);
        }

        private LinearLayout.LayoutParams createLayoutParams() {
            return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @Nullable
        private String createMethodName(Field field) {
            String methodName = null;

            Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                // ParameterizedType pType = (ParameterizedType) type;
                // TODO support pType.getRawType() pType.getActualTypeArguments()[0]
            } else {
                if (field.getType().equals(boolean.class)) {
                    if(field.getName().contains("is")) {
                        methodName = field.getName();
                    } else {
                        methodName = "is" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                    }
                } else {
                    methodName = "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                }
            }

            return methodName;
        }

        @Override
        public int getItemCount() {
            return mRealmObjects.size();
        }

        private String invokeMethod(Object realmObject, String methodName) {
            String result = null;
            try {
                Method method = realmObject.getClass().getMethod(methodName);
                result = method.invoke(realmObject).toString();
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                L.e(e.toString());
            }
            return result;

        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView txtIndex;
            public TextView txtColumn1;
            public TextView txtColumn2;
            public TextView txtColumn3;

            public ViewHolder(View v) {
                super(v);
                txtIndex = (TextView) v.findViewById(R.id.txtIndex);
                txtColumn1 = (TextView) v.findViewById(R.id.txtColumn1);
                txtColumn2 = (TextView) v.findViewById(R.id.txtColumn2);
                txtColumn3 = (TextView) v.findViewById(R.id.txtColumn3);
            }
        }

    }
}
