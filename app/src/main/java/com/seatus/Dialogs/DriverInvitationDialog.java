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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seatus.R;
import com.seatus.Views.RippleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rohail on 19-Oct-17.
 */

public class DriverInvitationDialog extends Dialog {

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

    @BindView(R.id.txt_seats_return)
    TextView txtSeatsReturn;

    @BindView(R.id.btn_seat_return_minus)
    ImageView btnMinusReturn;
    @BindView(R.id.btn_seat_return_plus)
    ImageView btnPlusReturn;

    @BindView(R.id.layout_returnseats)
    LinearLayout layoutReturnSeats;


    private DriverInvitationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();

    }

    public DriverInvitationDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_driver_invite, null, false);
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


        btnPlusReturn.setOnClickListener(v -> {
            int bags = Integer.parseInt(txtSeatsReturn.getText().toString());
            if (bags < 9)
                bags++;
            txtBags.setText(String.valueOf(bags));
        });

        btnMinusReturn.setOnClickListener(v -> {
            int seats = Integer.parseInt(txtSeatsReturn.getText().toString());
            if (seats > 1)
                seats--;
            txtBags.setText(String.valueOf(seats));
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

    public int getReturnSeats() {
        try {
            int bags = Integer.parseInt(txtSeatsReturn.getText().toString().trim());
            return bags;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static class Builder {

        DriverInvitationDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new DriverInvitationDialog(context, styleRes);
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


        public Builder setPositiveButton(DriverInvitationInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> iface.onAccept(dialog, dialog.getSeats(), dialog.getReturnSeats()));
            return this;
        }

        public Builder setNegativeButton(View.OnClickListener iface) {
            dialog.btnNegative.setOnClickListener(v -> {
                dialog.dismiss();
                iface.onClick(v);
            });
            return this;
        }

        public DriverInvitationDialog show() {
            dialog.show();
            return dialog;
        }

        public Builder showReturnSeats(boolean isFieldShown) {
            dialog.layoutReturnSeats.setVisibility(isFieldShown ? View.VISIBLE : View.GONE);
            return this;
        }
    }

    public interface DriverInvitationInterface {
        void onAccept(DriverInvitationDialog dialog, int seats, int returnSeats);
    }
}
