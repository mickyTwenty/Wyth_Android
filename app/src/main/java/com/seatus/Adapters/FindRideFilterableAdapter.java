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
import com.seatus.Utils.DateTimeHelper;
import com.seatus.Utils.StaticMethods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static com.seatus.Utils.AppConstants.LOCAL_TRIP_RANGE;

/**
 * Created by rohail on 4/10/2018.
 */

public class FindRideFilterableAdapter extends RecyclerView.Adapter<FindRideFilterableAdapter.FindRideViewHolder> implements Filterable {

    public Context context;
    public ArrayList<TripItem> defaultData = new ArrayList<>();
    public ArrayList<TripItem> data = new ArrayList<>();

    public TripFilter mFilter;
    public boolean isUser = true;

    OnItemClickInterface iface;

    public Gson gson;

    public FindRideFilterableAdapter(Context context, boolean isUser, ArrayList<TripItem> list, OnItemClickInterface iface) {
        this.context = context;
        defaultData.addAll(list);
        data.addAll(list);
        this.isUser = isUser;
        gson = new Gson();
        this.iface = iface;
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
                .inflate(R.layout.item_findrid_new, parent, false);
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
            holder.origin.setText(object.origin_title);
            holder.dest.setText(object.destination_title);
            holder.distance.setText(object.expected_distance_format);
            holder.rating.setRating(object.driver.rating.floatValue());

            int seatsFilled = object.seats_total - object.seats_available;
            holder.seats.setText(new StringBuilder().append(seatsFilled).append(" of ").append(object.seats_total).append(object.seats_total == 1 ? " seat" : " seats").append(" filled").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(object.expected_distance!=null && object.start_time!=null && (Integer.parseInt(object.expected_distance) < LOCAL_TRIP_RANGE))
            {
                holder.clock.setVisibility(View.VISIBLE);

                if(object.start_time.contains("00:00:00")) {
                    holder.clock.setVisibility(View.INVISIBLE);
                }
                else
                {
                    holder.time.setText(DateTimeHelper.getTimeToShow(DateTimeHelper.convertFromUTC(DateTimeHelper.parseDate(object.start_time, DateTimeHelper.DATE_SERVER_FORMAT).getTime())));
                }

            }
            else
            {
                holder.clock.setVisibility(View.INVISIBLE);
            }
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
        TextView distance;
        ImageView img;
        RatingBar rating;
        LinearLayout clock;
        TextView time;
        TextView seats;

        public FindRideViewHolder(View itemView) {
            super(itemView);
            origin = itemView.findViewById(R.id.txt_origin);
            groupView = itemView.findViewById(R.id.view_group);
            bgLL = itemView.findViewById(R.id.ll_parent);
            dest = itemView.findViewById(R.id.txt_destination);
            othername = itemView.findViewById(R.id.name);
            othernameHint = itemView.findViewById(R.id.txt_name_hint);
            distance = itemView.findViewById(R.id.txt_distance);
            img = itemView.findViewById(R.id.img);
            rating = itemView.findViewById(R.id.rating);
            clock = itemView.findViewById(R.id.btn_selected_time);
            time = itemView.findViewById(R.id.txt_selected_time);
            seats = itemView.findViewById(R.id.txt_seats_filled);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != -1) {
                    iface.onTripItemClickListener(data.get(pos));
                }
            });
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
                    try {
                        getSortWeight(item, searchItem);
                    }
                    catch (Exception e)
                    {

                    }
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
            Collections.sort(filterdData, new Comparator<TripItem>(){
                public int compare(TripItem o1, TripItem o2){
                    if(o1.sortWeight == o2.sortWeight)
                        return 0;
                    return o2.sortWeight < o1.sortWeight ? -1 : 1;
                }
            });
            results.values = filterdData;

            return results;
        }

        public void getSortWeight(TripItem sortItem, SearchFilterDataItem filterItem) {
            final int BOOK_NOW_WEIGHT = 200000;
            final int ORIGIN_WEIGHT = 100000;
            final int DESTINATION_WEIGHT = 50000;
            final float TIME_WEIGHT = 10;
            final int RATING_WEIGHT = 100;
            final int SEATS_WEIGHT = 200;

            float sortWeight = 0;

            // Hired Drivers at the top
            if(sortItem.is_enabled_booknow)
                sortWeight += BOOK_NOW_WEIGHT;

            // Origin Strongest weight calculated as difference in Lat/Lng
            if(sortItem.origin_title.contains(filterItem.origin_text))
                sortWeight += ORIGIN_WEIGHT;

            // Destination next Strongest weight calculated as difference in Lat/Lng
            if(sortItem.destination_title.contains(filterItem.destination_text))
                sortWeight += DESTINATION_WEIGHT;

            // Time next strongest weight
            if(sortItem.start_time!=null && !sortItem.start_time.contains("00:00:00")) {
                float clock = (DateTimeHelper.parseDate(sortItem.start_time, DateTimeHelper.DATE_SERVER_FORMAT).getTime() % 86400000) / 60000.0f;
                sortWeight += (1500 - clock) * TIME_WEIGHT;
            }

            // Seats taken next strongest weight
            if(sortItem.seats_available != 0 && sortItem.seats_total!=0) {
                sortWeight += (sortItem.seats_total - sortItem.seats_available) * SEATS_WEIGHT;
            }

            // Rating next strongest weight
            if(sortItem.driver.rating!=0) {
                sortWeight += (sortItem.driver.rating - 2.5) * RATING_WEIGHT;
            }

            sortItem.sortWeight = sortWeight;
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
