package com.example.mymap;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView degree, weather, txtc, txtc1, txtcy;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        degree = (TextView)findViewById(R.id.degree);
        weather = (TextView)findViewById(R.id.weather);
        txtc = (TextView)findViewById(R.id.txtc);
        txtc1 = (TextView)findViewById(R.id.txtc1);
        txtcy = (TextView)findViewById(R.id.txtcy);

        geocoder = new Geocoder(this, Locale.getDefault());

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
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(32.2227, 35.2621);
        mMap.addMarker(new MarkerOptions().position(sydney).title("This is Palestine"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng latLng) {

                googleMap.clear();
                double x = latLng.latitude;
                double y = latLng.longitude;

                LatLng sydney = new LatLng(x, y);

                mMap.addMarker(new MarkerOptions().position(sydney).title(""));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));

                txtc.setText(Double.toString(x) + "°");
                txtcy.setText(Double.toString(y) + "°");

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(x, y, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                addresses.get(0).getAdminArea();
                String city = addresses.get(0).getLocality();
                txtc1.setText(city);
            }
        });
    }
}
