package com.loginext.easylocationpicker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class Utils {

    /*
     * check if google maps key is available */
    public static boolean isMapsKeyAvailable(Context context){
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;

            for (String key: bundle.keySet())
            {
                if (key.equals("com.google.android.geo.API_KEY")){
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }

        return false;
    }
}
