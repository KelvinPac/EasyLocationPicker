package com.loginext.easylocationpicker;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

class DistanceCalculator {

    protected static boolean isGPSPosition(LatLng midLatLng, Location myCurrentLocation){
        //no gps location
        if (midLatLng == null || myCurrentLocation == null){
          return false;
        }

        //calculate distance
        double locationDistance = distance(midLatLng.latitude, midLatLng.longitude,
                myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude(), "m");

        Log.i("LOC","distance is "+locationDistance);
        //accuracy of 100 meters is assumed to be gps
        return locationDistance < 100;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            switch (unit) {
                case "K":
                    dist = dist * 1.609344;
                    break;
                case "N":
                    dist = dist * 0.8684;
                    break;
                case "m":
                    dist = dist * 1609.344;
                    break;
            }
            return (dist);
        }
    }
}
