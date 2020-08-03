package com.seatus.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.seatus.Activities.MainActivity;
import com.seatus.Holders.PassengerOfferHolder;
import com.seatus.Interfaces.DriverOfferAcceptInterface;
import com.seatus.Interfaces.OnOfferAcceptInterface;
import com.seatus.Models.OfferItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.DateTimeHelper;

/**
 * Created by saqib on 12/7/2017.
 */

public class DriverOffersAdapter extends FirestoreRecyclerAdapter<OfferItem, PassengerOfferHolder> {

    private final DriverOfferAcceptInterface iface;
    private Context context;
    private UserItem userItem;

    public int bags;
    public int last_bags_position;

    public DriverOffersAdapter(Context context, UserItem userItem, FirestoreRecyclerOptions options, DriverOfferAcceptInterface iface) {
        super(options);
        this.context = context;
        this.userItem = userItem;
        this.iface = iface;
    }

    @Override
    protected void onBindViewHolder(PassengerOfferHolder holder, int position, OfferItem model) {
        UserItem userItem = ((MainActivity) context).getUserItem();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (!model.sender_id.toString().equals(userItem.user_id)) {
            params.gravity = Gravity.START;
            holder.messageBox.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chatbox_grey));
            holder.messageBox.setLayoutParams(params);
            holder.message.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.date.setTextColor(ContextCompat.getColor(context, R.color.black));

            holder.btnAcceptOffer.setVisibility(View.VISIBLE);
            if (last_bags_position == 0 || last_bags_position < position) {
                bags = model.bags;
                last_bags_position = position;
                iface.onBagsChanged(bags);
            }
            holder.btnAcceptOffer.setOnClickListener(v -> iface.onOfferAccepted(model));

        } else {
            params.gravity = Gravity.END;
            holder.messageBox.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chatbox_blue));
            holder.messageBox.setLayoutParams(params);
            holder.message.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.date.setTextColor(ContextCompat.getColor(context, R.color.white));

            holder.btnAcceptOffer.setVisibility(View.GONE);
        }
        holder.message.setText(model.getPrice());
        holder.date.setText(DateTimeHelper.getChatDateTime(model.timestamp));
    }

    @Override
    public PassengerOfferHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_offer, group, false);
        return new PassengerOfferHolder(view);
    }
}
