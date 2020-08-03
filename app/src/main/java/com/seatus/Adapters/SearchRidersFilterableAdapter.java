package com.seatus.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.seatus.Models.SearchFilterDataItem;
import com.seatus.Models.SearchPreferenceItem;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.seatus.Utils.StaticMethods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rohail on 4/10/2018.
 */

public class SearchRidersFilterableAdapter extends RecyclerView.Adapter<SearchRidersFilterableAdapter.FindRideViewHolder> implements Filterable {

    public Context context;
    public ArrayList<TripItem> defaultData = new ArrayList<>();
    public ArrayList<TripItem> data = new ArrayList<>();

    public TripFilter mFilter;
    public boolean isUser = true;

    public Gson gson;

    public SearchRidersFilterableAdapter(Context context, boolean isUser, ArrayList<TripItem> list) {
        this.context = context;
        defaultData.addAll(list);
        data.addAll(list);
        this.isUser = isUser;
        gson = new Gson();
    }

    public void notifyChange(ArrayList<TripItem> list) {
        defaultData.clear();
        data.clear();
        defaultData.addAll(list);
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public FindRideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_search_riders, parent, false);
        return new FindRideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FindRideViewHolder holder, int position) {

        TripItem object = data.get(position);

        try {
            if (!TextUtils.isEmpty(object.group_id)) {
                if (position > 0) {
                    TripItem tripItem = data.get(position - 1);

                    if (tripItem.group_id != null && tripItem.group_id.trim().equals(object.group_id.trim())) {
                        holder.groupView.setVisibility(View.GONE);
                    } else {
                        holder.groupView.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.groupView.setVisibility(View.VISIBLE);
                }
                holder.bgLL.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));
            } else {
                holder.groupView.setVisibility(View.GONE);
                holder.bgLL.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            holder.gender.setText(object.gender);
            holder.origin.setText(object.origin_title);
            holder.dest.setText(object.destination_title);
            holder.rating.setRating(object.driver.rating.floatValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (isUser) {
                holder.othernameHint.setText("Driver Name:");
                holder.othername.setText(object.driver.getFull_name());
                Picasso.with(context).load(object.driver.profile_picture).fit().centerCrop().into(holder.img);
                holder.rating.setRating(object.driver.rating.floatValue());
            } else {
                holder.othernameHint.setText("Passenger Name:");
                holder.othername.setText(object.passenger.getFull_name());
                Picasso.with(context).load(object.passenger.profile_picture).fit().centerCrop().into(holder.img);
                holder.rating.setRating(object.passenger.rating.floatValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null)
            mFilter = new TripFilter();
        return mFilter;
    }

    public class FindRideViewHolder extends RecyclerView.ViewHolder {

        TextView origin;
        TextView groupView;
        LinearLayout bgLL;
        TextView dest;
        TextView othernameHint;
        TextView othername;
        TextView gender;
        ImageView img;
        RatingBar rating;

        public FindRideViewHolder(View itemView) {
            super(itemView);
            origin = itemView.findViewById(R.id.txt_origin);
            groupView = itemView.findViewById(R.id.view_group);
            bgLL = itemView.findViewById(R.id.ll_parent);
            dest = itemView.findViewById(R.id.txt_destination);
            othername = itemView.findViewById(R.id.name);
            othernameHint = itemView.findViewById(R.id.txt_name_hint);
            gender = itemView.findViewById(R.id.trip_gender);
            img = itemView.findViewById(R.id.img);
            rating = itemView.findViewById(R.id.rating);
        }
    }

    public class TripFilter extends Filter {

        ArrayList<String> rejectedGroups = new ArrayList<>();

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            ArrayList<TripItem> filterdData = new ArrayList<>();

            rejectedGroups.clear();
            SearchFilterDataItem searchItem = new Gson().fromJson(constraint.toString(), SearchFilterDataItem.class);

            for (int position = 0; position < defaultData.size(); position++) {

                TripItem item = defaultData.get(position);
                boolean isMatch = true;

                if (searchItem.isRoundTrip == 1) {
                    if (TextUtils.isEmpty(item.group_id)) {
                        if ((item.rides.get(0).time_range & searchItem.time_range) == 0)
                            isMatch = false;
                        if ((item.rides.get(1).time_range & searchItem.return_time_range) == 0)
                            isMatch = false;
                    } else {
                        Integer pairPos = item.getPairedItemPosition(defaultData);
                        if (pairPos != null) {
                            if (position < pairPos) {
                                if ((item.rides.get(0).time_range & searchItem.time_range) == 0)
                                    isMatch = false;
                            } else {
                                if ((item.rides.get(0).time_range & searchItem.return_time_range) == 0)
                                    isMatch = false;
                            }
                        }
                    }
                } else {
                    if ((item.rides.get(0).time_range & searchItem.time_range) == 0)
                        isMatch = false;
                }

                if (isUser) {
                    if (!searchItem.gender.equals("Both") && !item.driver.gender.equals(searchItem.gender))
                        isMatch = false;
                } else {
                    if (!searchItem.gender.equals("Both") && !item.passenger.gender.equals(searchItem.gender))
                        isMatch = false;
                }

                ArrayList<SearchPreferenceItem> list = StaticMethods.getArrayListFromJson(gson, searchItem.preferences, SearchPreferenceItem.class);

                if (!isMatchingPreferences(item.preferences, list))
                    isMatch = false;

                if (isMatch) {
                    if (TextUtils.isEmpty(item.group_id) || !rejectedGroups.contains(item.group_id))
                        filterdData.add(item);
                } else {
                    // Removing Suggested Paired Trip for Round Trip
                    if (!TextUtils.isEmpty(item.group_id)) {
                        Integer pairPos = item.getPairedItemPosition(filterdData);
                        if (pairPos != null) {
                            filterdData.remove(pairPos.intValue());
                        } else
                            rejectedGroups.add(item.group_id);
                    }
                }
            }

            results.count = filterdData.size();
            results.values = filterdData;

            return results;
        }

        private boolean isMatchingPreferences(ArrayList<SearchPreferenceItem> tripPrefs, ArrayList<SearchPreferenceItem> searchPrefs) {
            for (SearchPreferenceItem item : tripPrefs) {
                if (!searchPrefs.contains(item))
                    return false;
            }
            return true;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            if (results.values != null)
                data.addAll((ArrayList<TripItem>) results.values);
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickInterface {
        void onTripItemClickListener(TripItem tripItem);
    }
}
