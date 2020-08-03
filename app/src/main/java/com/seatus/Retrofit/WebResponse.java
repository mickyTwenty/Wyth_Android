package com.seatus.Retrofit;


import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

public class WebResponse<T> {

    public static final String ERROR_ACTION_SIGNUP = "action_signup";
    public static final String ERROR_EMAIL_VERIFICATION = "action_verify_email";
    public static final String ERROR_NO_CARD = "invalid_credit_card";
    public static final String ERROR_NO_BANK_DETAILS= "add_bank_account";


    @Expose
    public boolean status;

    @Expose
    public String error_code;

    @Expose
    public PagingInfo paging;

    @Nullable
    @Expose
    public String message;

    @Nullable
    @Expose
    public T body;

    public boolean isSuccess() {
        return status;
    }

    public boolean isExpired() {
        try {
            if (error_code.equalsIgnoreCase("invalid_token"))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
