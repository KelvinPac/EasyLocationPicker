package co.ke.locationpicker;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loginext.easylocationpicker.EasyLocation;
import com.loginext.easylocationpicker.EasyLocationCallbacks;
import com.loginext.easylocationpicker.SelectedLocation;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EasyLocation easyLocation = new EasyLocation.Builder(this,"AIzaSyALzTDX4AcHzrSZYPsuUsfdUwExQPCYlRc")
                .showCurrentLocation(true)
                .useGeoCoder(true)
                .setResultOnBackPressed(false)
                .setCallbacks(new EasyLocationCallbacks() {
                    @Override
                    public void onSuccess(SelectedLocation location) {
                        Toast.makeText(requireContext(), location.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(String reason) {
                        Toast.makeText(requireContext(), reason, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }
}
