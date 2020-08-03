package com.seatus.Holders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.Models.UserItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.squareup.picasso.Picasso;

/**
 * Created by rohail on 27-Oct-17.
 */

public class PickDropHolder extends EfficientViewHolder<UserItem> {

    public PickDropHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, UserItem object) {

        ((TextView) findViewByIdEfficient(R.id.name)).setText(object.getFull_name());
        CheckBox checkbox = findViewByIdEfficient(R.id.checkbox);
        ImageView img = findViewByIdEfficient(R.id.img);
        LinearLayout parent = findViewByIdEfficient(R.id.parent);

        parent.setOnClickListener(v -> {
            checkbox.toggle();
        });
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> object.checked = isChecked);

        try {
            Picasso.with(context).load(object.profile_picture).fit().centerCrop().into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
