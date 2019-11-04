package com.loginext.easylocationpicker;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

import mumayank.com.airlocationlibrary.AirLocation;

import static com.loginext.easylocationpicker.Constants.EXTRA_LOCATION_PICKER;
import static com.loginext.easylocationpicker.Constants.EXTRA_LOCATION_RESULTS_FAILED;
import static com.loginext.easylocationpicker.Constants.EXTRA_LOCATION_RESULTS_SUCCESS;


public class EasyLocationPickerActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = EasyLocationPickerActivity.class.getSimpleName();
    private GoogleMap mMap;

    public static final int LOCATION_REQUEST_CODE = 418;
    public static final String EXTRA_LATITUDE =  "EXTRA_LATITUDE_DATA";
    public static final String EXTRA_LONGITUDE = "EXTRA_LONGITUDE_DATA";

    /* 1 -> get current location when activity starts
     *  2 -> get current location when location button clicked
     * todo -> add map click events
     * todo -> show current location dot if available. center map drag here
     * todo 3 -> get location reverse geo code
     * todo 4 -> show address or coordinates after getting location or searching
     * todo 5 -> implement dragging map with center icon all times no more markers
     * todo 6 -> on click okay show confirmation dialog and return location */

    private AirLocation airLocation;
    private FloatingActionButton floatingActionButtonLocation,fabBtnAccept;
    private TextView latitude, longitude, address, locationTitle;
    private LatLng midLatLng;
    private ProgressBar progressBar;

    //https://developer.android.com/training/location/display-address#java
    private AddressResultReceiver resultReceiver;
    EasyLocation easyLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_location_picker);

        floatingActionButtonLocation = findViewById(R.id.btnFloatingActionLocation);
        fabBtnAccept = findViewById(R.id.fabBtnAccept);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        address = findViewById(R.id.address);
        progressBar = findViewById(R.id.progressBar);
        locationTitle = findViewById(R.id.coordinates_tittle);

        floatingActionButtonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
        fabBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackLocation(true);
            }
        });


        easyLocation = getIntent().getParcelableExtra(EXTRA_LOCATION_PICKER);

        resultReceiver = new AddressResultReceiver(new Handler());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize Places.
        //todo remove and add dynamically
        Places.initialize(getApplicationContext(), easyLocation.getPlacesApiKey());

        if (easyLocation.isUseGeoCoder()){
            locationTitle.setText(R.string.easylocation_address);
        }else {
            locationTitle.setText(R.string.easylocation_coordinates);
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        //todo remove and add dynamically
        autocompleteFragment.setCountry("ke");
        autocompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        ));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                // Toast.makeText(EasyLocationPickerActivity.this, place.getAddress(), Toast.LENGTH_SHORT).show();

                if (place.getLatLng() != null) {

                    newLocationSelected(place.getLatLng());
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(EasyLocationPickerActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        if (easyLocation.isShowCurrentLocation()){
            //get current user location
            getCurrentLocation();
        }else {
            floatingActionButtonLocation.setVisibility(View.GONE);
        }


       /* if (getMapsKey()){
            showToast("Map key found");
        }else {
            showToast("No map key");
        }*/
    }

    /*
    * check if google maps key is available */
    private boolean getMapsKey(){
        try {
            ApplicationInfo app = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;

            for (String key: bundle.keySet())
            {
                if (key.equals("com.google.android.geo.API_KEY")){
                    Log.d("TEST","MAPS KEY FOUND");
                    Log.d("TEST","MAPS KEY: "+bundle.getString("com.google.android.geo.API_KEY"));
                    return true;
                }
            }


        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (easyLocation.isSetResultOnBackPressed()){
            sendBackLocation(false);
        }else {
           // super.onBackPressed();
           sendBackUserCancelledOperation();
        }

    }

    private void sendBackLocation(boolean userClicked){

        if (midLatLng !=null){
            //todo show dialog to confirm selected location

            //intent.putExtra(EXTRA_LATITUDE, String.valueOf(midLatLng.latitude));
            //intent.putExtra(EXTRA_LONGITUDE, String.valueOf(midLatLng.longitude));

            SelectedLocation selectedLocation = new SelectedLocation();
            selectedLocation.setSelectedAddress("");
            selectedLocation.setSelectedLatitude(midLatLng.latitude);
            selectedLocation.setSelectedLongitude(midLatLng.longitude);
            selectedLocation.setGPSLocation(false);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_LOCATION_RESULTS_SUCCESS,selectedLocation);
            setResult(RESULT_OK, intent);
            finish();

        }else {

           sendBackUserCancelledOperation();
        }

    }

    private void sendBackUserCancelledOperation() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_LOCATION_RESULTS_FAILED,getString(R.string.easylocation_user_cancelled));
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void getCurrentLocation() {
        // Fetch location simply like this whenever you need
        airLocation = new AirLocation(this, true, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NonNull Location location) {
                // user location acquired
                LatLng selectedPlace = new LatLng(location.getLatitude(), location.getLongitude());
                newLocationSelected(selectedPlace);
            }

            @Override
            public void onFailed(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {
                // todo do something
            }
        });

    }

    /*
     * Called when user location is automatically captured and when user clicks map
     * show new user location on map*/
    private void newLocationSelected(LatLng selectedPlace) {
        mMap.clear();
        //Toast.makeText(EasyLocationPickerActivity.this, place.getLatLng().toString(), Toast.LENGTH_SHORT).show();

        // mMap.addMarker(new MarkerOptions().position(selectedPlace).title("Place"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedPlace,16));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlace, 16));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.animateCamera(zoomingLocation());

        //listen for map interactions
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                midLatLng = mMap.getCameraPosition().target;

                if (easyLocation.isUseGeoCoder()){
                    //get location address
                    if (Geocoder.isPresent()) {
                        getGeoCoderAddress(midLatLng);
                    } else {
                        showCoordinatesOnly();
                        Toast.makeText(EasyLocationPickerActivity.this, R.string.easylocation_no_geocoder_available, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    //show coordinates
                    showCoordinatesOnly();
                }

            }
        });


        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

                if (!easyLocation.isUseGeoCoder()){
                    latitude.setVisibility(View.GONE);
                    longitude.setVisibility(View.GONE);
                    address.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
                //show loading progress as we fetch address
                address.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Toast.makeText(EasyLocationPickerActivity.this, " "+latLng.toString(), Toast.LENGTH_SHORT).show();
                //move camera to new user selected position
                newLocationSelected(latLng);
            }
        });
    }

    private void getGeoCoderAddress(LatLng currentLatLng) {

        Location targetLocation = new Location("");//provider name is unnecessary
        targetLocation.setLatitude(currentLatLng.latitude);
        targetLocation.setLongitude(currentLatLng.longitude);

        //https://developer.android.com/training/location/display-address#java
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, targetLocation);
        startService(intent);
    }

    private CameraUpdate zoomingLocation() {
        return CameraUpdateFactory.newLatLngZoom(new LatLng(-1.2833, 36.8167), 13);
    }

    // override and call airLocation object's method by the same name
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    // override and call airLocation object's method by the same name
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void text(View view) {
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
    }

    private void displayAddressOutput(String addressOutput) {
        locationTitle.setText(R.string.easylocation_address);
        address.setText(addressOutput);

        //show address and hide location position
        progressBar.setVisibility(View.GONE);
        latitude.setVisibility(View.GONE);
        longitude.setVisibility(View.GONE);
        address.setVisibility(View.VISIBLE);
    }

    private void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    //show data from getting address service
    class AddressResultReceiver extends ResultReceiver {

        //https://developer.android.com/training/location/display-address#java

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

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

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                displayAddressOutput(addressOutput);
            } else {
                //show location coordinates
                //show location position as we fetch address
                showCoordinatesOnly();

                showToast(addressOutput);
            }

        }
    }

    private void showCoordinatesOnly(){
        latitude.setVisibility(View.VISIBLE);
        longitude.setVisibility(View.VISIBLE);
        address.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        locationTitle.setText(R.string.easylocation_coordinates);
        latitude.setText(String.valueOf(midLatLng.latitude));
        longitude.setText(String.valueOf(midLatLng.longitude));
    }

}
