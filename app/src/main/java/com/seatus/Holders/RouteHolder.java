package com.seatus.Holders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.model.Route;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;

/**
 * Created by rah on 21-Nov-17.
 */

public class RouteHolder extends EfficientViewHolder<Route> {

    public RouteHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, Route object) {
        TextView name = findViewByIdEfficient(R.id.name);
        TextView distance = findViewByIdEfficient(R.id.distance);
        TextView popularity = findViewByIdEfficient(R.id.popularity);
        TextView time = findViewByIdEfficient(R.id.time);

        LinearLayout layout_popularity = findViewByIdEfficient(R.id.layout_popularity);

        Route.TimeDistanceInfo info = object.getRouteTimeDistance();

        name.setText(object.getSummary());
        distance.setText(info.getDistanceText());
        time.setText(info.getTimeText());
        if (object.popularity == null)
            layout_popularity.setVisibility(View.GONE);
        else
            popularity.setText(String.valueOf(object.popularity));

        if (object.isSelected)
            getView().setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
        else
            getView().setBackgroundColor(ContextCompat.getColor(context, R.color.white));
    }
}
