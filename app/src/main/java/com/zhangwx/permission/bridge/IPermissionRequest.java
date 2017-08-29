package com.zhangwx.permission.bridge;

import com.zhangwx.permission.bridge.PermissionRequestBridge;

/**
 * Created by zhangwx on 2017/4/5.
 */

public interface IPermissionRequest {
    void request(boolean needRationale,
                 int requestCode,
                 String[] permissionGroup,
                 String title,
                 String rationale,
                 PermissionRequestBridge.RequestCallBack callBack);
}
