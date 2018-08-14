package com.example.visha.boxoffice.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.visha.boxoffice.R;

import static android.content.Context.MODE_PRIVATE;

public class UserPreferences {

    public static final int TOP_RATED = 0;
    public static final int POPULAR = 1;
    public static final int FAVOURITE = 2;

    private static SharedPreferences getSharedPreferences (Context context) {
        return context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
    }

    public static int getSavedState (Context context) {

        int state = getSharedPreferences(context).getInt(context.getString(R.string.preferences_name), TOP_RATED);
        return state;

    }

    public static void updateUserPreferences (Context context, int category) {

        getSharedPreferences(context).edit().putInt(context.getString(R.string.preferences_name), category).apply();

    }

}
