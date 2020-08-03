package com.seatus.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seatus.R;

/**
 * Created by saqib on 12/7/2017.
 */

public class PassengerOfferHolder extends RecyclerView.ViewHolder {
    public TextView name, message, date;
    public ImageView btnAcceptOffer;
    public RelativeLayout messageBox;

    public PassengerOfferHolder(View itemView) {
        super(itemView);
        messageBox = itemView.findViewById(R.id.ll_msg_box);
        message = itemView.findViewById(R.id.txt_msg);
        date = itemView.findViewById(R.id.txt_date);
        btnAcceptOffer = itemView.findViewById(R.id.btn_accept_offer);
    }
}
