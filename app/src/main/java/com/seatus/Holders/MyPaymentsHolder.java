package com.seatus.Holders;

import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.BaseClasses.FragmentHandlingActivity;
import com.seatus.Fragments.Passenger.MyPaymentsFragment;
import com.seatus.Fragments.Passenger.PaymentsFragment;
import com.seatus.Models.CardItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;

/**
 * Created by saqib on 1/24/2018.
 */

public class MyPaymentsHolder extends EfficientViewHolder<CardItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public MyPaymentsHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, CardItem object) {
        TextView count = findViewByIdEfficient(R.id.txt_ccn_count);
        TextView cardNo = findViewByIdEfficient(R.id.txt_ccn);
        CheckBox isDefault = findViewByIdEfficient(R.id.checkbox_isDefault);
        ImageView divider = findViewByIdEfficient(R.id.vertical_divider);
        LinearLayout ll = findViewByIdEfficient(R.id.ll_fields);

        count.setText("Card Number " + (getAdapterPosition() + 1) + ":");
        cardNo.setText("**** **** " + object.last_digits);
        isDefault.setChecked(object.is_default);

        if (object.isLast)
            divider.setVisibility(View.INVISIBLE);
        try {
            PaymentsFragment paymentsFragment = (PaymentsFragment) ((FragmentHandlingActivity) context).getCurrentFragment();
            MyPaymentsFragment myPaymentsFragment = (MyPaymentsFragment) paymentsFragment.getViewpageFragment(0);

            isDefault.setOnClickListener(v -> {
                if (!object.is_default)
                    myPaymentsFragment.setDefaultCard(getAdapterPosition());
                else isDefault.setChecked(true);
            });

            ll.setOnLongClickListener(v -> {
                myPaymentsFragment.removeCardDialog(getAdapterPosition());
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
