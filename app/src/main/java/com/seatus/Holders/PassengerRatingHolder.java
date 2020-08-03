package com.seatus.Holders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.seatus.BaseClasses.FragmentHandlingActivity;
import com.seatus.Fragments.Driver.PassengerRatingFragment;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.DialogHelper;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saqib on 2/8/2018.
 */

public class PassengerRatingHolder extends EfficientViewHolder<UserItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public PassengerRatingHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, UserItem object) {
        TextView passengerName = findViewByIdEfficient(R.id.txt_passenger_name);
        passengerName.setText(object.getFull_name());

        CircleImageView passengerImage = findViewByIdEfficient(R.id.circleImageView);
        if (passengerImage != null && !TextUtils.isEmpty(object.profile_picture))
            Picasso.with(context).load(object.profile_picture).fit().centerCrop().into(passengerImage);

        RatingBar ratingBar = findViewByIdEfficient(R.id.ratings);

        if (object.is_rated) {
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(object.rating.floatValue());
        } else
            ratingBar.setVisibility(View.GONE);

        findViewByIdEfficient(R.id.btn_rate_now).setOnClickListener(v -> {
            ((PassengerRatingFragment) ((FragmentHandlingActivity) getContext()).getCurrentFragment()).ratePassenger(getAdapterPosition());
        });
    }
}
