package com.zhangwx.permission.bridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.zhangwx.permission.AppSettingsDialog;
import com.zhangwx.permission.EasyPermissions;
import com.zhangwx.permission.PermissionUtils;
import com.zhangwx.seekbarplayer.R;

import java.util.List;

/**
 * Created by zhangwx on 2017/4/5.
 * <p>
 * <p> 一个空的Activity，用于权限请求的中转 </p>
 */

public class RequestBridgeActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks {

    private int mRequestCode;
    private String[] mPermissionGroup;
    private String mRationale;
    private String mTitle;
    private boolean mNeedRationale;
    public static final String KEY_CHECK_TYPE_CODE = "key_check_type_code";
    public static final String KEY_CHECK_TITLE = "key_check_title";
    public static final String KEY_CHECK_RATIONALE = "key_check_rationale";
    public static final String KEY_CHECK_GROUP = "key_check_group";
    public static final String KEY_CHECK_NEED_RATIONALE = "key_check_need_rationale";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mRequestCode = intent.getIntExtra(KEY_CHECK_TYPE_CODE, PermissionUtils.DEFAULT_CODE);
        mPermissionGroup = intent.getStringArrayExtra(KEY_CHECK_GROUP);
        mRationale = intent.getStringExtra(KEY_CHECK_RATIONALE);
        mTitle = intent.getStringExtra(KEY_CHECK_TITLE);
        mNeedRationale = intent.getBooleanExtra(KEY_CHECK_NEED_RATIONALE, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNeedRationale) {
            EasyPermissions.requestPermissions(this,
                    PermissionUtils.WITHOUT_ICON,
                    mTitle,
                    mRationale,
                    mRequestCode,
                    mPermissionGroup);
        } else {
            EasyPermissions.requestPermissionsWithOutRationale(this, mRequestCode, mPermissionGroup);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (mRequestCode == requestCode) {
            sendResultBroadcast(true, requestCode);
            finish();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (mRequestCode == requestCode) {
                AppSettingsDialog.Builder builder = new AppSettingsDialog.Builder(this);
                builder.setPositiveButton(getString(R.string.permission_positive));
                builder.setRationale(mRationale);
                builder.setTitle(mTitle);
                builder.build().show();
            }
        }
        if (mRequestCode == requestCode) {
            sendResultBroadcast(false, requestCode);
            finish();
        }
    }

    public static void startSelf(Context context, boolean needRationale, int type, String[] permissionGroup, String title, String rationale) {
        Intent intent = new Intent(context, RequestBridgeActivity.class);
        intent.putExtra(KEY_CHECK_TYPE_CODE, type);
        intent.putExtra(KEY_CHECK_GROUP, permissionGroup);
        intent.putExtra(KEY_CHECK_RATIONALE, rationale);
        intent.putExtra(KEY_CHECK_TITLE, title);
        intent.putExtra(KEY_CHECK_NEED_RATIONALE, needRationale);
        context.startActivity(intent);
    }

    private void sendResultBroadcast(boolean isGranted, int requestCode) {
        Intent intent = new Intent(PermissionRequestBridge.PERMISSION_CHECK_ACTION);
        intent.putExtra(PermissionRequestBridge.KEY_CHECK_RESULT, isGranted);
        intent.putExtra(PermissionRequestBridge.KEY_CHECK_PERMISSION_CODE, requestCode);
        sendBroadcast(intent);
    }
}
