/*

Copyright 2015 Akexorcist

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.akexorcist.googledirection.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akexorcist on 11/29/15 AD.
 */

@SuppressWarnings("WeakerAccess")
public class Route implements Parcelable {

    @SerializedName("bounds")
    private Bound bound;
    private String copyrights;
    @SerializedName("legs")
    private List<Leg> legList;
    @SerializedName("overview_polyline")
    private RoutePolyline overviewPolyline;
    private String summary;
    private Fare fare;
    @SerializedName("warnings")
    private List<String> warningList;

    public boolean isSelected = false;
    public Long popularity = null;

    public Route() {
    }

    protected Route(Parcel in) {
        bound = in.readParcelable(Bound.class.getClassLoader());
        copyrights = in.readString();
        summary = in.readString();
        fare = in.readParcelable(Fare.class.getClassLoader());
        warningList = in.createStringArrayList();
    }

    public Bound getBound() {
        return bound;
    }

    public LatLngBounds getLatLngBounds() {
        LatLngBounds LatLngbounds = LatLngBounds.builder().include(new LatLng(bound.getNortheastCoordination().getLatitude(), bound.getNortheastCoordination().getLongitude()))
                .include(new LatLng(bound.getSouthwestCoordination().getLatitude(), bound.getSouthwestCoordination().getLongitude())).build();
        return LatLngbounds;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public List<Leg> getLegList() {
        return legList;
    }

    public void setLegList(List<Leg> legList) {
        this.legList = legList;
    }

    public RoutePolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(RoutePolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Fare getFare() {
        return fare;
    }

    public void setFare(Fare fare) {
        this.fare = fare;
    }

    public List<String> getWarningList() {
        return warningList;
    }

    public void setWarningList(List<String> warningList) {
        this.warningList = warningList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bound, flags);
        dest.writeString(copyrights);
        dest.writeString(summary);
        dest.writeParcelable(fare, flags);
        dest.writeStringList(warningList);
    }

    public Info getRouteDistance() {
        int distance = 0;
        for (Leg leg : getLegList()) {
            distance += Integer.parseInt(leg.getDistance().getValue());
        }
        Info info = new Info();
        info.setValue(String.valueOf(distance));

        int km = distance / 1000;
        int meters = distance % 1000;
        if (km > 0)
            info.setText(String.format("%d km %d meters", km, meters));
        else
            info.setText(String.format("%d meters", meters));

        return info;
    }

    public TimeDistanceInfo getRouteTimeDistance() {
        int distance = 0;
        int time = 0;
        String durationText, timeText;

        for (Leg leg : getLegList()) {
            distance += Integer.parseInt(leg.getDistance().getValue());
            time += Integer.parseInt(leg.getDuration().getValue());
        }

        int miles = (int) (distance / 1609.34);
        int metersOverMiles = (int) (distance % 1609.34);
        int feetsOverMiles = (int) (metersOverMiles / 3.2808);

        if (miles > 0)
            durationText = String.format("%d miles", miles);
//            durationText = String.format("%d miles %d feets", miles, feetsOverMiles);
        else
            durationText = String.format("Less then a Mile");
//            durationText = String.format("%d feets", feetsOverMiles);

        return new TimeDistanceInfo(distance, durationText, time, secToTime(time));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public ArrayList<LatLng> getAllCoordinates() {
        ArrayList<LatLng> listCoordinates = new ArrayList<>();
        for (Leg leg : getLegList())
            listCoordinates.addAll(leg.getDirectionPoint());
        return listCoordinates;
    }

    /*
    tolerance is the max difference required between each coordinates in meters
     */
    public ArrayList<LatLng> getAllCoordinates(int tolerance) {
        ArrayList<LatLng> listCoordinates = getAllCoordinates();
        for (int i = 0; i < listCoordinates.size() - 1; i++) {
            while (getDistanceLatLng(listCoordinates.get(i), listCoordinates.get(i + 1)) > tolerance) {
                listCoordinates.add(i + 1, getCentreLatLng(listCoordinates.get(i), listCoordinates.get(i + 1)));
            }
        }
        return listCoordinates;
    }

    private LatLng getCentreLatLng(LatLng latLng1, LatLng latlng2) {
        return new LatLng((latLng1.latitude + latlng2.latitude) / 2, (latLng1.longitude + latlng2.longitude) / 2);
    }

    private int getDistanceLatLng(LatLng latLng1, LatLng latLng2) {
        Location loc1 = new Location("dummy");
        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);

        Location loc2 = new Location("dummy2");
        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);

        return (int) loc1.distanceTo(loc2);
    }


    public class TimeDistanceInfo {
        int distanceValue;
        String distanceText;

        int timeValue;
        String timeText;

        public TimeDistanceInfo(int durationValue, String durationText, int timeValue, String timeText) {
            this.distanceValue = durationValue;
            this.distanceText = durationText;
            this.timeValue = timeValue;
            this.timeText = timeText;
        }

        public int getDistanceValue() {
            return distanceValue;
        }

        public void setDistanceValue(int distanceValue) {
            this.distanceValue = distanceValue;
        }

        public String getDistanceText() {
            return distanceText;
        }

        public void setDistanceText(String distanceText) {
            this.distanceText = distanceText;
        }

        public int getTimeValue() {
            return timeValue;
        }

        public void setTimeValue(int timeValue) {
            this.timeValue = timeValue;
        }

        public String getTimeText() {
            return timeText;
        }

        public void setTimeText(String timeText) {
            this.timeText = timeText;
        }
    }

    String secToTime(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        if (minutes < 1)
            return "1 Min";
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;
            return String.format("%02d hour %02d Min", hours, minutes);
        } else {
            return String.format("%02d Min", minutes);
        }
    }

}
