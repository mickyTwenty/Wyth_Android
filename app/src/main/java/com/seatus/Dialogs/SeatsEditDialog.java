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
import android.widget.TextView;

import com.seatus.R;
import com.seatus.Views.RippleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rohail on 19-Oct-17.
 */

public class SeatsEditDialog extends Dialog {

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
    @BindView(R.id.txt_negative)
    TextView txtNegative;
    @BindView(R.id.txt_positive)
    TextView txtPositive;

    @BindView(R.id.txt_bags)
    TextView txtBags;

    @BindView(R.id.btn_seat_minus)
    ImageView btnMinus;
    @BindView(R.id.btn_seat_plus)
    ImageView btnPlus;


    private SeatsEditDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();

    }

    public SeatsEditDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_edit_seats, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        btnNegative.setOnClickListener(view -> dismiss());

        btnPlus.setOnClickListener(v -> {
            int bags = Integer.parseInt(txtBags.getText().toString());
            if (bags < 9)
                bags++;
            txtBags.setText(String.valueOf(bags));
        });

        btnMinus.setOnClickListener(v -> {
            int bags = Integer.parseInt(txtBags.getText().toString());
            if (bags > 1)
                bags--;
            txtBags.setText(String.valueOf(bags));
        });

    }

    public void setError(String error) {
    }

    public void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
        imgHeader.setVisibility(View.INVISIBLE);
    }

    public void stopProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        imgHeader.setVisibility(View.VISIBLE);
    }

    private boolean areFieldsValid() {

        boolean valid = true;

        return valid;
    }


    public int getSeats() {
        try {
            int bags = Integer.parseInt(txtBags.getText().toString().trim());
            return bags;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static class Builder {

        SeatsEditDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new SeatsEditDialog(context, styleRes);
        }

        public Builder setHeaderImg(@DrawableRes int headerRes) {
            dialog.imgHeader.setImageResource(headerRes);
            return this;
        }

        public Builder setTitleImg(@DrawableRes int titleRes) {
            dialog.imgTitle.setImageResource(titleRes);
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


        public Builder setMessageBold() {
            dialog.txtMessage.setTypeface(dialog.txtMessage.getTypeface(), Typeface.BOLD_ITALIC);
            return this;
        }


        public Builder setPositiveButton(EditSeatsInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> iface.onAccept(dialog, dialog.getSeats()));
            return this;
        }

        public Builder setNegativeButton(View.OnClickListener iface) {
            dialog.btnNegative.setOnClickListener(v -> {
                dialog.dismiss();
                iface.onClick(v);
            });
            return this;
        }

        public Builder setInitialSeats(int seats) {
            dialog.txtBags.setText(seats + "");
            return this;
        }

        public SeatsEditDialog show() {
            dialog.show();
            return dialog;
        }

    }

    public interface EditSeatsInterface {
        void onAccept(SeatsEditDialog dialog, int seats);
    }
}
