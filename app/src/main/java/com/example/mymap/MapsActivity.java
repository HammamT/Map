package com.example.mymap;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.mymap.weatherapi.Weather;
import com.example.mymap.weatherapi.WeatherResponse;
import com.example.mymap.weatherapi.WeatherService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView degree, weather, txtc, txtc1, txtcy;
    Geocoder geocoder;
    Weather weatherr;


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

    private void fetchAndUpdateWeather(String lat, String lon){
        // Generate the service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);

        //Run the request

        service.get("a21a79d3c32d92ebc8f8ee542782377f", lat, lon).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.isSuccessful()) {
                    degree.setText(Double.toString(response.body().getmMain().getmTemp() - 273.15));
                    weather.setText(response.body().getmWeather().get(0).getmDescription());
                }else{
                    return;
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d("", "Error");
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(32.2227, 35.2621);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Nablus"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
        txtc1.setText("Nablus");
        txtc.setText(32.2227 + "°");
        txtcy.setText(35.2621 + "°");
        fetchAndUpdateWeather(Double.toString(32.2227), Double.toString(35.2621));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                double x = latLng.latitude;
                double y = latLng.longitude;
                LatLng sydney = new LatLng(x, y);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
                txtc.setText(Double.toString(x) + "°");
                txtcy.setText(Double.toString(y) + "°");
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(x, y, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addresses.get(0).getAdminArea();
                String city = addresses.get(0).getLocality();

                //Log.d("\n", city + "Hammam \n\n\n\n\n\n\n\n");

                if(city == null){
                    txtc1.setText("Unknown");
                }else {
                    txtc1.setText(city);
                }

                mMap.addMarker(new MarkerOptions().position(sydney).title(city));

                fetchAndUpdateWeather(Double.toString(x), Double.toString(y));
            }
        });
    }
}
