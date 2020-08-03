package com.seatus.Dialogs;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class AddCardDialog extends BaseOfferDialog {

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


    @BindView(R.id.card_input_widget)
    CardInputWidget cardInputWidget;


    private AddCardDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();

    }

    public AddCardDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_addcard, null, false);
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
            txtMessage.setText("Enter your card details");
            txtMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_grey));
        } else {
            txtMessage.setText(error);
            txtMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
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

        AddCardDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new AddCardDialog(context, styleRes);
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
//            dialog.txtMessage.setTextSize(StaticMethods.convertSpToPixels(16, dialog.context));
            return this;
        }


        public Builder setPositiveButton(AddCardInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> {
                Card card = dialog.cardInputWidget.getCard();
                if (card == null) {
                    dialog.setError("Invalid Card");
                } else {
                    dialog.setError(null);
                    Stripe stripe = new Stripe(dialog.context, AppConstants.STRIPE_KEY);
                    dialog.startProgress();
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    dialog.dismiss();
                                    iface.onSubmit(dialog, card, token);
                                }

                                public void onError(Exception error) {
                                    dialog.setError(error.getLocalizedMessage());
                                    dialog.stopProgress();
                                }
                            }
                    );
                }
            });
            return this;
        }

        public AddCardDialog show() {
            dialog.show();
            return dialog;
        }

    }

    public interface AddCardInterface {
        void onSubmit(AddCardDialog dialog, Card card, Token token);
    }
}
