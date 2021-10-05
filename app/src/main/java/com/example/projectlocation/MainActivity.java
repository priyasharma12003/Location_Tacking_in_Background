package com.example.projectlocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    SupportMapFragment mSupportMapFragment;
    FusedLocationProviderClient mFusedLocationProviderClient;
    LocationRequest locationRequest;
    Marker marker;
    GoogleMap mmap;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(100);
        locationRequest.setInterval(300);

        latLng = new LatLng(112, 37);

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        // stopping the location tacking
       findViewById(R.id.btnstop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopService(new Intent(MainActivity.this,LocationServiceclass.class));
            }
        });

        mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Toast.makeText(MainActivity.this, "in map ready", Toast.LENGTH_SHORT).show();
                mmap = googleMap;
                marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                startService(new Intent(MainActivity.this, LocationServiceclass.class));
                  getCurrentLocation();

            }
        });


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.FOREGROUND_SERVICE}, 44);

        }
    }


    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());


    }
    LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            setLocationTracing(location);
            super.onLocationResult(locationResult);
        }
    };

    private void setLocationTracing(Location location) {

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(latLng);
        mmap.getUiSettings().setZoomControlsEnabled(true);
        mmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
      //  mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16f));


    }

    @Override
    protected void onPause() {
        startService(new Intent(MainActivity.this,LocationServiceclass.class));
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        startService(new Intent(MainActivity.this,LocationServiceclass.class));
        super.onDestroy();
    }
}







  /*  private void getCurrentLocation() {

        Task<Location> task=mFusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("I am here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            googleMap.addMarker(markerOptions);
                        }
                    });
                }

            }
        });

    }*/

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==44){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }*/
