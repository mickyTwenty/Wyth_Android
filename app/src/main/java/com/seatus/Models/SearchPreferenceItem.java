package com.seatus.Models;

import java.util.ArrayList;

/**
 * Created by rah on 17-Nov-17.
 */

public class SearchPreferenceItem {


    public String title;
    public String identifier;
    public String var_type;
    public boolean checked;
    public String option;

    public ArrayList<PreferenceOptions> options;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SearchPreferenceItem) {
            SearchPreferenceItem prefItem = (SearchPreferenceItem) obj;
            return (identifier.equals(prefItem.identifier) && checked == prefItem.checked);
        } else
            return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public class PreferenceOptions {
        public String label;
        public String value;
        public String ride_preference_id;
        public boolean checked;
    }
}
