package com.seatus.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.RippleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rohail on 19-Oct-17.
 */

public class InviteItineraryDialog extends Dialog {

    Context context;

    @BindView(R.id.dialog_progressbar)
    ProgressBar progressBar;
    @BindView(R.id.dialog_header)
    ImageView imgHeader;

    @BindView(R.id.btn_negative)
    RippleView btnNegative;
    @BindView(R.id.btn_positive)
    RippleView btnPositive;

    @BindView(R.id.inputlayout_firstname)
    TextInputLayout inputLayoutFirstName;
    @BindView(R.id.inputlayout_lastname)
    TextInputLayout inputLayoutLastName;
    @BindView(R.id.inputlayout_email)
    TextInputLayout inputLayoutEmail;
    @BindView(R.id.inputlayout_phone)
    TextInputLayout inputLayoutPhone;


    @BindView(R.id.txt_negative)
    TextView txtNegative;
    @BindView(R.id.txt_positive)
    TextView txtPositive;

    @BindView(R.id.field_firstname)
    TextInputEditText fieldFirstName;
    @BindView(R.id.field_lastname)
    TextInputEditText fieldLastName;
    @BindView(R.id.field_email)
    TextInputEditText fieldEmail;
    @BindView(R.id.field_phone)
    TextInputEditText fieldPhone;


    private InviteItineraryDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initUi();
    }

    public InviteItineraryDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_share_thirdparty, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        btnNegative.setOnClickListener(view -> dismiss());
        fieldPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }


    public void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
        imgHeader.setVisibility(View.INVISIBLE);
    }

    public void stopProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        imgHeader.setVisibility(View.VISIBLE);
    }

    public void onSubmitClick() {

    }

    private boolean areFieldsValid() {

        boolean valid = true;

        String firstName = fieldFirstName.getText().toString();
        String lastName = fieldLastName.getText().toString();
        String email = fieldEmail.getText().toString();
        String phone = fieldPhone.getText().toString();

        if (firstName.length() < 1) {
            inputLayoutFirstName.setError(getContext().getString(R.string.error_validation_null));
            valid = false;
        } else inputLayoutFirstName.setError(null);

        if (lastName.length() < 1) {
            inputLayoutLastName.setError(getContext().getString(R.string.error_validation_null));
            valid = false;
        } else inputLayoutLastName.setError(null);

        if (!StaticMethods.isValidEmail(email)) {
            inputLayoutEmail.setError(getContext().getString(R.string.error_validation_email));
            valid = false;
        } else inputLayoutEmail.setError(null);

        if (phone.length() < 4) {
            inputLayoutPhone.setError(getContext().getString(R.string.error_validation_null));
            valid = false;
        } else inputLayoutPhone.setError(null);


        return valid;
    }

    private UserItem getAddedItem() {

        String firstName = fieldFirstName.getText().toString();
        String lastName = fieldLastName.getText().toString();
        String email = fieldEmail.getText().toString();
        String phone = fieldPhone.getText().toString();

        return UserItem.newThirdPartyItem(firstName, lastName, email, phone);

    }

    public static class Builder {

        InviteItineraryDialog dialog;
        boolean isFieldShown;

        public Builder(Context context, @StyleRes int styleRes) {
            dialog = new InviteItineraryDialog(context, styleRes);
        }

        public Builder setPositiveButton(UserAddedInterface iface) {
            dialog.btnPositive.setOnClickListener(view -> {
                if (dialog.areFieldsValid()) {
                    dialog.dismiss();
                    iface.onAdded(dialog, dialog.getAddedItem());
                }
            });
            return this;
        }

        public InviteItineraryDialog show() {
            dialog.show();
            return dialog;
        }

    }

    public interface UserAddedInterface {
        void onAdded(InviteItineraryDialog dialog, UserItem user);
    }
}
