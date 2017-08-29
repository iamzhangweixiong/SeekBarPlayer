package com.zhangwx.permission.bridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import com.zhangwx.seekbarplayer.MyApplication;

import java.lang.ref.WeakReference;

/**
 * Created by zhangwx on 2017/4/5.
 */

public class PermissionRequestBridge/* implements IPermissionRequest */{

    public static final String PERMISSION_CHECK_ACTION = "permission_check_action";
    public static final String KEY_CHECK_RESULT = "key_check_result";
    public static final String KEY_CHECK_PERMISSION_CODE = "key_permission_code";

    private static PermissionRequestBridge sInstance;
    private WeakReference<Context> mContext;
    private RequestCallBack mRequestCallBack;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PERMISSION_CHECK_ACTION.equals(intent.getAction())) {
                final boolean isGranted = intent.getBooleanExtra(KEY_CHECK_RESULT, false);
                final int requestCode = intent.getIntExtra(KEY_CHECK_PERMISSION_CODE, 0);
                mRequestCallBack.onRequestResult(isGranted);
            }
        }
    };

    public static PermissionRequestBridge getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PermissionRequestBridge.class) {
                if (sInstance == null) {
                    sInstance = new PermissionRequestBridge(context);
                }
            }
        }
        return sInstance;
    }

    public PermissionRequestBridge(Context context) {
        this.mContext = new WeakReference<>(context);
        registerBroadcast();
    }

    /*@Override*/
    public void request(boolean needRationale, int requestCode, String[] permissionGroup, String title, String rationale, RequestCallBack callBack) {
        mRequestCallBack = callBack;
        RequestBridgeActivity.startSelf(mContext.get(), needRationale, requestCode, permissionGroup, title, rationale);
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PERMISSION_CHECK_ACTION);
        MyApplication.getContext().registerReceiver(mReceiver, filter);
    }


    public interface RequestCallBack {
        void onRequestResult(boolean isGranted);
    }
}
