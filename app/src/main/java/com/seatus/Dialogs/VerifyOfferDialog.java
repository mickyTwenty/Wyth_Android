package com.seatus.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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

/**
 * Created by rohail on 19-Oct-17.
 */

public class VerifyOfferDialog extends Dialog {

    public Context context;

    @BindView(R.id.dialog_progressbar)
    ProgressBar progressBar;
    @BindView(R.id.dialog_header)
    ImageView imgHeader;
    @BindView(R.id.dialog_title)
    ImageView imgTitle;
    @BindView(R.id.btn_apply)
    ImageView btnApply;
    @BindView(R.id.dialog_message)
    TextView txtMessage;
    @BindView(R.id.dialog_cost)
    TextView txtCost;
    @BindView(R.id.field)
    TextInputEditText field;
    @BindView(R.id.inputlayout_field)
    TextInputLayout inputLayout;
    @BindView(R.id.btn_negative)
    RippleView btnNegative;
    @BindView(R.id.btn_positive)
    RippleView btnPositive;
    @BindView(R.id.txt_negative)
    TextView txtNegative;
    @BindView(R.id.txt_positive)
    TextView txtPositive;


    private VerifyOfferDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.BounceDialog);
        this.context = context;
        initUi();
    }

    public VerifyOfferDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_verify_offer, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        btnNegative.setOnClickListener(view -> dismiss());
    }

    public TextInputEditText getField() {
        return field;
    }

    public TextView getCostField() {
        return txtCost;
    }

    public void setError(String error) {
        inputLayout.setError(error);
    }

    public void startProgress() {
        btnPositive.setEnabled(false);
        btnNegative.setEnabled(false);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        progressBar.setVisibility(View.VISIBLE);
        imgHeader.setVisibility(View.INVISIBLE);
    }

    public void stopProgress() {
        btnPositive.setEnabled(true);
        btnNegative.setEnabled(true);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        progressBar.setVisibility(View.INVISIBLE);
        imgHeader.setVisibility(View.VISIBLE);
    }

    public static class Builder {

        VerifyOfferDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new VerifyOfferDialog(context, styleRes);
        }

        public Builder setHeaderImg(@DrawableRes int headerRes) {
            dialog.imgHeader.setImageResource(headerRes);
            return this;
        }

        public Builder setTitleImg(@DrawableRes int titleRes) {
            dialog.imgTitle.setImageResource(titleRes);
            return this;
        }
        public Builder hideTitleImg() {
            dialog.imgTitle.setVisibility(View.GONE);
            return this;
        }

        public Builder setMessage(@StringRes int msgRes) {
            dialog.txtMessage.setText(msgRes);
            return this;
        }

        public Builder setMessage(String msgRes) {
            dialog.txtMessage.setText(msgRes);
            return this;
        }

        public Builder setFieldDrawable(@DrawableRes int drawableRes) {
            isFieldShown = true;
            dialog.field.setCompoundDrawablesWithIntrinsicBounds(drawableRes, 0, 0, 0);
            return this;
        }

        public Builder setFieldInputType(int inputType) {
            dialog.field.setInputType(inputType);
            isFieldShown = true;
            return this;
        }



        public Builder setPositiveButton(String str, SeatUsDialogInterface iface) {
            dialog.txtPositive.setText(str);
            dialog.btnPositive.setOnClickListener(view -> {
                iface.onSubmit(dialog);
            });
            return this;
        }

        public Builder setApplyButton(SeatUsDialogInterface iface) {
            dialog.btnApply.setOnClickListener(view -> {
                iface.onSubmit(dialog);
            });
            return this;
        }

        public Builder setNegativeButton(String str) {
            dialog.txtNegative.setText(str);
            return this;
        }

        public Builder setNegativeButton(String str, SeatUsDialogInterface iface) {
            dialog.txtNegative.setText(str);
            dialog.btnNegative.setOnClickListener(v -> {
                iface.onSubmit(dialog);
            });
            return this;
        }

        public Builder setFieldHint(String hint) {
            dialog.inputLayout.setHint(hint);
            isFieldShown = true;
            return this;
        }

        public Builder setMessageBold() {
            dialog.txtMessage.setTypeface(dialog.txtMessage.getTypeface(), Typeface.BOLD_ITALIC);
//            dialog.txtMessage.setTextSize(StaticMethods.convertSpToPixels(16, dialog.context));
            return this;
        }

        public VerifyOfferDialog show() {
            if (!isFieldShown)
                dialog.inputLayout.setVisibility(View.GONE);
            dialog.show();
            return dialog;
        }

    }

    public interface SeatUsDialogInterface {
        void onSubmit(VerifyOfferDialog dialog);
    }
}