package com.seatus.Holders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.seatus.BaseClasses.FragmentHandlingActivity;
import com.seatus.Fragments.ChatFragment;
import com.seatus.Fragments.Passenger.MyTripsFragment;
import com.seatus.Fragments.RideDetailFragment;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saqib on 11/22/2017.
 */

public class MessagingPassengerHolder extends EfficientViewHolder<TripItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public MessagingPassengerHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, TripItem object) {
        TextView driverName = findViewByIdEfficient(R.id.txt_driver_name);
        driverName.setText(object.driver.first_name);

        TextView tripName = findViewByIdEfficient(R.id.txt_trip_name);
        tripName.setText(object.getTripName());

        CircleImageView img_driver = findViewByIdEfficient(R.id.circleImageView);

        try {
            ((RatingBar) findViewByIdEfficient(R.id.ratings)).setRating(object.driver.rating.floatValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(object.driver.profile_picture))
            Picasso.with(getContext()).load(object.driver.profile_picture).fit().centerCrop().into(img_driver);

        findViewByIdEfficient(R.id.btn_group_chat).setOnClickListener(v -> {
            if ((object.passengers.size() > 0 && object.driver != null) || object.passengers.size() > 1)
                ((FragmentHandlingActivity) getContext())
                        .replaceFragmentWithBackstack(
                                ChatFragment.newInstance(object.trip_id));
            else
                ((FragmentHandlingActivity) getContext()).makeSnackbar("There is currently no one to chat in this trip.");
        });

        findViewByIdEfficient(R.id.btn_ride_detail).setOnClickListener(v -> {
            ((FragmentHandlingActivity) getContext())
                    .replaceFragmentWithBackstack(
                            RideDetailFragment.newInstance(object.trip_id));
        });
    }
}
