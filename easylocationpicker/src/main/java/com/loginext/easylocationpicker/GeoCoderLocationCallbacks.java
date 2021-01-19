package com.loginext.easylocationpicker;

/**
 * Location selection results callback
 */
public interface GeoCoderLocationCallbacks {

    /** Called with successful results*/
    void onSuccess(String location);

    /** Called with failure reason when location selection failed*/
    void onFailed(String reason);
}
