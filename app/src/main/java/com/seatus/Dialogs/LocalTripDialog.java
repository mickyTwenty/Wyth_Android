package com.seatus.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.seatus.R;
import com.seatus.Views.RippleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rohail on 19-Oct-17.
 */

public class LocalTripDialog extends Dialog {

    Context context;

    @BindView(R.id.dialog_message)
    TextView txtMessage;

    @BindView(R.id.btn_positive)
    RippleView btnPositive;
    @BindView(R.id.txt_positive)
    TextView txtPositive;


    private LocalTripDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();

    }

    public LocalTripDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_local_trip, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        btnPositive.setOnClickListener(view -> dismiss());

    }

    public void setError(String error) {
    }

    public static class Builder {

        LocalTripDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new LocalTripDialog(context, styleRes);
        }

        public Builder setMessage(@StringRes int msgRes) {
            dialog.txtMessage.setText(msgRes);
            return this;
        }

        public Builder setMessage(String msgRes) {
            dialog.txtMessage.setText(msgRes);
            return this;
        }


        public Builder setMessageBold() {
            dialog.txtMessage.setTypeface(dialog.txtMessage.getTypeface(), Typeface.BOLD_ITALIC);
            return this;
        }

        public Builder setPositiveButton(View.OnClickListener iface) {
            dialog.btnPositive.setOnClickListener(v -> {
                dialog.dismiss();
                if (iface != null)
                    iface.onClick(v);
            });
            return this;
        }

        public LocalTripDialog show() {
            dialog.show();
            return dialog;
        }

    }
}
