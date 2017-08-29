package com.zhangwx.permission;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;

/**
 * Configuration for either {@link RationaleDialogFragment} or {@link RationaleDialogFragmentCompat}.
 */
class RationaleDialogConfig {

    private static final String KEY_POSITIVE_BUTTON = "positiveButton";
    private static final String KEY_NEGATIVE_BUTTON = "negativeButton";
    private static final String KEY_RATIONALE_ICON = "icon";
    private static final String KEY_RATIONALE_TITLE = "title";
    private static final String KEY_RATIONALE_MESSAGE = "rationaleMsg";
    private static final String KEY_REQUEST_CODE = "requestCode";
    private static final String KEY_PERMISSIONS = "permissions";

    int positiveButton;
    int negativeButton;
    int icon = -1;
    int requestCode;
    String rationaleMsg;
    String title;
    String[] permissions;

    RationaleDialogConfig(@StringRes int positiveButton, @StringRes int negativeButton, @DrawableRes int icon,
                          String title, @NonNull String rationaleMsg, int requestCode,
                          @NonNull String[] permissions) {

        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.icon = icon;
        this.title = title;
        this.rationaleMsg = rationaleMsg;
        this.requestCode = requestCode;
        this.permissions = permissions;
    }

    RationaleDialogConfig(Bundle bundle) {
        positiveButton = bundle.getInt(KEY_POSITIVE_BUTTON);
        negativeButton = bundle.getInt(KEY_NEGATIVE_BUTTON);
        icon = bundle.getInt(KEY_RATIONALE_ICON);
        title = bundle.getString(KEY_RATIONALE_TITLE);
        rationaleMsg = bundle.getString(KEY_RATIONALE_MESSAGE);
        requestCode = bundle.getInt(KEY_REQUEST_CODE);
        permissions = bundle.getStringArray(KEY_PERMISSIONS);
    }

    Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITIVE_BUTTON, positiveButton);
        bundle.putInt(KEY_NEGATIVE_BUTTON, negativeButton);
        bundle.putString(KEY_RATIONALE_MESSAGE, rationaleMsg);
        bundle.putString(KEY_RATIONALE_TITLE, title);
        bundle.putInt(KEY_RATIONALE_ICON, icon);
        bundle.putInt(KEY_REQUEST_CODE, requestCode);
        bundle.putStringArray(KEY_PERMISSIONS, permissions);

        return bundle;
    }

    AlertDialog createDialog(Context context, Dialog.OnClickListener listener) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);

        builder.setCancelable(false)
                .setPositiveButton(positiveButton, listener)
//                .setNegativeButton(negativeButton, listener)
                .setMessage(rationaleMsg);
        if (icon != -1) {
            builder.setIcon(icon);
        }
        if (!TextUtils.isEmpty(title)) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(title);
            final StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            final AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(17, true);
            stringBuilder.setSpan(boldSpan, 0, stringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            stringBuilder.setSpan(sizeSpan, 0, stringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            builder.setTitle(stringBuilder);
        }
        return builder.create();
    }

}
