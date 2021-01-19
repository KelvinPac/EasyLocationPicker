package com.loginext.easylocationpicker;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

public class AddressResultReceiver2 extends ResultReceiver {

    private GeoCoderLocationCallbacks coderLocationCallbacks;

    public AddressResultReceiver2(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        String currentAddress;

        if (resultData == null) {
            return;
        }

        // Display the address string
        // or an error message sent from the intent service.
        String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
        if (addressOutput == null) {
            addressOutput = "";
        }

        // displayAddressOutput(addressOutput);
        if (resultCode == Constants.SUCCESS_RESULT) {
            currentAddress = addressOutput;
           coderLocationCallbacks.onSuccess(currentAddress);
        } else {
            //show location coordinates
            //show location position as we fetch address
            coderLocationCallbacks.onFailed(addressOutput);
            Log.d("AddressResultReceiver2",addressOutput);
        }

    }

    public void setCallBacks(GeoCoderLocationCallbacks callbacks) {
        this.coderLocationCallbacks = callbacks;
    }
}
