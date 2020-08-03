package com.seatus.Holders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seatus.Models.UserItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Picasso;

/**
 * Created by rohail on 27-Oct-17.
 */

public class AddFriendsHolder extends EfficientViewHolder<UserItem> {

    public AddFriendsHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, UserItem object) {

        ((TextView) findViewByIdEfficient(R.id.name)).setText(object.getFull_name());
        TextView txtStatus = findViewByIdEfficient(R.id.status);
        ImageView img = findViewByIdEfficient(R.id.img);

        if (object.status == null) {
            txtStatus.setTextColor(ContextCompat.getColor(context, R.color.orange));
            txtStatus.setText("Pending");
        } else
            switch (object.status) {
                case 0:
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.orange));
                    txtStatus.setText("Pending");
                    break;
                case 1:
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
                    txtStatus.setText("Accepted");
                    break;
                case -1:
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
                    txtStatus.setText("Rejected");
                    break;
            }

        try {
            Picasso.with(context).load(object.profile_picture).fit().centerCrop().into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
