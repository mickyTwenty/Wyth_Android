package com.seatus.Holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seatus.Models.NotificationListingItem;
import com.seatus.R;
import com.seatus.Utils.DateTimeHelper;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by saqib on 1/29/2018.
 */

public class NotificationHolder extends EfficientViewHolder<NotificationListingItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public NotificationHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, NotificationListingItem object) {
        TextView notificationText = findViewByIdEfficient(R.id.txt_notification);
        TextView date = findViewByIdEfficient(R.id.txt_date);

        notificationText.setText(object.payload.data.data_message);

        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTimeInMillis(Long.parseLong(object.unix_timestamp) * 1000L);
        date.setText(DateTimeHelper.getChatDateTime(cal.getTime()));
    }
}
