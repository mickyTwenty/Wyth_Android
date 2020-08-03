package com.seatus.Dialogs;

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

public class MakeOfferDialog extends BaseOfferDialog {

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

    @BindView(R.id.field)
    TextInputEditText field;
    @BindView(R.id.inputlayout_field)
    TextInputLayout inputLayout;

    @BindView(R.id.txt_estimate)
    TextView txtEstimate;

    @BindView(R.id.layout_seats_return)
    LinearLayout layout_seats_returning;

    @BindView(R.id.txt_return_seats)
    TextView txtSeatsReturning;

    @BindView(R.id.btn_seat_minus_return)
    ImageView btnMinusReturning;
    @BindView(R.id.btn_seat_plus_return)
    ImageView btnPlusReturning;

    boolean isBags = true; // else seats

    private MakeOfferDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();

    }

    public MakeOfferDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_offer, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        btnNegative.setOnClickListener(view -> dismiss());

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

    @Override
    public void setError(String error) {
        inputLayout.setError(error);
    }

    @Override
    public void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
        imgHeader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void stopProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        imgHeader.setVisibility(View.VISIBLE);
    }

    private boolean areFieldsValid() {

        boolean valid = true;
        if (field.getText().toString().trim().isEmpty()) {
            setError(getContext().getString(R.string.error_validation_null));
            valid = false;
        } else if (getPrice() == null) {
            setError("Invalid Price");
            valid = false;
        } else if (!isBags && (getBagsOrSeats() == null || getBagsOrSeats() == 0) || getReturnSeats() == null | getReturnSeats() == 0) {
            setError("Please Select valid Seats before proceeding");
        } else setError(null);


        return valid;
    }

    public Float getPrice() {
        try {
            if (!field.isShown())
                return null;
            float price = Float.parseFloat(field.getText().toString().trim());
            return price;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

        MakeOfferDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new MakeOfferDialog(context, styleRes);
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

        public Builder setEnablePrice() {
            dialog.inputLayout.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setPositiveButton(OfferInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> {
                if ((dialog.inputLayout.isShown()) == dialog.areFieldsValid())
                    iface.onSubmit(dialog, dialog.getPrice(), dialog.getBagsOrSeats(), dialog.getReturnSeats());
            });
            return this;
        }

        public Builder showEstimate(String estimates_format) {
            dialog.txtEstimate.setText(estimates_format);
            return this;
        }

        public MakeOfferDialog show() {
            dialog.show();
            return dialog;
        }

        public Builder enableReturnSeats(boolean show) {
            dialog.layout_seats_returning.setVisibility(show ? View.VISIBLE : View.GONE);
            return this;
        }
    }

    public interface OfferInterface {
        void onSubmit(MakeOfferDialog dialog, Float price, Integer bagsOrSeats, Integer returnSeats);
    }
}
