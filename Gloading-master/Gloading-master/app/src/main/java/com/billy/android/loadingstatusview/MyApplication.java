package com.billy.android.loadingstatusview;

import android.app.Application;

import com.billy.android.loading.Gloading;
import com.billy.android.loadingstatusview.wrapactivity.adapter.GlobalAdapter;
import com.squareup.leakcanary.LeakCanary;

/**
 * @author billy.qi
 * @since 19/3/19 15:27
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        //Init the default loading status view for global usage wrap a
        //      customer Adapter implementation
        Gloading.debug(BuildConfig.DEBUG);
        Gloading.initDefault(new GlobalAdapter());
    }
}
