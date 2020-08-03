package com.seatus.Dialogs;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.seatus.Interfaces.RateInterface;
import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Views.RippleView;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rohail on 19-Oct-17.
 */

public class RateDialog extends BaseOfferDialog {

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

    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.txt_name)
    TextView txtName;

    @BindView(R.id.layout_name)
    LinearLayout layoutName;
    @BindView(R.id.layout_date)
    LinearLayout layoutDate;

    @BindView(R.id.ratings)
    RatingBar ratingBar;
    @BindView(R.id.field_comment)
    EditText fieldComment;


    private RateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();

    }

    public RateDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_rate, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        btnNegative.setOnClickListener(view -> dismiss());

    }

    @Override
    public void setError(String error) {
        if (error == null) {
            txtMessage.setVisibility(View.GONE);
        } else {
            txtMessage.setText(error);
            txtMessage.setVisibility(View.VISIBLE);
        }
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


    public static class Builder {

        RateDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new RateDialog(context, styleRes);
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
            dialog.txtMessage.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setUserName(String userName) {
            dialog.txtName.setText(userName);
            dialog.layoutName.setVisibility(View.VISIBLE);
            return this;
        }
        public Builder setDate(String date) {
            dialog.txtDate.setText(date);
            dialog.layoutDate.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setMessageBold() {
            dialog.txtMessage.setTypeface(dialog.txtMessage.getTypeface(), Typeface.BOLD_ITALIC);
//            dialog.txtMessage.setTextSize(StaticMethods.convertSpToPixels(16, dialog.context));
            return this;
        }


        public Builder setPositiveButton(RateInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> {

                if (dialog.ratingBar.getRating() == 0) {
                    dialog.setError("Please rate the passenger");
                    return;
                }
                iface.onRate(dialog.ratingBar.getRating(), dialog.fieldComment.getText().toString(), dialog);
                dialog.dismiss();
            });
            return this;
        }

        public RateDialog show() {
            dialog.show();
            return dialog;
        }

    }
}
