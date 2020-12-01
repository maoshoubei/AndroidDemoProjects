package com.ecity.mypermissioncheck;

import android.app.Application;

/**
 * @ProjectName: MyPermissionCheck
 * @Package: com.ecity.mypermissioncheck
 * @ClassName:
 * @Description:
 * @Author: Administrator
 * @CreateDate: 2020/7/21
 * @Version: 1.0
 */
public class App extends Application {
    private static App instance;

    public static App getsInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
