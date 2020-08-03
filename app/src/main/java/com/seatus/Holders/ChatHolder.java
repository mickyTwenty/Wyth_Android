package com.seatus.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.R;

/**
 * Created by saqib on 12/7/2017.
 */

public class ChatHolder extends RecyclerView.ViewHolder {
    public TextView name, message, date;
    public LinearLayout messageBox;

    public ChatHolder(View itemView) {
        super(itemView);
        messageBox = itemView.findViewById(R.id.ll_msg_box);
        name = itemView.findViewById(R.id.txt_name);
        message = itemView.findViewById(R.id.txt_msg);
        date = itemView.findViewById(R.id.txt_date);
    }
}
