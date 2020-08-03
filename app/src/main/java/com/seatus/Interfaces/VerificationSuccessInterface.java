package com.seatus.Interfaces;

import com.seatus.Dialogs.VerificationDialog;
import com.seatus.Models.UserItem;

/**
 * Created by rohail on 6/1/2018.
 */

public interface VerificationSuccessInterface {
    void userVerified(UserItem user);

    void onResend(VerificationDialog dialog);
}
