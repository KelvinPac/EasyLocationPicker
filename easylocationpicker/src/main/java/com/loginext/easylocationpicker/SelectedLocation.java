package com.loginext.easylocationpicker;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectedLocation implements Parcelable {

    private double selectedLatitude;
    private double selectedLongitude;
    private String selectedAddress;
    private boolean isGPSLocation;

    public SelectedLocation() {
    }

    protected SelectedLocation(Parcel in) {
        selectedLatitude = in.readDouble();
        selectedLongitude = in.readDouble();
        selectedAddress = in.readString();
        isGPSLocation = in.readByte() != 0;
    }

    public static final Creator<SelectedLocation> CREATOR = new Creator<SelectedLocation>() {
        @Override
        public SelectedLocation createFromParcel(Parcel in) {
            return new SelectedLocation(in);
        }

        @Override
        public SelectedLocation[] newArray(int size) {
            return new SelectedLocation[size];
        }
    };

    public double getSelectedLatitude() {
        return selectedLatitude;
    }

    public void setSelectedLatitude(double selectedLatitude) {
        this.selectedLatitude = selectedLatitude;
    }

    public double getSelectedLongitude() {
        return selectedLongitude;
    }

    public void setSelectedLongitude(double selectedLongitude) {
        this.selectedLongitude = selectedLongitude;
    }

    public String getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(String selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public boolean isGPSLocation() {
        return isGPSLocation;
    }

    public void setGPSLocation(boolean GPSLocation) {
        isGPSLocation = GPSLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(selectedLatitude);
        dest.writeDouble(selectedLongitude);
        dest.writeString(selectedAddress);
        dest.writeByte((byte) (isGPSLocation ? 1 : 0));
    }

    @Override
    public String toString() {
        return "SelectedLocation{" +
                "selectedLatitude=" + selectedLatitude +
                ", selectedLongitude=" + selectedLongitude +
                ", selectedAddress='" + selectedAddress + '\'' +
                ", isGPSLocation=" + isGPSLocation +
                '}';
    }
}
