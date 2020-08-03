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

public class BookNowDialog extends BaseOfferDialog {

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
    TextView txtBags;
    @BindView(R.id.txt_estimate)
    TextView txtEstimate;

    @BindView(R.id.btn_seat_minus)
    ImageView btnMinus;
    @BindView(R.id.btn_seat_plus)
    ImageView btnPlus;

    @BindView(R.id.field)
    TextInputEditText field;
    @BindView(R.id.inputlayout_field)
    TextInputLayout inputLayout;


    private BookNowDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();

    }

    public BookNowDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_booknow, null, false);
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
            if (bags > 0)
                bags--;
            txtBags.setText(String.valueOf(bags));
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

    public Integer getBags() {
        try {
            int bags = Integer.parseInt(txtBags.getText().toString().trim());
            return bags;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Builder {

        BookNowDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new BookNowDialog(context, styleRes);
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

        public Builder setEnableBags(boolean enable) {
            if (enable)
                dialog.layout_bags.setVisibility(View.VISIBLE);
            else
                dialog.layout_bags.setVisibility(View.GONE);
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

        public Builder setPositiveButton(BookNowInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> {
                iface.onSubmit(dialog, dialog.field.getText().toString(), dialog.getBags());
            });
            return this;
        }

        public Builder showEstimate(String estimates_format) {
            dialog.txtEstimate.setText(estimates_format);
            return this;
        }

        public BookNowDialog show() {
            dialog.show();
            return dialog;
        }

    }

    public interface BookNowInterface {
        void onSubmit(BookNowDialog dialog, String promocode, Integer bags);
    }
}
