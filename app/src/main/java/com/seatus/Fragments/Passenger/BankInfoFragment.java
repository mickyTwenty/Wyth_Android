package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Holders.BankItem;
import com.seatus.Models.CityItem;
import com.seatus.Models.StateItem;
import com.seatus.R;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.EncryptionHelper;
import com.seatus.Utils.Help;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.database.AppDatabase;
import com.seatus.Views.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by saqib on 1/25/2018.
 */

public class BankInfoFragment extends BaseFragment {
    @BindView(R.id.field_bank_name)
    TextInputEditText fieldBankName;
    @BindView(R.id.inputlayout_bank_name)
    TextInputLayout inputlayoutBankName;

    @BindView(R.id.field_account_name)
    TextInputEditText fieldAccountName;
    @BindView(R.id.inputlayout_account_name)
    TextInputLayout inputlayoutAccountName;

    @BindView(R.id.field_account_no)
    TextInputEditText fieldAccountNo;
    @BindView(R.id.inputlayout_account_no)
    TextInputLayout inputlayoutAccountNo;

    @BindView(R.id.field_bank_routing)
    TextInputEditText fieldBankRouting;
    @BindView(R.id.inputlayout_bank_routing)
    TextInputLayout inputlayoutBankRouting;

//    @BindView(R.id.field_paymnet_type)
//    TextInputEditText fieldPaymentType;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

//    @BindView(R.id.txt_paymenttype_hint)
//    TextView txtPaymentTypeHint;


    @BindView(R.id.field_personal_id)
    TextInputEditText fieldPersonalId;
    @BindView(R.id.inputlayout_personal_id)
    TextInputLayout inputlayoutPersonalId;
    @BindView(R.id.field_ssn)
    TextInputEditText fieldSsn;
    @BindView(R.id.inputlayout_ssn)
    TextInputLayout inputlayoutSsn;
    @BindView(R.id.field_address)
    TextInputEditText fieldAddress;
    @BindView(R.id.inputlayout_address)
    TextInputLayout inputlayoutAddress;
    @BindView(R.id.field_state)
    TextInputEditText fieldState;
    @BindView(R.id.inputlayout_state)
    TextInputLayout inputlayoutState;
    @BindView(R.id.field_city)
    TextInputEditText fieldCity;
    @BindView(R.id.inputlayout_city)
    TextInputLayout inputlayoutCity;
    @BindView(R.id.field_postal_code)
    TextInputEditText fieldPostalCode;
    @BindView(R.id.inputlayout_postal_code)
    TextInputLayout inputlayoutPostalCode;
    @BindView(R.id.field_dob)
    TextInputEditText fieldDob;
    @BindView(R.id.inputlayout_dob)
    TextInputLayout inputlayoutDob;


    StateItem selectedState;
    CityItem selectedCity;


    public boolean isRedirectable = false;

    public static BankInfoFragment newInstance(boolean isRedirectable) {
        BankInfoFragment fragment = new BankInfoFragment();
        fragment.isRedirectable = isRedirectable;
        return fragment;
    }


    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_bank_info;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        if (isRedirectable)
            titleBar.setTitle("Add BankInfo").enableBack();
        else
            titleBar.enableInfo(Help.Payments);
    }

    @Override
    public void inits() {
        getBankDetail();
        refreshLayout.setOnRefreshListener(() -> getBankDetail());

        getUserLiveData().observe(this, userItem -> {
            fieldDob.setText(userItem.birth_date);
            fieldState.setText(userItem.state_text);
            fieldCity.setText(userItem.city_text);

            selectedCity = new CityItem(userItem.city, userItem.city_text);
            selectedState = new StateItem(userItem.state, userItem.state_text);
        });
    }

    @Override
    public void setEvents() {

//        fieldPaymentType.setOnClickListener(view -> DialogHelper.showPaymentTypeDialog(getContext(), item -> {
//            fieldPaymentType.setText(item);
//            setPaymentHint(item);
//        }));
    }


    @OnClick({R.id.field_dob, R.id.field_state, R.id.field_city, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                actionSaveChanges();
                break;
            case R.id.field_dob:
                StaticMethods.datePopup(getContext(), fieldDob, true, false, 2000);
                break;
            case R.id.field_state:
                try {
                    DialogHelper.showLocationPickerDialog(getContext(), AppDatabase.getInstance(getContext()).stateDoa().getAll(), item -> {
                        try {
                            selectedState = (StateItem) item;
                            fieldState.setText(selectedState.name);
                            if (selectedCity != null && selectedCity.state_id != null && !selectedCity.state_id.equals(selectedState.id)) {
                                selectedCity = null;
                                fieldCity.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.field_city:
                try {
                    if (selectedState == null)
                        inputlayoutState.setError(getString(R.string.error_validation_select));
                    else {
                        inputlayoutState.setError(null);
                        DialogHelper.showLocationPickerDialog(getContext(), AppDatabase.getInstance(getContext()).cityDoa().getAllCityByStateID(selectedState.id), item -> {
                            try {
                                selectedCity = (CityItem) item;
                                fieldCity.setText(selectedCity.name);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setFields(BankItem bankItem) {
      //  fieldBankName.setText(bankItem.bank_name);
        fieldAccountName.setText(bankItem.account_title);
        fieldBankRouting.setText(bankItem.routing_number);
        fieldAccountNo.setText(bankItem.account_number);

        fieldPersonalId.setText(bankItem.personal_id_number);
        fieldSsn.setText(bankItem.ssn_last_4);
        fieldAddress.setText(bankItem.address);

        fieldPostalCode.setText(bankItem.postal_code);

//        if (!TextUtils.isEmpty(bankItem.period))
//            fieldPaymentType.setText(new StringBuilder().append(Character.toUpperCase(bankItem.period.charAt(0))).append(bankItem.period.substring(1)).toString());
//        else
//            fieldPaymentType.setText("Standard");
//        setPaymentHint(bankItem.period);


    }

//    private void setPaymentHint(String period) {
//
//        try {
//            switch (period.toLowerCase()) {
//                case "expedited":
//                    txtPaymentTypeHint.setText(R.string.payment_hint_daily);
//                    break;
//                case "standard":
//                    txtPaymentTypeHint.setText(R.string.payment_hint_standard);
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            txtPaymentTypeHint.setText(R.string.payment_hint_standard);
//        }
//    }

    private void getBankDetail() {

        getActivityViewModel().getBankDetail().observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    refreshLayout.setRefreshing(false);
                    hideLoader();
                    if (webResponseResource.data.body != null) {
                        try {
                            BankItem bankItem = getGson().fromJson(EncryptionHelper.decryptAES(webResponseResource.data.body), BankItem.class);
                            setFields(bankItem);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        makeSnackbar(webResponseResource.data.message);
                    break;
                default:
                    refreshLayout.setRefreshing(false);
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    private void actionSaveChanges() {
        String bank_name = fieldBankName.getText().toString().trim();
        String account_title = fieldAccountName.getText().toString().trim();
        String routing_number = fieldBankRouting.getText().toString().trim();
        String account_number = fieldAccountNo.getText().toString().trim();

        //String personal_id_number = fieldPersonalId.getText().toString().trim();
        String ssn_last_4 = fieldSsn.getText().toString().trim();
        String address = fieldAddress.getText().toString().trim();
        String postal_code = fieldPostalCode.getText().toString().trim();
        String birth_date = fieldDob.getText().toString().trim();


        if (areFieldsValid(bank_name, account_title, account_number, routing_number, ssn_last_4, address, postal_code, birth_date)) {

            JsonObject json = new JsonObject();


           // json.addProperty("bank_name", bank_name);
            json.addProperty("account_title", account_title);
            json.addProperty("routing_number", routing_number);

            json.addProperty("account_number", account_number);
           // json.addProperty("personal_id_number", personal_id_number);
            json.addProperty("ssn_last_4", ssn_last_4);
            json.addProperty("address", address);
            json.addProperty("state", selectedState.id);
            json.addProperty("city", selectedCity.id);
            json.addProperty("postal_code", postal_code);
            json.addProperty("birth_date", birth_date);



            String encryptedPayLoad = EncryptionHelper.encryptAES(json.toString());

            getActivityViewModel().postBankDetail(encryptedPayLoad).observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case success:
                        hideLoader();
                        if (webResponseResource.data.body != null) {
                            try {
                                BankItem bankItem = getGson().fromJson(EncryptionHelper.decryptAES(webResponseResource.data.body), BankItem.class);
                                setFields(bankItem);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        makeSnackbar(webResponseResource.data.message);
                        if (isRedirectable)
                            getFragmentActivity().actionBack();
                        break;
                    default:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        break;
                }
            });
        }
    }

    private boolean areFieldsValid(String bankName, String accountName, String accountNo, String bankRouting, String ssn_last_4, String address, String postal_code, String birth_date) {

        boolean valid = true;

        if (selectedState == null) {
            inputlayoutState.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutState.setError(null);

        if (selectedCity == null) {
            inputlayoutCity.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutCity.setError(null);

       /* if (bankName.length() < 1) {
            inputlayoutBankName.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutBankName.setError(null);*/

        if (accountName.length() < 1) {
            inputlayoutAccountName.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutAccountName.setError(null);

        if (bankRouting.length() < 1) {
            inputlayoutBankRouting.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutBankRouting.setError(null);

        if (accountNo.length() < 1) {
            inputlayoutAccountNo.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutAccountNo.setError(null);

      /*  if (personal_id_number.length() < 1) {
            inputlayoutPersonalId.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutPersonalId.setError(null);*/

        if (ssn_last_4.length() < 4) {
            inputlayoutPersonalId.setError(getString(R.string.error_validation_invalid));
            valid = false;
        } else inputlayoutPersonalId.setError(null);

        if (address.length() < 1) {
            inputlayoutAddress.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutAddress.setError(null);

        if (postal_code.length() < 1) {
            inputlayoutPostalCode.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutPostalCode.setError(null);

        if (birth_date.length() < 1) {
            inputlayoutDob.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutDob.setError(null);

        return valid;
    }
}
