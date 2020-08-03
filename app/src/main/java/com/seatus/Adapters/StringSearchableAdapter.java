package com.seatus.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.seatus.R;

import java.util.ArrayList;
import java.util.List;

public class StringSearchableAdapter extends BaseAdapter implements Filterable {

    List<String> data = new ArrayList<>();
    List<String> defaultData = new ArrayList<>();
    Context context;
    static final int Layout = R.layout.item_text;  // Set Layout

    public StringSearchableAdapter(Context context, List<String> data) {
        this.context = context;
        this.data.addAll(data);
        this.defaultData.addAll(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        String item = data.get(position);

        if (view == null) {
            LayoutInflater inflator = ((Activity) context).getLayoutInflater();
            view = inflator.inflate(Layout, null);
        }

        TextView text = view.findViewById(R.id.text1);
        text.setText(item);

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
                    data.addAll((ArrayList<String>) results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<String> FilteredArrayNames = new ArrayList<String>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < defaultData.size(); i++) {
                    if (defaultData.get(i).toLowerCase().contains(constraint.toString())) {
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
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


}
