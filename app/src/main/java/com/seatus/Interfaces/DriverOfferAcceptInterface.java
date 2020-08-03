package com.seatus.Interfaces;

import com.seatus.Models.OfferItem;

/**
 * Created by rah on 18-Dec-17.
 */

public interface DriverOfferAcceptInterface {
    void onOfferAccepted(OfferItem offer);

    void onBagsChanged(Integer bags);
}
