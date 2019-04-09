package com.andy.filecopymove.sample.app;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    /**
     * 文件复制
     */
    public static final int TYPE_COPY = 1;
    /**
     * 文件移动
     */
    public static final int TYPE_MOVE = 2;
    /**
     * 更多
     */
    public static final int TYPE_MORE = 3;
    /**
     * 文件操作requestCode
     */
    public static final int FILE_OPERATION_REQUEST_CODE = 0;
    /**
     * 替换勾选应用到全部
     */
    public static final int REPLACE_CHECKED_TRUE = 4;
    /**
     * 替换
     */
    public static final int REPLACE_CHECKED_FALSE = 5;
    /**
     * 跳过勾选应用到全部
     */
    public static final int SKIP_CHECKED_TRUE = 6;
    /**
     * 跳过
     */
    public static final int SKIP_CHECKED_FALSE = 7;
    /**
     * 保留勾选应用到全部
     */
    public static final int KEEP_CHECKED_TRUE = 8;
    /**
     * 保留
     */
    public static final int KEEP_CHECKED_FALSE = 9;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }
}
