
package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlacesResultsNames implements Serializable {

    @SerializedName("html_attributions")
    public List<Object> mHtmlAttributions;
    @SerializedName("next_page_token")
    public String mNextPageToken;
    @SerializedName("results")
    public List<PlacesResults> mResults;
    @SerializedName("status")
    public String mStatus;

    public class PlacesResults implements Serializable {

        @SerializedName("geometry")
        public GeomentryClass mGeometry;
        @SerializedName("icon")
        public String mIcon;
        @SerializedName("id")
        public String mId;
        @SerializedName("name")
        public String mName;
        @SerializedName("photos")
        public List<Object> mPhotos;
        @SerializedName("place_id")
        public String mPlaceId;
        @SerializedName("reference")
        public String mReference;
        @SerializedName("scope")
        public String mScope;
        @SerializedName("types")
        public List<String> mTypes;
        @SerializedName("vicinity")
        public String mVicinity;

    }

    public class GeomentryClass implements Serializable {

        @SerializedName("location")
        public LocationClass mLocation;

        public class LocationClass {
            @SerializedName("lat")
            public Double lat;
            @SerializedName("lng")
            public Double lng;

            public String getLatLng() {
                return lat + "," + lng;
            }
        }
    }

}
