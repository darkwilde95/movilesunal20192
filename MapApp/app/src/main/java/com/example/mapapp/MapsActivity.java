package com.example.mapapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocation;
    private PlacesClient placesClient;
    private RequestQueue requestQueue;
    private double lat, lng;
    private int radio;
    private SharedPreferences mPrev;
    public static final int PERMISSION_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mPrev = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        radio = mPrev.getInt("radio", 50);

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
            this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION
                );
            }
        } else {
            startApp();
        }
    }

    private void startApp() {
        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocation.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
            new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        requestQueue.start();
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        updateSearch();
                        Log.i("LOCATION", "===============LOCATION CHANGED===============");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    @Override
                    public void onProviderEnabled(String provider) {}

                    @Override
                    public void onProviderDisabled(String provider) {}
                },
      null
            );
        }

    }

    private void updateSearch() {
        mMap.clear();
        String aux_url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?";
        String params = "inputtype=textquery&language=es-419&fields=name,types,geometry/location";
        String loc = "locationbias=circle:" + radio + "@" + lat + "," + lng + "&input=*";
        String key = "key=" + getString(R.string.google_places_key);
        aux_url += params + "&" + loc + "&" + key;

        GsonRequest request = new GsonRequest(
                aux_url, ApiResponse.class, new Response.Listener<ApiResponse>() {
            @Override
            public void onResponse(ApiResponse response) {
                MarkerOptions placeMarker = new MarkerOptions();
                LatLng placeCoords;
                LatLngBounds.Builder b = new LatLngBounds.Builder();
                for (PlaceInfo place : response.candidates) {
                    if (place.valid()){
                        placeCoords = new LatLng(place.geometry.location.lat, place.geometry.location.lng);
                        placeMarker.position(placeCoords);
                        placeMarker.title(place.name);
                        b.include(placeMarker.getPosition());
                        mMap.addMarker(placeMarker);
                    }
                }
                if (response.candidates.length == 0) {
                    placeCoords = new LatLng(lat, lng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeCoords, 18.5f));
                } else {
                    LatLngBounds bound = b.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 96));
                }
            }
        }, null);
        requestQueue.add(request);

        MarkerOptions marker = new MarkerOptions();
        LatLng current = new LatLng(lat, lng);
        marker.position(current);
        marker.title(getString(R.string.your_are_here));
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(marker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startApp();

                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_permission_text, Toast.LENGTH_SHORT).show();
                    finish();
                }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.preferences) {
            startActivityForResult(new Intent(getApplicationContext(), Settings.class), 0);
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        placesClient = Places.createClient(this);
        checkLocationPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CANCELED) {
            Log.i("RESULT", "======================= CANCEL ========================");
            radio = mPrev.getInt("radio", 50);
            updateSearch();
        }

        if (requestCode == RESULT_OK) {
            Log.i("RESULT", "======================= OK ========================");
        }
    }
}
