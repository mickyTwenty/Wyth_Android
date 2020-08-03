package com.seatus.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seatus.R;
import com.seatus.Views.RippleView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.seatus.Utils.AppConstants.FIELD_MINIMUM_LENGTH;

/**
 * Created by rohail on 19-Oct-17.
 */

public class ChangePasswordDialog extends Dialog {

    Context context;

    @BindView(R.id.dialog_progressbar)
    ProgressBar progressBar;
    @BindView(R.id.dialog_header)
    ImageView imgHeader;
    @BindView(R.id.dialog_title)
    ImageView imgTitle;
    @BindView(R.id.dialog_message)
    TextView txtMessage;

    @BindView(R.id.btn_negative)
    RippleView btnNegative;
    @BindView(R.id.btn_positive)
    RippleView btnPositive;

    @BindView(R.id.inputlayout_oldpass)
    TextInputLayout inputLayoutOldPass;
    @BindView(R.id.inputlayout_newpass)
    TextInputLayout inputLayoutNewPass;
    @BindView(R.id.inputlayout_newpass2)
    TextInputLayout inputLayoutNewPass2;

    @BindView(R.id.txt_negative)
    TextView txtNegative;
    @BindView(R.id.txt_positive)
    TextView txtPositive;

    @BindView(R.id.field_oldpass)
    TextInputEditText fieldOldPass;
    @BindView(R.id.field_newpass)
    TextInputEditText fieldNewPass;
    @BindView(R.id.field_newpass2)
    TextInputEditText fieldNewPass2;


    private ChangePasswordDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();
    }

    public ChangePasswordDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_changepass, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        btnNegative.setOnClickListener(view -> dismiss());
    }

    public void setError(String error) {
        inputLayoutOldPass.setError(error);
    }

    public void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
        imgHeader.setVisibility(View.INVISIBLE);
    }

    public void stopProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        imgHeader.setVisibility(View.VISIBLE);
    }

    public void onSubmitClick() {

    }

    private boolean areFieldsValid() {

        String oldpass = fieldOldPass.getText().toString().trim();
        String newpass = fieldNewPass.getText().toString().trim();
        String newpass2 = fieldNewPass2.getText().toString().trim();

        boolean valid = true;

        if (oldpass.length() < FIELD_MINIMUM_LENGTH) {
            inputLayoutOldPass.setError(context.getString(R.string.error_validation_empty));
            valid = false;
        } else inputLayoutOldPass.setError(null);

        if (newpass.length() < FIELD_MINIMUM_LENGTH) {
            inputLayoutNewPass.setError(context.getString(R.string.error_validation_empty));
            valid = false;
            return valid;
        } else inputLayoutNewPass.setError(null);

        if (!newpass.equals(newpass2)) {
            inputLayoutNewPass2.setError(context.getString(R.string.error_validation_donotmatch));
            valid = false;
        } else inputLayoutNewPass2.setError(null);

        return valid;
    }

    public static class Builder {

        ChangePasswordDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new ChangePasswordDialog(context, styleRes);
        }

        public Builder setPositiveButton(ChangePassInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> {
                if (dialog.areFieldsValid())
                    iface.onSubmit(dialog, dialog.fieldOldPass.getText().toString().trim(), dialog.fieldNewPass.getText().toString().trim());
            });
            return this;
        }

        public ChangePasswordDialog show() {
            dialog.show();
            return dialog;
        }

    }

    public interface ChangePassInterface {
        void onSubmit(ChangePasswordDialog dialog, String oldPass, String newPass);
    }
}
