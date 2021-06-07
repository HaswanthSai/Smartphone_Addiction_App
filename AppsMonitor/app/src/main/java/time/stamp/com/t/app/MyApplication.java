package time.stamp.com.t.app;

import android.app.Application;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import time.stamp.com.t.AppConst;
import time.stamp.com.t.BuildConfig;
import time.stamp.com.t.data.AppItem;
import time.stamp.com.t.data.DataManager;
import time.stamp.com.t.db.DbHistoryExecutor;
import time.stamp.com.t.db.DbIgnoreExecutor;
import time.stamp.com.t.service.AppService;
import time.stamp.com.t.util.CrashHandler;
import time.stamp.com.t.util.PreferenceManager;



public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.init(this);
        getApplicationContext().startService(new Intent(getApplicationContext(), AppService.class));
        DbIgnoreExecutor.init(getApplicationContext());
        DbHistoryExecutor.init(getApplicationContext());
        DataManager.init();
        addDefaultIgnoreAppsToDB();
        if (AppConst.CRASH_TO_FILE) CrashHandler.getInstance().init();
    }

    private void addDefaultIgnoreAppsToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> mDefaults = new ArrayList<>();
                mDefaults.add("com.android.settings");
                mDefaults.add(BuildConfig.APPLICATION_ID);
                for (String packageName : mDefaults) {
                    AppItem item = new AppItem();
                    item.mPackageName = packageName;
                    item.mEventTime = System.currentTimeMillis();
                    DbIgnoreExecutor.getInstance().insertItem(item);
                }
            }
        }).run();
    }
}
