package com.seatus.Holders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.BaseClasses.FragmentHandlingActivity;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saqib on 11/21/2017.
 */

public class RideSeatsDetailHolder extends EfficientViewHolder<UserItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public RideSeatsDetailHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, UserItem object) {

        ImageView statusImage = findViewByIdEfficient(R.id.img_status);
        TextView status = findViewByIdEfficient(R.id.txt_status);
        LinearLayout bg_status = findViewByIdEfficient(R.id.bg_status);
        TextView passengerStatus = findViewByIdEfficient(R.id.passengerStatus);
        TextView passengerName = findViewByIdEfficient(R.id.passengerName);
        CircleImageView passengerImage = findViewByIdEfficient(R.id.passengerImage);
        AppCompatRatingBar ratingBar = findViewByIdEfficient(R.id.ratings);

        LinearLayout extraInfo = findViewByIdEfficient(R.id.layout_info);
        TextView txtBags = findViewByIdEfficient(R.id.txt_bags);
        TextView txtPrice = findViewByIdEfficient(R.id.txt_price);


        passengerName.setText(object.getFull_name());

        if (object.first_name.equals("XXX")) {
            statusImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_available));
            bg_status.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_passenger_detail));
            passengerImage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_image_circle));
            status.setText("Available");
            passengerStatus.setText("Not booked:");
        } else if (object.is_third_party) {
            statusImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_available_driver));
            bg_status.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_passenger_detail));
            passengerImage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_image_circle));
            status.setText(object.state_text);
            passengerStatus.setText("Driver");

            if (passengerImage != null && !TextUtils.isEmpty(object.profile_picture))
                Picasso.with(context).load(object.profile_picture).fit().centerCrop().into(passengerImage);
            try {
                ratingBar.setRating(object.rating.floatValue());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            if (TextUtils.isEmpty(object.getFull_name()))
//                passengerName.setVisibility(View.GONE);
        } else {
            if (passengerImage != null && !TextUtils.isEmpty(object.profile_picture))
                Picasso.with(context).load(object.profile_picture).fit().centerCrop().into(passengerImage);

            ratingBar.setRating(object.rating.floatValue());

            if (object.is_confirmed && object.has_payment_made) {
                statusImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_filled));
                bg_status.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_passenger_detail));
                passengerImage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_image_circle));
                status.setText("Filled");
                passengerStatus.setText("Booked by:");

                if (((FragmentHandlingActivity) context).getUserItem().getCurrentInterface())
                    extraInfo.setVisibility(View.GONE);
                else {
                    extraInfo.setVisibility(View.VISIBLE);
                    txtBags.setText("Bags: " + object.bags_quantity);
                    txtPrice.setText("Fare: $" + object.fare);
                }
            } else {
                statusImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_reserved));
                bg_status.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_passenger_detail_yellow));
                passengerImage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_image_circle_yellow));
                status.setText("Reserved");
                passengerStatus.setText("Reserved by:");
            }
        }
    }
}
