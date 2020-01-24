package com.loginext.easylocationpicker;

/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::                                                                         :*/
/*::  This routine calculates the distance between two points (given the     :*/
/*::  latitude/longitude of those points). It is being used to calculate     :*/
/*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
/*::                                                                         :*/
/*::  Definitions:                                                           :*/
/*::    South latitudes are negative, east longitudes are positive           :*/
/*::                                                                         :*/
/*::  Passed to function:                                                    :*/
/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
/*::    unit = the unit you desire for results                               :*/
/*::           where: 'M' is statute miles (default)                         :*/
/*::                  'm' is meters                                          :*/
/*::                  'K' is kilometers                                      :*/
/*::                  'N' is nautical miles                                  :*/
/*::  Worldwide cities and other features databases with latitude longitude  :*/
/*::  are available at https://www.geodatasource.com                         :*/
/*::                                                                         :*/
/*::  For enquiries, please contact sales@geodatasource.com                  :*/
/*::                                                                         :*/
/*::  Official Web site: https://www.geodatasource.com                       :*/
/*::                                                                         :*/
/*::           GeoDataSource.com (C) All Rights Reserved 2018                :*/
/*::                                                                         :*/
/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

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
