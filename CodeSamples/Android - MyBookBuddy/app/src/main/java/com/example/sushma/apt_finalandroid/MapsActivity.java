package com.example.sushma.apt_finalandroid;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
        {
    private GoogleMap mMap;
    double latit;
    double longit;
    String titleloc;
    ArrayList<String> imageURLs = new ArrayList<String>();
    ArrayList<String> imageCaps = new ArrayList<String>();
    ArrayList<String> imageLatitude = new ArrayList<String>();
    ArrayList<String> imageLongitude = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        imageCaps = intent.getStringArrayListExtra("imageCaps");
        imageLatitude = intent.getStringArrayListExtra("imageLatitude");
        imageLongitude = intent.getStringArrayListExtra("imageLongitude");

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (int i = 0; i < imageCaps.size(); i++) {
            latit = Double.parseDouble(imageLatitude.get(i));
            longit = Double.parseDouble(imageLongitude.get(i));
            titleloc = imageCaps.get(i);
            MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latit, longit)).title(titleloc);
            mMap.addMarker(marker);
            if(i == 0){
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(imageLatitude.get(i)),
                                Double.parseDouble(imageLongitude.get(i)))).zoom(15).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }
        }

    }
}
