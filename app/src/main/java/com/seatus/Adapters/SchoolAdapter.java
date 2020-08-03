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

import com.seatus.Models.SchoolItem;
import com.seatus.R;

import java.util.ArrayList;
import java.util.List;

public class SchoolAdapter<T> extends BaseAdapter implements Filterable {

    List<SchoolItem> data = new ArrayList<>();
    List<SchoolItem> defaultData = new ArrayList<>();
    Context context;
    static final int Layout = R.layout.item_text;  // Set Layout

    public SchoolAdapter(Context context, List<SchoolItem> data) {
        this.context = context;
        this.data.addAll(data);
        this.defaultData.addAll(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        SchoolItem item = data.get(position);

        if (view == null) {
            LayoutInflater inflator = ((Activity) context).getLayoutInflater();
            view = inflator.inflate(Layout, null);
        }

        TextView text = (TextView) view.findViewById(R.id.text1);
        text.setText(item.school + "");

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
                    data.addAll((ArrayList<SchoolItem>) results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<SchoolItem> FilteredArrayNames = new ArrayList<SchoolItem>();
                ArrayList<SchoolItem> FilteredArraySchoolStarts = new ArrayList<SchoolItem>();
                ArrayList<SchoolItem> FilteredArraySchools = new ArrayList<SchoolItem>();
                ArrayList<SchoolItem> FilteredArrayLocations = new ArrayList<SchoolItem>();

                constraint = constraint.toString().toLowerCase();

                // 1/3/2019 Steven Suggett: Improved College Autocomplete Suggestions -- START --

                // smart search for "U of *" abbreviations
                // autocompletes searches starting with any amount of the word "univerisity"
                // EX: "u of I", "Uni o M", "Uni Illi"
                CharSequence shortcut = "university of ";

                // find if the string should be autocompleted

                String[] shortcutted = constraint.toString().toLowerCase().split(" ");
                if((shortcut.toString().startsWith(shortcutted[0])) && (constraint.toString().contains(" "))) {
                    // split the string at the correct position and redefine the constraint accordingly
                    if(constraint.toString().contains(" o ") ||
                            constraint.toString().contains(" of "))
                        constraint = shortcut.toString() +
                                constraint.subSequence(shortcutted[0].length() + shortcutted[1].length() + 2,
                                        constraint.length()).toString();
                    else if(constraint.toString().contains(" o") ||
                            constraint.toString().contains(" of"))
                        constraint = shortcut.toString() +
                                constraint.subSequence(shortcutted[0].length() + shortcutted[1].length() + 1,
                                        constraint.length()).toString();
                    else
                        constraint = shortcut.toString() + constraint.subSequence(shortcutted[0].length() + 1,
                                constraint.length()).toString();
                }

                // perform your search here using the searchConstraint String.
                // Sub Arrays are used to order the search results based on relevance
                String firstLetters = "";

                for (int i = 0; i < defaultData.size(); i++) {

                    // First array contains schools that start with the phrase
                    // and schools that start "University of " + the phrase
                    if (defaultData.get(i).school.toLowerCase().startsWith(constraint.toString()) ||
                            (defaultData.get(i).school.toLowerCase().startsWith(shortcut.toString()) &&
                                    defaultData.get(i).school.toLowerCase().startsWith(constraint.toString(), shortcut.length()))) {
                        FilteredArraySchoolStarts.add(defaultData.get(i));
                    }
                    // Second Array contains schools that contain the phrase
                    else if (defaultData.get(i).school.toLowerCase().contains(constraint.toString())) {
                        FilteredArraySchools.add(defaultData.get(i));
                    }
                    // Third array contains schools located in the state or city of the phrase
                    else if (defaultData.get(i).school.toLowerCase().contains(constraint.toString()) ||
                            defaultData.get(i).state.toLowerCase().contains(constraint.toString()) ||
                            defaultData.get(i).city.toLowerCase().replaceAll(" ", "").contains(constraint.toString())) {
                        FilteredArrayLocations.add(defaultData.get(i));
                    }
                    // Search using the first letter of each college name
                    else
                    {
                        // Split the college name up at the spaces
                        StringBuffer sb = new StringBuffer();
                        String[] splitNames = defaultData.get(i).school.toLowerCase().split(" ");

                        // Extract the first letter of each word
                        for (int j = 0; j < splitNames.length; j++) {
                            // Exclude 1 character symbols such as &, and well as avoid empty strings
                            if(splitNames[j].length() > 1)
                                sb.append(splitNames[j].charAt(0));
                        }
                        firstLetters = sb.toString();

                        // Perform the check and add it to the Schools array
                        if(firstLetters.contains(constraint.toString())){
                            FilteredArraySchools.add(defaultData.get(i));
                        }
                    }
                }

                // Combine the arrays into one
                FilteredArrayNames.addAll(FilteredArraySchoolStarts);
                FilteredArrayNames.addAll(FilteredArraySchools);
                FilteredArrayNames.addAll(FilteredArrayLocations);

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                Log.e("VALUES", results.values.toString());

                return results;
            }
            // 1/3/2019 Steven Suggett: Improved College Autocomplete Suggestions ----- END ----
        };

        return filter;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public SchoolItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


}
