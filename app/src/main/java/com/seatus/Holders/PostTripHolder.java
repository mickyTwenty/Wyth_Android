package com.seatus.Holders;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.seatus.Models.SearchPreferenceItem;
import com.seatus.R;
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;

/**
 * Created by saqib on 10/25/2017.
 */

public class PostTripHolder extends EfficientViewHolder<SearchPreferenceItem> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public PostTripHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, SearchPreferenceItem object) {

        TextView name = findViewByIdEfficient(R.id.pref_name);

        AppCompatCheckBox option1 = findViewByIdEfficient(R.id.option1);
        AppCompatCheckBox option2 = findViewByIdEfficient(R.id.option2);

        name.setText(object.title);

        if (object.options != null && object.options.size() >= 2) {
            option1.setText(object.options.get(0).label);
            option2.setText(object.options.get(1).label);
            option1.setOnCheckedChangeListener(null);
            option2.setOnCheckedChangeListener(null);
            option1.setChecked(object.options.get(0).checked);
            option2.setChecked(object.options.get(1).checked);
            option1.setOnCheckedChangeListener((compoundButton, b) ->
                    object.options.get(0).checked = b
            );
            option2.setOnCheckedChangeListener((compoundButton, b) -> object.options.get(1).checked = b);
        }

    }
}
