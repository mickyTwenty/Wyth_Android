package com.seatus.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.seatus.Models.BaseLocationItem;
import com.seatus.R;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter<T> extends BaseAdapter implements Filterable {

    List<BaseLocationItem> data = new ArrayList<>();
    List<BaseLocationItem> defaultData = new ArrayList<>();
    Context context;
    static final int Layout = R.layout.item_text;  // Set Layout

    public LocationAdapter(Context context, List<BaseLocationItem> data) {
        this.context = context;
        this.data.addAll(data);
        this.defaultData.addAll(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        BaseLocationItem item = data.get(position);

        if (view == null) {
            LayoutInflater inflator = ((Activity) context).getLayoutInflater();
            view = inflator.inflate(Layout, null);
        }

        TextView text = (TextView) view.findViewById(R.id.text1);
        text.setText(item.name + "");

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data.clear();
                if (results.values != null)
                    data.addAll((ArrayList<BaseLocationItem>) results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<BaseLocationItem> FilteredArrayNames = new ArrayList<BaseLocationItem>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < defaultData.size(); i++) {
                    String dataNames = defaultData.get(i).name;
                    if (dataNames.toLowerCase().contains(constraint.toString())) {
                        FilteredArrayNames.add(defaultData.get(i));
                    }
                }
                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public BaseLocationItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


}
