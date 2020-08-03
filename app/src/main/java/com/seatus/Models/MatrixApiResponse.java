
package com.seatus.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatrixApiResponse {

    @SerializedName("destination_addresses")
    public List<String> DestinationAddresses;
    @SerializedName("origin_addresses")
    public List<String> OriginAddresses;
    @SerializedName("rows")
    public List<Row> Rows;
    @SerializedName("status")
    public String Status;

    public class Row {

        @SerializedName("elements")
        public List<Element> Elements;

    }

    public class Element {

        @SerializedName("distance")
        public Distance Distance;
        @SerializedName("duration")
        public Duration Duration;
        @SerializedName("status")
        public String Status;

    }

    public class Distance {

        @SerializedName("text")
        public String Text;
        @SerializedName("value")
        public Long Value;

    }

    public class Duration {

        @SerializedName("text")
        public String Text;
        @SerializedName("value")
        public Long Value;


    }


}
