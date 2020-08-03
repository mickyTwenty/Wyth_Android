package com.seatus.Holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seatus.BaseClasses.FragmentHandlingActivity;
import com.seatus.Fragments.ChatFragment;
import com.seatus.Fragments.RideDetailFragment;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;

/**
 * Created by saqib on 11/22/2017.
 */

public class MessagingDriverHolder extends EfficientViewHolder<TripItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public MessagingDriverHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, TripItem object) {
        TextView tripName = findViewByIdEfficient(R.id.txt_trip_name);
        tripName.setText(object.getTripName());

        TextView origin = findViewByIdEfficient(R.id.txt_origin);
        origin.setText(object.origin_title);

        TextView destination = findViewByIdEfficient(R.id.txt_destination);
        destination.setText(object.destination_title);

        TextView date = findViewByIdEfficient(R.id.txt_date);
        date.setText(object.destination_title);

        findViewByIdEfficient(R.id.btn_group_chat).setOnClickListener(v -> {
            if((object.passengers.size()>0 && object.driver!= null) || object.passengers.size() > 1)
            ((FragmentHandlingActivity) getContext())
                    .replaceFragmentWithBackstack(
                            ChatFragment.newInstance(object.trip_id));
            else ((FragmentHandlingActivity) getContext()).makeSnackbar("There is currently no one to chat in this trip.");
        });

        findViewByIdEfficient(R.id.btn_ride_detail).setOnClickListener(v -> {
            ((FragmentHandlingActivity) getContext())
                    .replaceFragmentWithBackstack(
                            RideDetailFragment.newInstance(object.trip_id));
        });
    }
}
