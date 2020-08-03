package com.seatus.Holders;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.seatus.Models.SearchPreferenceItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;

/**
 * Created by saqib on 10/25/2017.
 */

public class PreferenceItemHolder extends EfficientViewHolder<SearchPreferenceItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public PreferenceItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, SearchPreferenceItem object) {

        TextView name = findViewByIdEfficient(R.id.pref_name);

        AppCompatCheckBox checked = findViewByIdEfficient(R.id.option1);

        name.setText(object.title + ":");

        checked.setText(TextUtils.isEmpty(object.option) ? "Yes" : object.option);
        checked.setOnCheckedChangeListener(null);
        checked.setChecked(object.checked);
        checked.setOnCheckedChangeListener((buttonView, isChecked) -> object.checked = isChecked);

    }
}
