package com.example.currentplacedetailsonmap;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivityCurrentPlace extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    private GoogleMap map;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final float DEFAULT_ZOOM = 16.13570929f;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private Location lastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private TextView latitude;
    private TextView longitude;
    private TextView radius;

    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latitude = (TextView) findViewById(R.id.editLatitude);
        longitude = (TextView) findViewById(R.id.editLongitude);
        radius = (TextView) findViewById(R.id.editRadius);

        latitude.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { updateCoords(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        longitude.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { updateCoords(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        radius.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { updateCoords(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    public void updateCoords(){
        if(latitude.getText().length()>0 && longitude.getText().length()>0 && radius.getText().length()>0) {
            double lat = Double.parseDouble(String.valueOf(latitude.getText()));
            double lon = Double.parseDouble(String.valueOf(longitude.getText()));
            float kmRad = Float.parseFloat(String.valueOf(radius.getText()));
            double zoom = Math.log(18000 / (kmRad / 2)) / Math.log(2);

            if(marker!=null && lastKnownLocation.getLatitude() != lat || lastKnownLocation.getLongitude() != lon) marker.remove();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), (float) zoom));
            marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        map.setOnPoiClickListener(this);
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(this, "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (map == null)  return;

        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);

                lastKnownLocation = null;
                getLocationPermission();
            }
            map.getUiSettings().setScrollGesturesEnabled(false);
            map.getUiSettings().setZoomGesturesEnabled(false);
            map.getUiSettings().setMapToolbarEnabled(false);
        } catch (SecurityException e)  { }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        radius.setText(String.valueOf(0.5));
                        if (task.isSuccessful()) {
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                latitude.setText(String.valueOf(lastKnownLocation.getLatitude()));
                                longitude.setText(String.valueOf(lastKnownLocation.getLongitude()));
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            latitude.setText(String.valueOf(defaultLocation.latitude));
                            longitude.setText(String.valueOf(defaultLocation.longitude));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  { }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
}
