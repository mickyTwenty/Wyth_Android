package com.seatus.Adapters;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.seatus.Models.UserItem;
import com.seatus.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FriendsAdapter extends BaseAdapter implements Filterable {

    List<UserItem> data = new ArrayList<>();
    List<UserItem> defaultData = new ArrayList<>();
    Activity context;
    static final int Layout = R.layout.item_friend_autocomplete;  // Set Layout

    public FriendsAdapter(Activity context, List<UserItem> data) {
        this.context = context;
        this.data.addAll(data);
        this.defaultData.addAll(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        UserItem item = data.get(position);

        if (view == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(Layout, null);
        }

        TextView text = view.findViewById(R.id.name);
        ImageView img = view.findViewById(R.id.img);
        text.setText(item.getFull_name());

        try {
            Picasso.with(context).load(item.profile_picture).fit().centerCrop().into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    data.addAll((ArrayList<UserItem>) results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<UserItem> FilteredArrayNames = new ArrayList<UserItem>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < defaultData.size(); i++) {
                    String dataName = defaultData.get(i).getFull_name().toLowerCase(Locale.US);
                    if (dataName.contains(constraint.toString())) {
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
    public UserItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


}
