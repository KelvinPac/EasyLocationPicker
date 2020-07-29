package co.ke.locationpicker;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loginext.easylocationpicker.EasyLocation;
import com.loginext.easylocationpicker.EasyLocationCallbacks;
import com.loginext.easylocationpicker.SelectedLocation;


public class MainActivity extends AppCompatActivity {

    private EasyLocation easyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Location loc = new Location("dummyprovider");
        loc.setLatitude(-0.533);
        loc.setLongitude(37.45);

        Button pickLocationBtn = findViewById(R.id.btnPickLocation);
        pickLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                easyLocation = new EasyLocation.Builder(MainActivity.this,"")
                        .showCurrentLocation(true)
                        .useGeoCoder(true)
                        .setResultOnBackPressed(false)
                        //.withLocation(loc)
                        .setCallbacks(new EasyLocationCallbacks() {
                            @Override
                            public void onSuccess(SelectedLocation location) {
                                Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailed(String reason) {
                                Toast.makeText(MainActivity.this, reason, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        easyLocation.onActivityResult(requestCode, resultCode, data);
    }

}
