package com.example.mymap;

import androidx.fragment.app.FragmentActivity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private TextView degree, weather, txtc1, txtcy;
    private Geocoder geocoder;
    private ProgressBar pBar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        degree = findViewById(R.id.degree);
        weather = findViewById(R.id.weather);
        txtc1 = findViewById(R.id.txtc1);
        txtcy = findViewById(R.id.txtcy);
        pBar3 = findViewById(R.id.progressBar);
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

    private void fetchAndUpdateWeather(String lat, String lon) {

        // Generate the service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);

        pBar3.getIndeterminateDrawable().setColorFilter(Color.parseColor("#D7E9F4"), PorterDuff.Mode.MULTIPLY);
        pBar3.setVisibility(View.VISIBLE);
        degree.setVisibility(View.GONE);
        weather.setVisibility(View.GONE);
        txtc1.setVisibility(View.GONE);
        txtcy.setVisibility(View.GONE);

        //Run the request
        service.get("a21a79d3c32d92ebc8f8ee542782377f", lat, lon).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {

                pBar3.setVisibility(View.GONE);
                degree.setVisibility(View.VISIBLE);
                weather.setVisibility(View.VISIBLE);
                txtc1.setVisibility(View.VISIBLE);
                txtcy.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    degree.setText(Double.toString(Math.ceil(response.body().getmMain().getmTemp()) - 273));
                    weather.setText(response.body().getmWeather().get(0).getmDescription());
                } else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d("FAILURE", "Error");
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Nablus and move the camera
        LatLng sydney = new LatLng(32.2227, 35.2621);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Palestine, Nablus"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
        txtc1.setText("Nablus, Palestine");
        txtcy.setText(32.0 + "°, " + 35.0 + "°");
        fetchAndUpdateWeather(Double.toString(32.2227), Double.toString(35.2621));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Setting the new marker
                mMap.clear();
                double x = (latLng.latitude);
                double y = (latLng.longitude);
                LatLng sydney = new LatLng(x, y);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
                txtcy.setText(Math.ceil(x) + "°, " + Math.ceil(y) + "°");

                // Get the address
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(x, y, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String city = null;
                String country = null;
                if (addresses != null && addresses.size() > 0) {
                    addresses.get(0).getAdminArea();
                    city = addresses.get(0).getLocality();
                    country = addresses.get(0).getCountryName();
                }

                if (country != null && country.equals("Israel"))
                    country = "Palestine";

                // Handling the country and city cases
                if (city == null && country == null) {
                    txtc1.setText("Unknown");
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Unknown"));
                } else if (city == null && country != null) {
                    txtc1.setText(country);
                    mMap.addMarker(new MarkerOptions().position(sydney).title(country));
                } else if (country == null && city != null) {
                    txtc1.setText(city);
                    mMap.addMarker(new MarkerOptions().position(sydney).title(city));
                } else {
                    txtc1.setText(city + ", " + country);
                    mMap.addMarker(new MarkerOptions().position(sydney).title(city + ", " + country));
                }

                // get the weather and set them to the text view
                fetchAndUpdateWeather(Double.toString(x), Double.toString(y));
            }
        });
    }
}
