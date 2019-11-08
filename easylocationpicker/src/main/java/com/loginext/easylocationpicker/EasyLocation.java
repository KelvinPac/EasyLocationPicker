package com.loginext.easylocationpicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Toast;

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
    private boolean showConfirmDialog;
    private EasyLocationCallbacks callbacks;


    private EasyLocation(Builder builder) {
        setPlacesApiKey(builder.placesApiKey);
        setContext(builder.context);
        setShowCurrentLocation(builder.showCurrentLocation);
        setUseGeoCoder(builder.useGeoCoder);
        setCallbacks(builder.callbacks);
        setSetResultOnBackPressed(builder.setResultOnBackPressed);
        setShowConfirmDialog(builder.showConfirmDialog);

        openActivity(this);
    }

    protected EasyLocation(Parcel in) {
        placesApiKey = in.readString();
        showCurrentLocation = in.readByte() != 0;
        useGeoCoder = in.readByte() != 0;
        setResultOnBackPressed = in.readByte() != 0;
        showConfirmDialog = in.readByte() != 0;
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
        if (!Utils.isMapsKeyAvailable(getContext())){
            //no maps key in apps manifest
            if (callbacks !=null){
                String reason  = getContext().getString(R.string.easylocation_no_map_keys);
                callbacks.onFailed(reason);
            }else {
                Toast.makeText(context, R.string.easylocation_no_map_keys, Toast.LENGTH_SHORT).show();
            }
        }else if (TextUtils.isEmpty(easyLocation.placesApiKey)){
            //supplied places api key is empty
            if (callbacks !=null){
                String reason  = getContext().getString(R.string.easylocation_empty_places_api_key);
                callbacks.onFailed(reason);
            }else {
                Toast.makeText(context, R.string.easylocation_empty_places_api_key, Toast.LENGTH_SHORT).show();
            }
        }else {

            Intent intent = new Intent(getContext(),EasyLocationPickerActivity.class);
            intent.putExtra(EXTRA_LOCATION_PICKER,easyLocation);
            Activity activity = (Activity) getContext();
            //getContext().startActivity(intent);
            activity.startActivityForResult(intent,LOCATION_REQUEST_CODE);

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

    protected boolean isShowConfirmDialog() {
        return showConfirmDialog;
    }

    private void setShowConfirmDialog(boolean showConfirmDialog) {
        this.showConfirmDialog = showConfirmDialog;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placesApiKey);
        dest.writeByte((byte) (showCurrentLocation ? 1 : 0));
        dest.writeByte((byte) (useGeoCoder ? 1 : 0));
        dest.writeByte((byte) (setResultOnBackPressed ? 1 : 0));
        dest.writeByte((byte) (showConfirmDialog ? 1 : 0));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //todo handle when callbacks is null

        //check intent data
        if (data == null){

            if (callbacks !=null){
                String reason  = getContext().getString(R.string.easylocation_load_location_error);
                callbacks.onFailed(reason);
            }

            return;
        }


        //get location
        if (requestCode == EasyLocationPickerActivity.LOCATION_REQUEST_CODE){

            if (resultCode == RESULT_OK){
                //location results received

                if (callbacks !=null){

                    if (data.hasExtra(EXTRA_LOCATION_RESULTS_SUCCESS)){
                        SelectedLocation selectedLocation = data.getParcelableExtra(EXTRA_LOCATION_RESULTS_SUCCESS);
                        callbacks.onSuccess(selectedLocation);
                    }else {

                        String reason  = getContext().getString(R.string.easylocation_load_location_error);
                        callbacks.onFailed(reason);
                    }

                }
            }

            else {
                //location not received
                if (callbacks !=null){

                    String reason;

                    if (data.hasExtra(EXTRA_LOCATION_RESULTS_FAILED)){
                        reason = data.getStringExtra(EXTRA_LOCATION_RESULTS_FAILED);
                    }else {
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
        private boolean showCurrentLocation = true;
        private boolean useGeoCoder = true;
        private boolean setResultOnBackPressed;
        private boolean showConfirmDialog;
        private EasyLocationCallbacks callbacks;

        public Builder(Context ctx, String googlePlacesApiKey) {
            context = ctx;
            placesApiKey = googlePlacesApiKey;
        }

       /* public Builder placesApiKey(String val) {
            placesApiKey = val;
            return this;
        }*/

        public Builder showCurrentLocation(boolean val) {
            showCurrentLocation = val;
            return this;
        }

        public Builder useGeoCoder(boolean val) {
            useGeoCoder = val;
            return this;
        }

        public Builder setCallbacks(EasyLocationCallbacks val) {
            callbacks = val;
            return this;
        }

        public Builder setResultOnBackPressed(boolean val) {
            setResultOnBackPressed = val;
            return this;
        }

        /*public Builder showConfirmDialog(boolean val) {
            showConfirmDialog = val;
            return this;
        }*/

        public EasyLocation build() {
            return new EasyLocation(this);
        }
    }

}
