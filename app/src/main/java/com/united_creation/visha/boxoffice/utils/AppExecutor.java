package com.united_creation.visha.boxoffice.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {

    private static final Object LOCK = new Object();
    private final Executor diskIO;
    private static AppExecutor sInstance;

    private AppExecutor (Executor diskIO) {

        this.diskIO = diskIO;

    }

    public static AppExecutor getInstance () {

        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutor(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor getDiskIO() {
        return diskIO;
    }
}
