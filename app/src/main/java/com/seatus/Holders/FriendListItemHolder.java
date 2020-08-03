package com.seatus.Holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.seatus.Models.UserItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Picasso;

/**
 * Created by rohail on 27-Oct-17.
 */

public class FriendListItemHolder extends EfficientViewHolder<UserItem> {

    public FriendListItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, UserItem object) {
        ((TextView) findViewByIdEfficient(R.id.name)).setText(object.getFull_name());
        try {
            ((RatingBar) findViewByIdEfficient(R.id.rating)).setRating(object.rating.floatValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Picasso.with(context).load(object.profile_picture).fit().centerCrop().into((ImageView) findViewByIdEfficient(R.id.img));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
