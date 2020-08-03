package com.seatus.Holders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.BaseClasses.FragmentHandlingActivity;
import com.seatus.Fragments.Passenger.MyTripsFragment;
import com.seatus.Fragments.RideDetailFragment;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saqib on 11/21/2017.
 */

public class PassengerTripsHolder extends EfficientViewHolder<TripItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public PassengerTripsHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, TripItem object) {

        TextView driverName = findViewByIdEfficient(R.id.txt_driver_name);
        driverName.setText(object.driver.first_name + " " + object.driver.last_name);

        TextView origin = findViewByIdEfficient(R.id.txt_origin);
        origin.setText(object.origin_title);

        TextView destination = findViewByIdEfficient(R.id.txt_destination);
        destination.setText(object.destination_title);

        TextView date = findViewByIdEfficient(R.id.txt_date);
        date.setText(object.date);

        LinearLayout layoutDriver = findViewByIdEfficient(R.id.layout_drivername);
        CircleImageView img_driver = findViewByIdEfficient(R.id.circleImageView);

        if (object.driver == null || TextUtils.isEmpty(object.driver.user_id)) {
            layoutDriver.setVisibility(View.GONE);
            img_driver.setBackgroundResource(R.drawable.bg_image_nodriver);
        } else {
            img_driver.setBackgroundResource(R.drawable.bg_image_circle);
            layoutDriver.setVisibility(View.VISIBLE);
            img_driver.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(object.driver.profile_picture))
                Picasso.with(getContext()).load(object.driver.profile_picture).fit().centerCrop().into(img_driver);
        }

        findViewByIdEfficient(R.id.btn_view_detail).setOnClickListener(v -> {
            ((FragmentHandlingActivity) getContext())
                    .replaceFragmentWithBackstack(
                            RideDetailFragment.newInstance(object.trip_id));
        });
    }
}
