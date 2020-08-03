package com.seatus.Holders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.seatus.Fragments.Passenger.OffersListingFragment;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Picasso;

/**
 * Created by saqib on 11/21/2017.
 */

public class OffersDriverListingHolder extends EfficientViewHolder<TripItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public OffersDriverListingHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, TripItem object) {

        LinearLayout bgLL = findViewByIdEfficient(R.id.ll_offer);
        TextView groupView = findViewByIdEfficient(R.id.view_offer);
        TextView origin = findViewByIdEfficient(R.id.txt_origin);
        TextView dest = findViewByIdEfficient(R.id.txt_destination);
        TextView passengerName = findViewByIdEfficient(R.id.name);
        TextView trip_name = findViewByIdEfficient(R.id.trip_name);
        ImageView img = findViewByIdEfficient(R.id.img);
        RatingBar rating = findViewByIdEfficient(R.id.rating);

        try {
            if (!TextUtils.isEmpty(object.group_id)) {
                if (getAdapterPosition() > 0) {
                    TripItem tripItem = OffersListingFragment.mList.get(getAdapterPosition() - 1);

                    if (tripItem.group_id != null && tripItem.group_id.trim().equals(object.group_id.trim())) {
                        groupView.setVisibility(View.GONE);
                    } else {
                        groupView.setVisibility(View.VISIBLE);
                    }
                } else {
                    groupView.setVisibility(View.VISIBLE);
                }
                bgLL.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_grey));
            } else {
                groupView.setVisibility(View.GONE);
                bgLL.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            }

            trip_name.setText(object.getTripName());
            origin.setText(object.origin_title);
            dest.setText(object.destination_title);
            passengerName.setText(object.passenger.getFull_name());
            rating.setRating(object.passenger.rating.floatValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Picasso.with(context).load(object.passenger.profile_picture).fit().centerCrop().into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
