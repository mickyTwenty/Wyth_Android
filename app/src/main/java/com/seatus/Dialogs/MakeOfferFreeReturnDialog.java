package com.seatus.Dialogs;

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

public class MakeOfferFreeReturnDialog extends BaseOfferDialog {

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

    @BindView(R.id.layout_bags)
    LinearLayout layout_bags;

    @BindView(R.id.txt_bags)
    TextView txtBagsOrSeats;

    @BindView(R.id.txt_bags_hint)
    TextView txtHintBags;

    @BindView(R.id.btn_seat_minus)
    ImageView btnMinus;
    @BindView(R.id.btn_seat_plus)
    ImageView btnPlus;

    @BindView(R.id.layout_seats_return)
    LinearLayout layout_seats_returning;

    @BindView(R.id.txt_return_seats)
    TextView txtSeatsReturning;

    @BindView(R.id.btn_seat_minus_return)
    ImageView btnMinusReturning;
    @BindView(R.id.btn_seat_plus_return)
    ImageView btnPlusReturning;

    @BindView(R.id.icon_offer_price)
    ImageView iconOfferPrice;
    @BindView(R.id.txt_offer_price)
    TextView txtOfferPrice;

    boolean isBags = true; // else seats

    private MakeOfferFreeReturnDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();

    }

    public MakeOfferFreeReturnDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_offer_local, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        btnNegative.setOnClickListener(view -> dismiss());

        txtOfferPrice.setText("Your Cost: $0.00");
        txtNegative.setText("Cancel");
        txtPositive.setText("Join Trip");

        btnPlus.setOnClickListener(v -> {
            int bags = Integer.parseInt(txtBagsOrSeats.getText().toString());
            if (bags < 9)
                bags++;
            txtBagsOrSeats.setText(String.valueOf(bags));
        });

        btnMinus.setOnClickListener(v -> {
            int bags = Integer.parseInt(txtBagsOrSeats.getText().toString());
            if (isBags) {
                if (bags > 0)
                    bags--;
            } else {
                if (bags > 1)
                    bags--;
            }
            txtBagsOrSeats.setText(String.valueOf(bags));
        });


        btnPlusReturning.setOnClickListener(v -> {
            int bags = Integer.parseInt(txtSeatsReturning.getText().toString());
            if (bags < 9)
                bags++;
            txtSeatsReturning.setText(String.valueOf(bags));
        });

        btnMinusReturning.setOnClickListener(v -> {
            int bags = Integer.parseInt(txtSeatsReturning.getText().toString());
            {
                if (bags > 1)
                    bags--;
            }
            txtSeatsReturning.setText(String.valueOf(bags));
        });
    }

    public void setError(String error) {
        setError(error);
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
        if (!isBags && (getBagsOrSeats() == null || getBagsOrSeats() == 0) || getReturnSeats() == null | getReturnSeats() == 0) {
            setError("Please Select valid Seats before proceeding");
        } else setError(null);


        return valid;
    }

    public Float getPrice() {
        return 0.00f;
    }

    public Integer getBagsOrSeats() {
        try {
            int bags = Integer.parseInt(txtBagsOrSeats.getText().toString().trim());
            return bags;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer getReturnSeats() {
        try {
            int bags = Integer.parseInt(txtSeatsReturning.getText().toString().trim());
            return bags;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Builder {

        MakeOfferFreeReturnDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new MakeOfferFreeReturnDialog(context, styleRes);
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

        public Builder setEnableBagsElseSeats(boolean enable) {
            if (enable) {
                dialog.isBags = true;
                dialog.txtHintBags.setText("No of Bags");
            } else {
                dialog.isBags = false;
                dialog.txtHintBags.setText("No of Seats");
            }
            return this;
        }

        public Builder setMessageBold() {
            dialog.txtMessage.setTypeface(dialog.txtMessage.getTypeface(), Typeface.BOLD_ITALIC);
//            dialog.txtMessage.setTextSize(StaticMethods.convertSpToPixels(16, dialog.context));
            return this;
        }

        public Builder setPositiveButton(OfferInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> {
                iface.onSubmit(dialog, dialog.getPrice(), dialog.getBagsOrSeats(), dialog.getReturnSeats());
            });
            return this;
        }

        public MakeOfferFreeReturnDialog show() {
            dialog.show();
            return dialog;
        }

        public Builder enableReturnSeats(boolean show) {
            dialog.layout_seats_returning.setVisibility(show ? View.VISIBLE : View.GONE);
            return this;
        }
    }

    public interface OfferInterface {
        void onSubmit(MakeOfferFreeReturnDialog dialog, Float price, Integer bagsOrSeats, Integer returnSeats);
    }
}
