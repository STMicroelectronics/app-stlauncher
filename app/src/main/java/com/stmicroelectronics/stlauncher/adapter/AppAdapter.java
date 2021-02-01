package com.stmicroelectronics.stlauncher.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stmicroelectronics.stlauncher.data.AppDetails;
import com.stmicroelectronics.stlauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ST application adapter
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private final int mItemLayout;
    private Context mContext;
    private final OnClickListener mListener;
    private final ArrayList<AppDetails> arrayList = new ArrayList<>();

    public AppAdapter(int itemLayout, OnClickListener listener){
        mItemLayout = itemLayout;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(mItemLayout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppDetails app = arrayList.get(position);

        if (app.getAppName().contains(mContext.getString(R.string.settings_app_package_name))) {
            holder.appLogo.setImageResource(R.drawable.ic_settings);
            holder.appTitle.setTextColor(mContext.getColor(android.R.color.white));
            holder.appTitle.setBackgroundColor(mContext.getColor(R.color.colorPrimary));
        } else if ((app.getAppName().contains("stcoprom4")) || (app.getAppName().contains("st.m4"))) {
            holder.appLogo.setImageResource(R.drawable.ic_stapp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                holder.cardView.getBackground().setColorFilter(new BlendModeColorFilter(mContext.getColor(R.color.colorCopro), BlendMode.SRC_ATOP));
            }
            holder.appTitle.setTextColor(mContext.getColor(android.R.color.white));
            holder.appTitle.setBackgroundColor(mContext.getColor(R.color.colorCopro));
        } else if ((app.getAppName().contains(mContext.getString(R.string.st_app_package_name))) || (app.getAppName().contains(mContext.getString(R.string.st_app_package_name_bis)))) {
            holder.appLogo.setImageDrawable(app.getAppLogo());
            holder.appTitle.setTextColor(mContext.getColor(android.R.color.white));
            holder.appTitle.setBackgroundColor(mContext.getColor(R.color.colorPrimary));
        } else {
            holder.appLogo.setImageDrawable(app.getAppLogo());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                holder.cardView.getBackground().setColorFilter(new BlendModeColorFilter(mContext.getColor(android.R.color.background_light), BlendMode.SRC_ATOP));
            }
            holder.appTitle.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            holder.appTitle.setBackgroundColor(mContext.getColor(android.R.color.background_light));
        }
        holder.appTitle.setText(app.getAppLabel());
    }

    private int dpToPx(int dp) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,(float)dp,metrics);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public List<String> filterDisplayedAppList(ArrayList<AppDetails> fullList) {
        List<String> filteredList = new ArrayList<>();
        boolean addInList;
        for (AppDetails app:fullList) {
            addInList = true;
            for (AppDetails item:arrayList) {
                if ((app.getAppName().equals(item.getAppName())) || (app.getAppName().contains(mContext.getString(R.string.st_launcher_package_name)))) {
                    addInList = false;
                    break;
                }
            }
            if (addInList) {
                filteredList.add(app.getAppLabel());
            }
        }
        return filteredList;
    }

    public void addItem(AppDetails app) {
        int prevSize = arrayList.size();
        arrayList.add(app);
        notifyItemRangeInserted(prevSize,1);
    }

    public void addItems(ArrayList<AppDetails> list) {
        int prevSize = arrayList.size();
        arrayList.addAll(list);
        notifyItemRangeInserted(prevSize,arrayList.size() - prevSize);
    }

    public void removeItemByLabel(String label) {
        int position = 0;
        for (AppDetails item:arrayList) {
            if (item.getAppLabel().equals(label)) {
                arrayList.remove(position);
                notifyItemRemoved(position);
                return;
            }
            position++;
        }
    }

    public void removeAllItems() {
        int prevSize = arrayList.size();
        arrayList.clear();
        notifyItemRangeRemoved(0, prevSize);
    }

    public interface OnClickListener {
        void onClick(String stAppName);
        void onLongClick(String stAppLabel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView appLogo;
        TextView appTitle;
        View cardView;

        ViewHolder(View itemView) {
            super(itemView);
            appLogo = itemView.findViewById(R.id.stapp_logo);
            if (appLogo != null) {
                appTitle = itemView.findViewById(R.id.stapp_title);
                itemView.setOnLongClickListener(this);
            }
            itemView.setOnClickListener(this);
            cardView = itemView;
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            AppDetails app = arrayList.get(adapterPosition);
            mListener.onClick(app.getAppName());
        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            AppDetails app = arrayList.get(adapterPosition);
            mListener.onLongClick(app.getAppLabel());
            return true;
        }
    }

}
