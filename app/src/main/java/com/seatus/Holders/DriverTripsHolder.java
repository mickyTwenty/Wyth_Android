package com.seatus.Holders;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.BaseClasses.FragmentHandlingActivity;
import com.seatus.Fragments.RideDetailFragment;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.seatus.Utils.DateTimeHelper;
import com.seatus.Utils.StaticMethods;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;

import org.w3c.dom.Text;

import java.util.Calendar;

import static com.seatus.Utils.AppConstants.LOCAL_TRIP_RANGE;

/**
 * Created by saqib on 11/21/2017.
 */

public class DriverTripsHolder extends EfficientViewHolder<TripItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public DriverTripsHolder(View itemView) {
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
        date.setText(object.date);

        TextView seats = findViewByIdEfficient(R.id.txt_seats_filled);
        int seatsFilled = object.seats_total - object.seats_available;
        seats.setText(new StringBuilder().append(seatsFilled).append(" of ").append(object.seats_total).append(object.seats_total == 1 ? " seat" : " seats").append(" filled").toString());

        findViewByIdEfficient(R.id.btn_view_detail).setOnClickListener(v -> {
            ((FragmentHandlingActivity) getContext())
                    .replaceFragmentWithBackstack(
                            RideDetailFragment.newInstance(object.trip_id));
        });

        LinearLayout selectTime = findViewByIdEfficient(R.id.btn_select_time);
        TextView selectedTime = findViewByIdEfficient(R.id.txt_select_time);

        if(object.expected_distance!=null && object.start_time!=null && (Integer.parseInt(object.expected_distance) < LOCAL_TRIP_RANGE))
        {
            selectTime.setVisibility(View.VISIBLE);

            if(object.start_time.contains("00:00:00")) {
                selectedTime.setText("Select Start Time");
                selectTime.setOnClickListener(v -> {

                    RideDetailFragment newFragment = RideDetailFragment.newInstance(object.trip_id);
                    StaticMethods.timePopup(getContext(), DateTimeHelper.parseDate(object.date).getTime(), cal -> newFragment.selectTime(cal));

                    ((FragmentHandlingActivity) getContext())
                            .replaceFragmentWithBackstack(newFragment);
                });
            }
            else
            {
                selectedTime.setText(DateTimeHelper.getTimeToShow(DateTimeHelper.convertFromUTC(DateTimeHelper.parseDate(object.start_time, DateTimeHelper.DATE_SERVER_FORMAT).getTime())));
                selectTime.setOnClickListener(v -> {
                    ((FragmentHandlingActivity) getContext())
                            .replaceFragmentWithBackstack(RideDetailFragment.newInstance(object.trip_id));
                });
            }

        }
        else
        {
            selectTime.setVisibility(View.INVISIBLE);
        }
    }
}
