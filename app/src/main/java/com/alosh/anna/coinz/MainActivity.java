package com.alosh.anna.coinz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.Chars;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import com.mapbox.mapboxsdk.annotations.Marker;
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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {
    private  String tag = "MainActivity";

    //mapbox nonsense
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;

    //preffile and json nonsense
    public String mapData;
    private final String PreferenceFile = "preferenceData";
    public static String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

    //stuff for firestore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private static ArrayList<Float> Walletoverlord;
    public void setWalletoverlord(String w)
    {
        if (!w.contains("[")){
        String[] tok = w.split(", ");
        ArrayList<Float> nw = new ArrayList<Float>();

        for (String t : tok) {

            nw.add(Float.parseFloat(t));
        }
        Walletoverlord = nw;}
        else {
            String p = w.substring(1, w.length()-1);

            String[] tok = p.split(", ");
            ArrayList<Float> nw = new ArrayList<Float>();

            for (String t : tok) {

                nw.add(Float.parseFloat(t));
                Walletoverlord = nw;
            }

        }


    }
    public static ArrayList<Float> getWalletoverlord(){
        return Walletoverlord;
    }


    private static int Bankoverlord;
    public static void setBankoverlord(int b) {
        Bankoverlord = b;
    }
    public static int getBankoverlord() {
        return Bankoverlord;
    }


    private static ArrayList<String> Friendsoverlord;
    public static void setFriendsoverlord(String s)
    {    if (s.contains("[")){
        String p = s.substring(1, s.length()-1);
        ArrayList<String> myList = new ArrayList<>(Arrays.asList(p.split(",")));
        Friendsoverlord = myList;}
        else{
        ArrayList<String> myList = new ArrayList<>(Arrays.asList(s.split(",")));
        Friendsoverlord = myList;}

    }
    public static ArrayList<String> getFriendsoverlord(){
        return Friendsoverlord;
    }

    private static ArrayList<String> markercur = new ArrayList<>();
    public static void setMarkercur(ArrayList<String> markercur) {
        MainActivity.markercur = markercur;
    }
    public static ArrayList<String> getMarkercur() {
        return markercur;
    }

    private static ArrayList<Float> markerval = new ArrayList<>();
    public static void setMarkerval(ArrayList<Float> markerval) {
        MainActivity.markerval = markerval;
    }
    public static ArrayList<Float> getMarkerval() {
        return markerval;
    }

    private static ArrayList<String> markerid = new ArrayList<>();
    public static void setMarkerid(ArrayList<String> markerid) {
        MainActivity.markerid = markerid;
    }
    public static ArrayList<String> getMarkerid() {
        return markerid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton Wallet = findViewById(R.id.Wallet);
        FloatingActionButton Bank = findViewById(R.id.Bank);
        FloatingActionButton Trophies = findViewById(R.id.Trophies);
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        CollectionReference Users = db.collection("Users");


        Wallet.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WalletActivity.class)));

        Bank.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BankActivity.class)));

        Trophies.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TrophiesActivity.class)));


        Mapbox.getInstance(this, "pk.eyJ1IjoiczE2MDE4NDciLCJhIjoiY2puMXBoeXplMnNicTNxbzhhYWFmbnhqZyJ9.hTS5UNqpWkpg2Fcy4z4fAQ");
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //this is to access the firestore database (db)
        DocumentReference docRef = db.collection("Users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(tag, "Accessing Database");
                    if (document.exists()) {
                        //stores wallet info in wallet
                        String wallet = document.get("Wallet").toString();
                        //sets it to the public static var
                        setWalletoverlord(wallet);
                        //stores bank balance and sets it to the public var
                        int bank = (int) (long) document.get("BankCoinz");
                        setBankoverlord(bank);
                        //same here for the list of friends
                        String frens = document.get("Friends").toString();
                        setFriendsoverlord(frens);
                        Log.d(tag, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(tag, "No such document");
                    }
                } else {
                    Log.d(tag, "get failed with ", task.getException());
                }
            }
        });
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
           //Log.d(tag, "[onMapReady] connection error catch"+ mapData);
           if (mapData == null) { Log.d(tag, "[onMapReady] mapdata is null"); }
       }

        if (mapboxMap == null) {
            Log.d(tag, "[onMapReady] mapBox is null");
        } else {
            map = mapboxMap;
            // Set user interface options
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            //map.addOnMapClickListener(this::onMapClick);
            map.setOnMarkerClickListener(this::onMarkerClick);


            // Make location information available
            enableLocation();

            List<Feature> features = FeatureCollection.fromJson(mapData).features();
//            List<Feature> rates = FeatureCollection.fromJson(mapData);
            //Log.d(tag, "mapData is:"+ mapData);
            ArrayList<String> mrkrscurrency = new ArrayList<>();
            ArrayList<Float> mrkrsvls = new ArrayList<>();
            //double rates = JsonObject.getJsonObject(“rates”).getdouble(currency);

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
                    .title(id)
                    .setSnippet("Value -" +strValue));
                    mrkrscurrency.add(currency);
                    mrkrsvls.add(Float.parseFloat(strValue));
                    Log.d(tag, "[onMapReady] Adding marker "+i+" to map");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            setMarkercur(mrkrscurrency);
            setMarkerval(mrkrsvls);

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

    public boolean onMarkerClick(Marker mr){

        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        CollectionReference Users = db.collection("Users");
        List<Marker> mrkrs = map.getMarkers();

            String cur = getMarkercur().get(mrkrs.indexOf(mr));
            Log.d(tag, "curr is"+ cur);
            Float val = getMarkerval().get(mrkrs.indexOf(mr));
            ArrayList<String>mid = getMarkerid();
            Log.d(tag, mr.getTitle());


            LatLng loc = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
            if (mr.getPosition().distanceTo(loc)<=25 && !mid.contains(mr.getTitle())){

                        switch (cur){
                            case ("SHIL") :
                                Float updatedshil = Walletoverlord.get(0)+val;
                                Walletoverlord.remove(0);
                                Walletoverlord.add(0, updatedshil);
                                Map<String, Object> dataupdate = new HashMap<>();
                                //array of coinz in wallet, in the order shil, dolr, quid, peny
                                dataupdate.put("Wallet", Walletoverlord);
                                dataupdate.put("BankCoinz", Bankoverlord);
                                dataupdate.put("Friends", Friendsoverlord);
                                Users.document(email).set(dataupdate);
                                Toast.makeText(MainActivity.this, "You have acquired:"+val+" shil!", Toast.LENGTH_LONG).show();
                                break;

                            case ("DOLR"):
                                Float updateddolr = Walletoverlord.get(1)+val;
                                Walletoverlord.remove(1);
                                Walletoverlord.add(1, updateddolr);
                                Map<String, Object> dataupdate1 = new HashMap<>();
                                //array of coinz in wallet, in the order shil, dolr, quid, peny
                                dataupdate1.put("Wallet", Walletoverlord);
                                dataupdate1.put("BankCoinz", Bankoverlord);
                                dataupdate1.put("Friends", Friendsoverlord);
                                Users.document(email).set(dataupdate1);
                                Toast.makeText(MainActivity.this, "You have acquired:"+val+" dolrs!", Toast.LENGTH_LONG).show();
                                break;

                            case ("QUID"):
                                Log.d(tag, "running quid");
                                Float updatedquid = Walletoverlord.get(2)+val;
                                Walletoverlord.remove(2);
                                Walletoverlord.add(2, updatedquid);
                                Map<String, Object> dataupdate2 = new HashMap<>();
                                //array of coinz in wallet, in the order shil, dolr, quid, peny
                                dataupdate2.put("Wallet", Walletoverlord);
                                dataupdate2.put("BankCoinz", Bankoverlord);
                                dataupdate2.put("Friends", Friendsoverlord);
                                Users.document(email).set(dataupdate2);
                                Toast.makeText(MainActivity.this, "You have acquired:"+val+" quid!", Toast.LENGTH_LONG).show();
                                break;

                            case ("PENY"):
                                Float updatedpeny = Walletoverlord.get(3)+val;
                                Walletoverlord.remove(3);
                                Walletoverlord.add(3, updatedpeny);
                                Map<String, Object> dataupdate3 = new HashMap<>();
                                //array of coinz in wallet, in the order shil, dolr, quid, peny
                                dataupdate3.put("Wallet", Walletoverlord);
                                dataupdate3.put("BankCoinz", Bankoverlord);
                                dataupdate3.put("Friends", Friendsoverlord);
                                Users.document(email).set(dataupdate3);
                                Toast.makeText(MainActivity.this, "You have acquired:"+val+" penys!", Toast.LENGTH_LONG).show();
                                break;

                                }


                mid.add(mr.getTitle());
                setMarkerid(mid);
                Log.d(tag, "cleeck was succesful");
                return true;

            }
           else{
                Log.d(tag, "was already cleecked" +markerid);
                return false;
            }

        }


    }






