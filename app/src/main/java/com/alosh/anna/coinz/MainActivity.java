package com.alosh.anna.coinz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {
    private  String tag = "MainActivity";
    private MapView mapView;
    private MapboxMap map;

    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    public String mapData;
    private final String PreferenceFile = "preferenceData";
    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton Wallet = findViewById(R.id.Wallet);
        FloatingActionButton Bank = findViewById(R.id.Bank);
        FloatingActionButton Trophies = findViewById(R.id.Trophies);


        Wallet.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WalletActivity.class)));

        Bank.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BankActivity.class)));

        Trophies.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TrophiesActivity.class)));



        Mapbox.getInstance(this, "pk.eyJ1IjoiczE2MDE4NDciLCJhIjoiY2puMXBoeXplMnNicTNxbzhhYWFmbnhqZyJ9.hTS5UNqpWkpg2Fcy4z4fAQ");
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap)  {
       SharedPreferences FromFile = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
       String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());


       if (FromFile.contains(date)){
           mapData = FromFile.getString(date, "");
           // log.d mesg  onready map data tken from file
           Log.d(tag, "[onMapReady] fromfilemapdata ="+ mapData);
       }
       else{
           // log.d msg onready map data taken from server
           DownloadFileTask download =new DownloadFileTask();
           Log.d(tag, "[onMapReady] date ="+ date);
           download.execute("http://homepages.inf.ed.ac.uk/stg/coinz/"+ date +"/coinzmap.geojson");
           try{mapData= download.get(); }
           catch (ExecutionException e){e.printStackTrace();}
           catch (InterruptedException e) {e.printStackTrace();}
           if (mapData == null) { Log.d(tag, "[onMapReady] mapdata is null"); }
       }

        if (mapboxMap == null) {
            Log.d(tag, "[onMapReady] mapBox is null");
        } else {
            map = mapboxMap;
            // Set user interface options
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            // Make location information available
            enableLocation();

            List<Feature> features = FeatureCollection.fromJson(mapData).features();
//            List<Feature> rates = FeatureCollection.fromJson(mapData);
            Log.d(tag, "mapData is:"+ mapData);

            for (int i = 0; i<features.size(); i++){
                try{
                    JSONObject jsonObject = new JSONObject(features.get(i).toJson());
                    JSONArray coordinates = jsonObject.getJSONObject("geometry").getJSONArray("coordinates");
                    double lng = Double.parseDouble(coordinates.get(0).toString());
                    double lat = Double.parseDouble(coordinates.get(1).toString());
                    //String type =  jsonObject.getJSONObject("properties").get("");
                    String id = jsonObject.getJSONObject("properties").getString("id");
                    double value = jsonObject.getJSONObject("properties").getDouble("value");
                    String strValue = Double.toString(value);
                    String currency = jsonObject.getJSONObject("properties").getString("currency");
                  // get lat, lng, type, id , value, <- str value,  currency, markersymbol
                  // create new coin also make coin java class new coin(id, value, currency,lng, lat)
                  //  coin.coins.put(id,coin)
                    mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lng))
                    .title(currency)
                    .setSnippet("Value -" +strValue));
                    Log.d(tag, "[onMapReady] Adding marker "+i+" to map");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }


        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        SharedPreferences settings = getSharedPreferences(PreferenceFile,
                Context.MODE_PRIVATE);
        date = settings.getString("lastDownloadDate", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        SharedPreferences FromFile = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
        if (FromFile.contains(date)){
            Log.d(tag, "[onStop] mapdata already saved");
        }
        else{
            SharedPreferences settings = getSharedPreferences("mapData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(date, mapData);
            editor.apply();
            Log.d(tag, "[onStop] mapdata is being save with date: "+date);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }




    //mapbox additional methods
    //initializes a location engine, sets how fast/slow it updates location
    // sets camera position of the map to a specific location
    private void setCameraPosition(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer() {
        if (mapView == null) {
            Log.d(tag, "mapView is null");
        } else {
            if (map == null) {
                Log.d(tag, "map is null");
            } else {
                locationLayerPlugin = new LocationLayerPlugin(mapView,
                        map, locationEngine);
                locationLayerPlugin.setLocationLayerEnabled(true);
                locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
                locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        locationEngine = new LocationEngineProvider(this)
                .obtainBestLocationEngineAvailable();
        locationEngine.setInterval(5000); // preferably every 5 seconds
        locationEngine.setFastestInterval(1000); // at most every second
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Log.d(tag, "Permissions are granted");
            initializeLocationEngine();
            initializeLocationLayer();
        } else {
            Log.d(tag, "Permissions are not granted");
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    //changes camera position based on users location
    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Log.d(tag, "[onLocationChanged] location is null");
        } else {
            Log.d(tag, "[onLocationChanged] location is not null");
            originLocation = location;
            setCameraPosition(location);
        }
    }


    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        Log.d(tag, "[onConnected] requesting location updates");
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain){
        Log.d(tag, "Permissions: " + permissionsToExplain.toString());
        // Present toast or dialog.
    }

    @Override
    public void onPermissionResult(boolean granted) {
        Log.d(tag, "[onPermissionResult] granted == " + granted);
        if (granted) {
            enableLocation();
        } else {
            // Open a dialogue with the user
        }
    }
}





