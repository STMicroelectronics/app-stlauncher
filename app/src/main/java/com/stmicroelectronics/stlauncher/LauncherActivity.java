package com.stmicroelectronics.stlauncher;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stmicroelectronics.stlauncher.adapter.AppAdapter;
import com.stmicroelectronics.stlauncher.data.AppDetails;
import com.stmicroelectronics.stlauncher.dialog.AddAppDialogFragment;
import com.stmicroelectronics.stlauncher.dialog.RemoveAppDialogFragment;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LauncherActivity extends AppCompatActivity implements AppAdapter.OnClickListener, RemoveAppDialogFragment.RemoveAppDialogListener {

    RecyclerView mAppList;
    View mContainerView;
    FloatingActionButton mFab;
    SwipeRefreshLayout mSwipeRefreshView;

    int mSpanCount;

    String mAppName;
    String mAppNameBis;
    String mSettingsAppName;
    String mLauncherName;

    private PackageManager mManager;
    private AppAdapter mAppAdapter;

    private final ArrayList<AppDetails> mFullAppList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // hide toolbar (not required)
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.hide();
        }

        mAppList = findViewById(R.id.application_list);
        mContainerView = findViewById(R.id.full_container);
        mFab = findViewById(R.id.fab);
        mSwipeRefreshView = findViewById(R.id.swipe_refresh_app);

        mSpanCount = getResources().getInteger(R.integer.app_span_count);

        mAppName = getResources().getString(R.string.st_app_package_name);
        mAppNameBis = getResources().getString(R.string.st_app_package_name_bis);
        mSettingsAppName = getResources().getString(R.string.settings_app_package_name);
        mLauncherName = getResources().getString(R.string.st_launcher_package_name);

        mManager = getPackageManager();

        // initialize the RecyclerView for ST application list
        initRecycler();

        // initialize application list
        updateFullAppList();

        // display default application list
        refreshDisplayedAppList();

        // init swipe refresh layout (app list refresh)
        initSwipeRefresh();

        // set floating action button listener
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("Add application requested");
                List<String> appList = mAppAdapter.filterDisplayedAppList(mFullAppList);
                if (! appList.isEmpty()) {
                    AddAppDialogFragment fragment = new AddAppDialogFragment(appList, new AddAppDialogFragment.AddAppDialogListener() {
                        @Override
                        public void onDialogPositiveAddApp(String name) {
                            Timber.d("Add application in list: %s", name);
                            for (AppDetails app : mFullAppList) {
                                if (app.getAppLabel().equals(name)) {
                                    final AppDetails mAppToAdd = app;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAppAdapter.addItem(mAppToAdd);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onDialogNegativeAddApp() {
                            Timber.d("Add application in list cancelled");
                        }
                    });
                    fragment.show(getSupportFragmentManager(), "com.stmicroelectronics.stlauncher.fab.tag");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"No more application available, swipe down to refresh if needed",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void initRecycler(){
        mAppAdapter = new AppAdapter(R.layout.item_applist, this);
        mAppList.setAdapter(mAppAdapter);
        mAppList.setLayoutManager(new GridLayoutManager(this, mSpanCount));
    }

    private void initSwipeRefresh() {
        mSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshView.setRefreshing(true);
                updateFullAppList();
                mSwipeRefreshView.setRefreshing(false);
            }
        });
        mSwipeRefreshView.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
    }

    private void updateFullAppList() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        mFullAppList.clear();

        List<ResolveInfo> availableActivities = mManager.queryIntentActivities(intent, 0);
        for(ResolveInfo ri:availableActivities) {
            AppDetails stApp = new AppDetails();
            stApp.setAppName(ri.activityInfo.packageName);
            stApp.setAppLabel(ri.loadLabel(mManager).toString());
            stApp.setAppLogo(ri.loadIcon(mManager));
            mFullAppList.add(stApp);
        }
    }

    private void refreshDisplayedAppList(){
        for (AppDetails app:mFullAppList){
            if (((app.getAppName().contains(mAppName)) || (app.getAppName().contains(mAppNameBis))
                    || (app.getAppName().equals(mSettingsAppName)))
                    && (! app.getAppName().contains(mLauncherName))) {
                mAppAdapter.addItem(app);
            }
        }
    }

    @Override
    public void onClick(String stAppName) {
        Intent intent = mManager.getLaunchIntentForPackage(stAppName);
        if (intent != null) {
            ActivityOptions options = ActivityOptions.makeBasic();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent, options.toBundle());
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onLongClick(String stAppLabel) {
        RemoveAppDialogFragment fragment = new RemoveAppDialogFragment(stAppLabel,this);
        fragment.show(getSupportFragmentManager(), "com.stmicroelectronics.stlauncher.longclick.tag");
    }

    @Override
    public void onDialogPositiveRemoveApp(String name) {
        Timber.d("Suppression confirmed of %s", name);
        mAppAdapter.removeItemByLabel(name);
    }

    @Override
    public void onDialogNegativeRemoveApp(String name) {
        Timber.d("Suppression canceled of %s", name);
    }

}
