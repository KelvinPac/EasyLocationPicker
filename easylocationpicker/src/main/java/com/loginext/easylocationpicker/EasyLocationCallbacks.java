package com.loginext.easylocationpicker;

/**
 * Location selection results callback
 */
public interface EasyLocationCallbacks {

    /** Called with successful results*/
    void onSuccess(SelectedLocation location);

    /** Called with failure reason when location selection failed*/
    void onFailed(String reason);
}
