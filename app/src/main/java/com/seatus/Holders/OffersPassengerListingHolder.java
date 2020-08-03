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

public class OffersPassengerListingHolder extends EfficientViewHolder<TripItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public OffersPassengerListingHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, TripItem object) {

        TextView origin = findViewByIdEfficient(R.id.txt_origin);
        TextView dest = findViewByIdEfficient(R.id.txt_destination);
        TextView drivername = findViewByIdEfficient(R.id.name);
        TextView trip_name = findViewByIdEfficient(R.id.trip_name);
        ImageView img = findViewByIdEfficient(R.id.img);
        RatingBar rating = findViewByIdEfficient(R.id.rating);

        try {

            trip_name.setText(object.getTripName());
            origin.setText(object.origin_title);
            dest.setText(object.destination_title);

            rating.setRating(object.driver.rating.floatValue());
            drivername.setText(object.driver.getFull_name());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Picasso.with(context).load(object.driver.profile_picture).fit().centerCrop().into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
