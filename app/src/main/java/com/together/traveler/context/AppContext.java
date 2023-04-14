package com.together.traveler.context;

import android.content.Context;

public class AppContext {
    private static AppContext sInstance;
    private Context mContext;

    private AppContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new AppContext(context);
        }
    }

    public static Context getContext() {
        return sInstance.mContext;
    }
}
