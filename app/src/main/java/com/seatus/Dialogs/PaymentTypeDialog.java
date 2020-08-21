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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.seatus.R;
import com.seatus.Views.RippleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rohail on 19-Oct-17.
 */

public class PaymentTypeDialog extends Dialog {

    Context context;

    @BindView(R.id.dialog_progressbar)
    ProgressBar progressBar;
    @BindView(R.id.dialog_header)
    ImageView imgHeader;
    @BindView(R.id.txt_title)
    TextView txtTitle;
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

    @BindView(R.id.donationspinner)
    Spinner donationSpinner;

    @BindView(R.id.txt_payment_hint)
    TextView txtPaymentHint;


    String selectedPayment = "1";

    private PaymentTypeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();

    }

    public PaymentTypeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_paymentdonation_type, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        btnNegative.setOnClickListener(view -> dismiss());

        // Populate Spinner list for Donation values
        Spinner spinner = (Spinner) findViewById(R.id.donationspinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.donations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);

        // Set listener for new donation values being selected
        AdapterView.OnItemSelectedListener donationSelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,
                                       int position, long id) {
                selectedPayment = String.valueOf(id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        };
        spinner.setOnItemSelectedListener(donationSelectedListener);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
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


    public static class Builder {

        PaymentTypeDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new PaymentTypeDialog(context, styleRes);
        }

        public Builder setHeaderImg(@DrawableRes int headerRes) {
            dialog.imgHeader.setImageResource(headerRes);
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

        public Builder setPositiveButton(PaymentTypeInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> iface.onAccept(dialog, dialog.selectedPayment));
            return this;
        }

        public Builder setNegativeButton(View.OnClickListener iface) {
            dialog.btnNegative.setOnClickListener(v -> {
                dialog.dismiss();
                if (iface != null)
                    iface.onClick(v);
            });
            return this;
        }


        public PaymentTypeDialog show() {
            dialog.show();
            return dialog;
        }

    }

    public interface PaymentTypeInterface {
        void onAccept(PaymentTypeDialog dialog, String paymentType);
    }
}
