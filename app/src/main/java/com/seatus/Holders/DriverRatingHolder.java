package com.seatus.Holders;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.BaseClasses.FragmentHandlingActivity;
import com.seatus.Fragments.Driver.HomePassengerRatingFragment;
import com.seatus.Fragments.Passenger.HomeDriverRatingFragment;
import com.seatus.Fragments.RateFragment;
import com.seatus.Models.PendingRatingItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saqib on 2/8/2018.
 */

public class DriverRatingHolder extends EfficientViewHolder<PendingRatingItem.Data> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public DriverRatingHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, PendingRatingItem.Data object) {
        TextView originName = findViewByIdEfficient(R.id.txt_origin);
        originName.setText(object.trip.origin_title);

        TextView destinationName = findViewByIdEfficient(R.id.txt_destination);
        destinationName.setText(object.trip.destination_title);

        TextView driverName = findViewByIdEfficient(R.id.txt_driver_name);
        driverName.setText(object.user.getFull_name());

        TextView date = findViewByIdEfficient(R.id.txt_date);
        date.setText(object.trip.start_time);

        CircleImageView passengerImage = findViewByIdEfficient(R.id.circleImageView);
        if (passengerImage != null && !TextUtils.isEmpty(object.user.profile_picture))
            Picasso.with(context).load(object.user.profile_picture).fit().centerCrop().into(passengerImage);

        RatingBar ratingBar = findViewByIdEfficient(R.id.ratings);

        try {
            if (object.user.is_rated) {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(object.user.rating.floatValue());
            } else
                ratingBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewByIdEfficient(R.id.btn_rate_now).setOnClickListener(v -> {
            RateFragment rateFragment = (RateFragment) ((FragmentHandlingActivity) getContext()).getCurrentFragment();
            Fragment currFrag = rateFragment.getViewpageFragment(0);
            if (currFrag instanceof HomePassengerRatingFragment) {
                HomePassengerRatingFragment ratingFragment = (HomePassengerRatingFragment) rateFragment.getViewpageFragment(0);
                ratingFragment.rate(getAdapterPosition());
            } else {
                HomeDriverRatingFragment ratingFragment = (HomeDriverRatingFragment) rateFragment.getViewpageFragment(0);
                ratingFragment.rate(getAdapterPosition());
            }
        });
    }
}
