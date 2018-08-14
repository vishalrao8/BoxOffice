package com.example.visha.boxoffice.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkState {

    public static boolean isConnected(ConnectivityManager connectivityManager){

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

}
