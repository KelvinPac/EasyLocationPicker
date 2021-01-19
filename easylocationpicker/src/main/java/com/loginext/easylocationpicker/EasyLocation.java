package com.loginext.easylocationpicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.loginext.easylocationpicker.Constants.EXTRA_LOCATION_PICKER;
import static com.loginext.easylocationpicker.Constants.EXTRA_LOCATION_RESULTS_FAILED;
import static com.loginext.easylocationpicker.Constants.EXTRA_LOCATION_RESULTS_SUCCESS;
import static com.loginext.easylocationpicker.EasyLocationPickerActivity.LOCATION_REQUEST_CODE;

public class EasyLocation implements Parcelable {

    private String placesApiKey;
    private Context context;
    private boolean showCurrentLocation;
    private boolean useGeoCoder;
    private boolean setResultOnBackPressed;
    private Location location;
    private EasyLocationCallbacks callbacks;
    private Activity activity;
    private Fragment fragment;

    private AddressResultReceiver2 resultReceiver;

    private EasyLocation(Builder builder) {
        setPlacesApiKey(builder.placesApiKey);
        setContext(builder.context);
        setShowCurrentLocation(builder.showCurrentLocation);
        setUseGeoCoder(builder.useGeoCoder);
        setCallbacks(builder.callbacks);
        setSetResultOnBackPressed(builder.setResultOnBackPressed);
        setLocation(builder.location);
        setActivity(builder.activity);
        setFragment(builder.fragment);

        openActivity(this);
    }

    private EasyLocation(GeoCoderBuilder builder) {

        resultReceiver = new AddressResultReceiver2(new Handler());
        resultReceiver.setCallBacks(builder.callbacks);

        //https://developer.android.com/training/location/display-address#java
        Intent intent = new Intent(builder.context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, builder.location);
        builder.context.startService(intent);

    }


    protected EasyLocation(Parcel in) {
        placesApiKey = in.readString();
        showCurrentLocation = in.readByte() != 0;
        useGeoCoder = in.readByte() != 0;
        setResultOnBackPressed = in.readByte() != 0;
        location = in.readParcelable(Location.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placesApiKey);
        dest.writeByte((byte) (showCurrentLocation ? 1 : 0));
        dest.writeByte((byte) (useGeoCoder ? 1 : 0));
        dest.writeByte((byte) (setResultOnBackPressed ? 1 : 0));
        dest.writeParcelable(location, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EasyLocation> CREATOR = new Creator<EasyLocation>() {
        @Override
        public EasyLocation createFromParcel(Parcel in) {
            return new EasyLocation(in);
        }

        @Override
        public EasyLocation[] newArray(int size) {
            return new EasyLocation[size];
        }
    };

    private void openActivity(EasyLocation easyLocation) {
        if (!Utils.isMapsKeyAvailable(getContext())) {
            //no maps key in apps manifest
            if (callbacks != null) {
                String reason = getContext().getString(R.string.easylocation_no_map_keys);
                callbacks.onFailed(reason);
            }
        } else if (TextUtils.isEmpty(easyLocation.placesApiKey)) {
            //supplied places api key is empty
            if (callbacks != null) {
                String reason = getContext().getString(R.string.easylocation_empty_places_api_key);
                callbacks.onFailed(reason);
            }
        } else {

            Intent intent = new Intent(getContext(), EasyLocationPickerActivity.class);
            intent.putExtra(EXTRA_LOCATION_PICKER, easyLocation);
            if (activity !=null){
                activity.startActivityForResult(intent,LOCATION_REQUEST_CODE);
            }else if (fragment !=null){
                fragment.startActivityForResult(intent,LOCATION_REQUEST_CODE);
            }

        }

    }

    protected String getPlacesApiKey() {
        return placesApiKey;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    private EasyLocationCallbacks getCallbacks() {
        return callbacks;
    }

    private void setPlacesApiKey(String placesApiKey) {
        this.placesApiKey = placesApiKey;
    }

    protected boolean isShowCurrentLocation() {
        return showCurrentLocation;
    }

    private void setShowCurrentLocation(boolean showCurrentLocation) {
        this.showCurrentLocation = showCurrentLocation;
    }

    protected boolean isUseGeoCoder() {
        return useGeoCoder;
    }

    private void setUseGeoCoder(boolean useGeoCoder) {
        this.useGeoCoder = useGeoCoder;
    }

    private void setCallbacks(EasyLocationCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    protected boolean isSetResultOnBackPressed() {
        return setResultOnBackPressed;
    }

    private void setSetResultOnBackPressed(boolean setResultOnBackPressed) {
        this.setResultOnBackPressed = setResultOnBackPressed;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Process location selection results and fire appropriate callbacks
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //todo handle when callbacks is null

        //check intent data
        if (data == null) {

            if (callbacks != null) {
                String reason = getContext().getString(R.string.easylocation_load_location_error);
                callbacks.onFailed(reason);
            }

            return;
        }


        //get location
        if (requestCode == EasyLocationPickerActivity.LOCATION_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                //location results received

                if (callbacks != null) {

                    if (data.hasExtra(EXTRA_LOCATION_RESULTS_SUCCESS)) {
                        SelectedLocation selectedLocation = data.getParcelableExtra(EXTRA_LOCATION_RESULTS_SUCCESS);
                        callbacks.onSuccess(selectedLocation);
                    } else {
                        String reason = getContext().getString(R.string.easylocation_load_location_error);
                        callbacks.onFailed(reason);
                    }

                }
            } else {
                //location not received
                if (callbacks != null) {

                    String reason;

                    if (data.hasExtra(EXTRA_LOCATION_RESULTS_FAILED)) {
                        reason = data.getStringExtra(EXTRA_LOCATION_RESULTS_FAILED);
                    } else {
                        reason = getContext().getString(R.string.easylocation_load_location_error);
                    }

                    callbacks.onFailed(reason);
                }

            }
        }
    }

    public static class Builder {
        private String placesApiKey;
        private Context context;
        private Activity activity;
        private Fragment fragment;
        private boolean showCurrentLocation = true;
        private boolean useGeoCoder = true;
        private boolean setResultOnBackPressed;
        private Location location;
        private EasyLocationCallbacks callbacks;

        public Builder(Activity activity, String googlePlacesApiKey) {
            this.activity = activity;
            this.context = activity;
            placesApiKey = googlePlacesApiKey;
        }

        public Builder(Fragment fragment, String googlePlacesApiKey) {
            this.fragment = fragment;
            this.context = fragment.getActivity();
            placesApiKey = googlePlacesApiKey;
        }


        /**
         * Get user current location and set on the map as default location
         */
        public Builder showCurrentLocation(boolean val) {
            showCurrentLocation = val;
            return this;
        }

        /**
         * Geo code selected gps coordinates to human readable format
         * i.e address, street, town, country
         */
        public Builder useGeoCoder(boolean val) {
            useGeoCoder = val;
            return this;
        }

        /**
         * Call back to receive back results
         */
        public Builder setCallbacks(EasyLocationCallbacks val) {
            callbacks = val;
            return this;
        }

        /**
         * Return available location results when user clicks back canceling location selection
         */
        public Builder setResultOnBackPressed(boolean val) {
            setResultOnBackPressed = val;
            return this;
        }

        /**
        * Use this as default location. If available it overrides @ showCurrentLocation */
        public Builder withLocation(Location val) {
            location = val;
            return this;
        }

        public EasyLocation build() {
            return new EasyLocation(this);
        }
    }

    public static class GeoCoderBuilder {

        private Location location;
        private Context context;
        private GeoCoderLocationCallbacks callbacks;

        public GeoCoderBuilder(Context context, Location location, GeoCoderLocationCallbacks callbacks) {
            this.context = context;
            this.location = location;
            this.callbacks = callbacks;
        }


        public EasyLocation build() {
            return new EasyLocation(this);
        }
    }

}
