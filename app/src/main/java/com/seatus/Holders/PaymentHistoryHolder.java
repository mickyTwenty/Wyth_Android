package com.seatus.Holders;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.Fragments.Passenger.PaymentHistoryFragment;
import com.seatus.Models.CardItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;

/**
 * Created by saqib on 1/25/2018.
 */

public class PaymentHistoryHolder extends EfficientViewHolder<CardItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public PaymentHistoryHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, CardItem object) {
        TextView origin = findViewByIdEfficient(R.id.txt_origin);
        TextView destination = findViewByIdEfficient(R.id.txt_destination);
        TextView date = findViewByIdEfficient(R.id.txt_date);
        TextView cardNo = findViewByIdEfficient(R.id.txt_ccn);
        TextView amount = findViewByIdEfficient(R.id.txt_amount);
        TextView hint = findViewByIdEfficient(R.id.txt_hint);

        LinearLayout layoutCard = findViewByIdEfficient(R.id.layout_card);
        View dividerCard = findViewByIdEfficient(R.id.divider_card);

        LinearLayout layoutDate = findViewByIdEfficient(R.id.layout_date);
        View dividerDate = findViewByIdEfficient(R.id.divider_date);

        LinearLayout layoutTransaction = findViewByIdEfficient(R.id.layout_transaction);
        View dividerTranaction = findViewByIdEfficient(R.id.divider_transaction);
        TextView txtTransaction = findViewByIdEfficient(R.id.txt_transaction);

        origin.setText(object.origin_title);
        destination.setText(object.destination_title);

        if (PaymentHistoryFragment.userType == UserItem.UserType.driver) {
            layoutCard.setVisibility(View.GONE);
            dividerCard.setVisibility(View.GONE);
            layoutDate.setVisibility(View.GONE);
            dividerDate.setVisibility(View.GONE);
            dividerTranaction.setVisibility(View.GONE);
            layoutTransaction.setVisibility(View.GONE);

            amount.setText("$ " + object.earning+"*");
            hint.setText("Earning: ");
        } else {
            layoutCard.setVisibility(View.VISIBLE);
            dividerCard.setVisibility(View.VISIBLE);
            layoutDate.setVisibility(View.VISIBLE);
            dividerDate.setVisibility(View.VISIBLE);
            dividerTranaction.setVisibility(View.VISIBLE);
            layoutTransaction.setVisibility(View.VISIBLE);

            txtTransaction.setText("$ " + object.transaction_fee);
            date.setText(object.payment_datetime);
            cardNo.setText("**** **** " + object.last_digits);
            amount.setText("$ " + object.amount);
            hint.setText("Amount: ");
        }
    }
}
