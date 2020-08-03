package com.seatus.Holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seatus.Models.UserItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;

/**
 * Created by saqib on 11/27/2017.
 */

public class ItineraryHolder extends EfficientViewHolder<UserItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public ItineraryHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, UserItem object) {
        TextView fname = findViewByIdEfficient(R.id.txt_fname);
        TextView email = findViewByIdEfficient(R.id.txt_email);
        TextView phoneNo = findViewByIdEfficient(R.id.txt_phoneNo);

        fname.setText(object.getFull_name());
        email.setText(object.email);
        phoneNo.setText(object.phone);
    }
}
