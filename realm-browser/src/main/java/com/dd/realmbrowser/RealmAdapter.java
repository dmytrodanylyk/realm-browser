package com.dd.realmbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dd.realmbrowser.utils.L;
import io.realm.RealmObject;
import io.realm.RealmResults;

import java.lang.reflect.*;
import java.util.List;

public class RealmAdapter extends RecyclerView.Adapter<RealmAdapter.ViewHolder> {

    public interface Listener {
        void onRowItemClicked();
    }

    private RealmResults<? extends RealmObject> mRealmObjects;
    private Context mContext;
    private List<Field> mFieldList;
    private Listener mListener;

    public RealmAdapter(@NonNull Context context, @NonNull RealmResults<? extends RealmObject> realmObjects,
                        @NonNull List<Field> fieldList, @NonNull Listener listener) {
        mContext = context;
        mRealmObjects = realmObjects;
        mFieldList = fieldList;
        mListener = listener;
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
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
        } else {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        LinearLayout.LayoutParams layoutParams2 = createLayoutParams();
        LinearLayout.LayoutParams layoutParams3 = createLayoutParams();

        if (mFieldList.size() > 0) {
            holder.txtIndex.setText(String.valueOf(position));

            RealmObject realmObject = mRealmObjects.get(position);

            Field field1 = mFieldList.get(0);
            if (isParameterizedField(field1)) {
                holder.txtColumn1.setOnClickListener(createClickListener());
                holder.txtColumn1.setText(createParameterizedName(field1));
            } else {
                String methodName1 = createMethodName(field1);
                holder.txtColumn1.setText(invokeMethod(realmObject, methodName1));
            }

            if (mFieldList.size() > 1) {
                Field field2 = mFieldList.get(1);
                if (isParameterizedField(field2)) {
                    holder.txtColumn2.setOnClickListener(createClickListener());
                    holder.txtColumn2.setText(createParameterizedName(field2));
                } else {
                    String methodName2 = createMethodName(field2);
                    holder.txtColumn2.setText(invokeMethod(realmObject, methodName2));
                }

                layoutParams2.weight = 1;

                if (mFieldList.size() > 2) {
                    Field field3 = mFieldList.get(2);
                    if (isParameterizedField(field3)) {
                        holder.txtColumn3.setOnClickListener(createClickListener());
                        holder.txtColumn3.setText(createParameterizedName(field3));
                    } else {
                        String methodName3 = createMethodName(field3);
                        holder.txtColumn3.setText(invokeMethod(realmObject, methodName3));
                    }

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


    private View.OnClickListener createClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRowItemClicked();
            }
        };
    }

    private LinearLayout.LayoutParams createLayoutParams() {
        return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private boolean isParameterizedField(@NonNull Field field) {
        return field.getGenericType() instanceof ParameterizedType;
    }


    @Nullable
    private String createParameterizedName(@NonNull Field field) {
        ParameterizedType pType = (ParameterizedType) field.getGenericType();
        String rawType = pType.getRawType().toString();
        int rawTypeIndex = rawType.lastIndexOf(".");
        if(rawTypeIndex > 0) {
            rawType = rawType.substring(rawTypeIndex + 1);
        }

        String argument = pType.getActualTypeArguments()[0].toString();
        int argumentIndex = argument.lastIndexOf(".");
        if(argumentIndex > 0) {
            argument = argument.substring(argumentIndex + 1);
        }

        return rawType + "<" + argument + ">";
    }

    @Nullable
    private String createMethodName(@NonNull Field field) {
        String methodName;
        if (field.getType().equals(boolean.class)) {
            if (field.getName().contains("is")) {
                methodName = field.getName();
            } else {
                methodName = "is" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
            }
        } else {
            methodName = "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
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
