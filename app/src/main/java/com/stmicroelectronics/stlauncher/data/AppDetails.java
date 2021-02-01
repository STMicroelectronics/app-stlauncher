package com.stmicroelectronics.stlauncher.data;

import android.graphics.drawable.Drawable;

/**
 * Application Details data class
 */

public class AppDetails {
    private Drawable mAppLogo;
    private String mAppName;
    private String mAppLabel;

    public void setAppName(String appName) {
        this.mAppName = appName;
    }

    public void setAppLogo(Drawable appLogo) {
        this.mAppLogo = appLogo;
    }

    public void setAppLabel(String appLabel) {
        this.mAppLabel = appLabel;
    }

    public String getAppName() {
        return mAppName;
    }

    public Drawable getAppLogo() {
        return mAppLogo;
    }

    public String getAppLabel() {
        return mAppLabel;
    }
}
