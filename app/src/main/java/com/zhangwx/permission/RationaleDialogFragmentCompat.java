package com.zhangwx.permission;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * {@link DialogFragment} to display rationale for permission requests when the request
 * comes from a Fragment or Activity that can host a Fragment.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class RationaleDialogFragmentCompat extends DialogFragment {

    private EasyPermissions.PermissionCallbacks mPermissionCallbacks;

    static RationaleDialogFragmentCompat newInstance(
            @StringRes int positiveButton, @StringRes int negativeButton, @DrawableRes int icon,
            String title, @NonNull String rationaleMsg, int requestCode, @NonNull String[] permissions) {

        // Create new Fragment
        RationaleDialogFragmentCompat dialogFragment = new RationaleDialogFragmentCompat();

        // Initialize configuration as arguments
        RationaleDialogConfig config = new RationaleDialogConfig(
                positiveButton, negativeButton, icon, title, rationaleMsg, requestCode, permissions);
        dialogFragment.setArguments(config.toBundle());

        return dialogFragment;
    }

    @Override
    public void onStart() {
        try {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            window.setAttributes(windowParams);
        } catch (Exception e) {
        }
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null && getParentFragment() instanceof EasyPermissions.PermissionCallbacks) {
            mPermissionCallbacks = (EasyPermissions.PermissionCallbacks) getParentFragment();
        } else if (context instanceof EasyPermissions.PermissionCallbacks) {
            mPermissionCallbacks = (EasyPermissions.PermissionCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPermissionCallbacks = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Rationale dialog should not be cancelable
        setCancelable(false);

        // Get config from arguments, create click listener
        RationaleDialogConfig config = new RationaleDialogConfig(getArguments());
        RationaleDialogClickListener clickListener =
                new RationaleDialogClickListener(this, config, mPermissionCallbacks);

        // Create an AlertDialog
        return config.createDialog(getContext(), clickListener);
    }
}
