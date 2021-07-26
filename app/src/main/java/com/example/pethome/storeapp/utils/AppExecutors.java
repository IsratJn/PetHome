
package com.example.pethome.storeapp.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public final class AppExecutors {

    private static volatile AppExecutors INSTANCE;

    private final Executor diskIO;
    private final Executor mainThread;
    private AppExecutors(Executor diskIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }
    public static AppExecutors getInstance() {
        if (INSTANCE == null) {
            synchronized (AppExecutors.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppExecutors(
                            Executors.newSingleThreadExecutor(),
                            new MainThreadExecutor()
                    );
                }
            }
        }
        return INSTANCE;
    }
    public Executor getDiskIO() {
        return diskIO;
    }
    public Executor getMainThread() {
        return mainThread;
    }
    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}