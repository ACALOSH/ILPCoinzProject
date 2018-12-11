package com.alosh.anna.coinz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
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
import com.mapbox.mapboxsdk.camera.CameraUpdate;
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.mapbox.geojson.Geometry.fromJson;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {
    private String tag = "MainActivity";

    ////////////Auxilary vairables and methods /////////////////
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


    //this is the user wallet information
    private static ArrayList<Float> Walletoverlord;
    public static void setWalletoverlord(String w) {
        if (!w.contains("[")) {
            String[] tok = w.split(", ");
            ArrayList<Float> nw = new ArrayList<>();

            for (String t : tok) {

                nw.add(Float.parseFloat(t));
            }
            Walletoverlord = nw;
        } else {
            String p = w.substring(1, w.length() - 1);

            String[] tok = p.split(", ");
            ArrayList<Float> nw = new ArrayList<>();

            for (String t : tok) {

                nw.add(Float.parseFloat(t));
                Walletoverlord = nw;
            }
        }
    }
    public static ArrayList<Float> getWalletoverlord() {
        return Walletoverlord;
    }

    //this is the user's bank balance
    private static float Bankoverlord =0;
    public static void setBankoverlord(float b) {
        Bankoverlord = b;
    }
    public static float getBankoverlord() {
        return Bankoverlord;
    }


    //this is the user's friend information
    private static ArrayList<String> Friendsoverlord;
    public static void setFriendsoverlord(String s) {
        if (s.contains("[")) {
            String p = s.substring(1, s.length() - 1);
            Friendsoverlord = new ArrayList<>(Arrays.asList(p.split(",")));
        } else {
            Friendsoverlord = new ArrayList<>(Arrays.asList(s.split(",")));
        }

    }
    public static ArrayList<String> getFriendsoverlord() {
        return Friendsoverlord;
    }

    // variable dedicated to coins that have been transferred to the user, gotten from firestore
    private static Float Transferedcoinz = Float.parseFloat("0.0");
    public static void setTransferedcoinz(Float transferedcoinz) {
        Transferedcoinz = transferedcoinz;
    }
    public static Float getTransferedcoinz() {
        return Transferedcoinz;
    }

    //Array of the Currency Exchange, gotten from json
    private static ArrayList<Float> CurrencyEx;
    public static void setCurrencyEx(String str) {
        if (str.contains("}")) {
            str = str.substring(0, str.length() - 1);
        }
        String[] sp = str.split(", ");
        ArrayList<Float> curr = new ArrayList<>();
        ArrayList<String> myList = new ArrayList<>();
        for (String t : sp) {
            String[] c = t.split("=");
            if (c.length == 2) {
                myList.add(c[1]);
            } else {
                myList.add(c[0]);
            }
        }
        for (String t : myList) {
            curr.add(Float.parseFloat(t));
        }
        CurrencyEx = curr;
        Log.d("Main", "Currency success " + curr);
    }
    public static ArrayList<Float> getCurrencyEx() {
        return CurrencyEx;
    }

    //Array of the markers currency for ease
    private static ArrayList<String> markercur = new ArrayList<>();
    public static void setMarkercur(ArrayList<String> markercur) {
        MainActivity.markercur = markercur;
    }
    public static ArrayList<String> getMarkercur() {
        return markercur;
    }

    //Array of the markers value for ease
    private static ArrayList<Float> markerval = new ArrayList<>();
    public static void setMarkerval(ArrayList<Float> markerval) {
        MainActivity.markerval = markerval;
    }
    public static ArrayList<Float> getMarkerval() {
        return markerval;
    }

    //keep track of marker ids
    private static ArrayList<String> markerid = new ArrayList<>();
    public static ArrayList<String> getMarkerid() {
        return markerid;
    }
    public static void setMarkerid(ArrayList<String> markerid) {
        MainActivity.markerid = markerid;
    }

    //Dialouge list for a little bit of fun when collecting markers, shows up when you collect markers
    private ArrayList<String> dialougelist = new ArrayList<>(Arrays.asList("You managed to hand in an assignment!", "You avoided a mormon!", "You didn\'t forget your student card!",
            "You got an email, class is canceled!", "You made it to your 9am!", "You found a seat in the Library", "You saw a dog!", "Your friend invited you to drinks at PearTree",
            "There's free food in Appleton!", "Your bike didn\\'t get stolen!"));


    ////////////ON CREATE /////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sets up mapbox
        Mapbox.getInstance(this, "pk.eyJ1IjoiczE2MDE4NDciLCJhIjoiY2puMXBoeXplMnNicTNxbzhhYWFmbnhqZyJ9.hTS5UNqpWkpg2Fcy4z4fAQ");
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        CollectionReference Users = db.collection("Users");
        setFriendsoverlord("nofriends");



        //sets the buttons to go to their designated places
        FloatingActionButton Wallet = findViewById(R.id.Wallet);
        FloatingActionButton Bank = findViewById(R.id.Bank);
        FloatingActionButton Trophies = findViewById(R.id.Trophies);
        Wallet.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WalletActivity.class)));
        Bank.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BankActivity.class)));
        Trophies.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TrophiesActivity.class)));


        //this is to access the firestore database (db) from a uithread
        new Thread(new Runnable() {
            @Override
            public void run() {
                DocumentReference docRef = Users.document(email);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(tag, "Accessing Database from thread");
                        if (document.exists()) {
                            //stores wallet info in wallet
                            String wallet = document.get("Wallet").toString();
                            //sets it to the public static var
                            setWalletoverlord(wallet);
                            //stores bank balance and sets it to the public var
                            float bank = Float.parseFloat(document.get("BankCoinz").toString());
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
                });
                try {
                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } catch (final Exception ex) {
                    Log.i("---", "Exception in thread");
                }
            }
        }).start();

        //this get the transferred coinz and add them to the users bank balance
        transfercoinzz();



    }//end of on create


    ////////////ON MAP READY /////////////////
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        SharedPreferences FromFile = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());


        if (FromFile.contains(date)) {
            mapData = FromFile.getString(date, "");
            //if there is already data saved in preferences
            Log.d(tag, "[onMapReady] fromfilemapdata =" + mapData);
        } else {
            //else map data taken from server
            Log.d(tag, "[onMapReady] date =" + date);

            DownloadFileTask download = new DownloadFileTask();
            download.execute("http://homepages.inf.ed.ac.uk/stg/coinz/" + date + "/coinzmap.geojson");
            try {
                mapData = download.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(tag, "[onMapReady] connection error catch" + mapData);
            if (mapData == null) {
                Log.d(tag, "[onMapReady] mapdata is null");
            }

            //resets marker id, currency, and value arrays each day
            ArrayList<String> resetmarkeridandcurr = new ArrayList<>(Arrays.asList(" "));
            ArrayList<Float> resetmarkerval = new ArrayList<>();
            setMarkerid(resetmarkeridandcurr);
            setMarkercur(resetmarkeridandcurr);
            setMarkerval(resetmarkerval);
        }


        if (mapboxMap == null) {
            Log.d(tag, "[onMapReady] mapBox is null");
        } else {
            map = mapboxMap;
            // Set user interface options
            // Make location information available
            enableLocation();
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            //this sets the onMarkerclick method as the one at the bottom
            map.setOnMarkerClickListener(this::onMarkerClick);

            Log.d(tag, "[onMapReady] fromfilemapdata =" + mapData);
            //only downloads json once a day!

                //mmm watcha sayyy aw, that you only meant well?
                Map jsonderulo = new Gson().fromJson(mapData, Map.class);
                //gets rates
                String awrats = jsonderulo.get("rates").toString();
                setCurrencyEx(awrats);
                Log.d(tag, "gson " + jsonderulo.get("rates"));


                //gets the list of markers
                List<Feature> features = FeatureCollection.fromJson(mapData).features();


                // we make these lists bc we need them for this upcoming marker for loop
                ArrayList<String> mrkrscurrency = new ArrayList<>();
                ArrayList<Float> mrkrsvls = new ArrayList<>();
                ArrayList<String> mrkrid = new ArrayList<>();


                for (int i = 0; i < features.size(); i++) {
                    try {
                        //extracts data from marker feature collection
                        JSONObject jsonObject = new JSONObject(features.get(i).toJson());
                        JSONArray coordinates = jsonObject.getJSONObject("geometry").getJSONArray("coordinates");
                        double lng = Double.parseDouble(coordinates.get(0).toString());
                        double lat = Double.parseDouble(coordinates.get(1).toString());
                        String id = jsonObject.getJSONObject("properties").getString("id");
                        String markersymb = jsonObject.getJSONObject("properties").getString("marker-symbol");
                        double value = jsonObject.getJSONObject("properties").getDouble("value");
                        String strValue = Double.toString(value);
                        String currency = jsonObject.getJSONObject("properties").getString("currency");

                        //adds marker with properties extracted above
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title("A Mysterious coin!")
                                .setSnippet(strValue));
                        mrkrscurrency.add(currency);
                        mrkrsvls.add(Float.parseFloat(strValue));
                        mrkrid.add(id);
                        //Log.d(tag, "[onMapReady] Adding marker " + i + " to map");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setMarkercur(mrkrscurrency);
                setMarkerval(mrkrsvls);
                setMarkerid(mrkrid);
                Log.d(tag, markerid.toString());

        }


    }// end of onmapready


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
        //sharedpref stuff
        SharedPreferences FromFile = getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
        if (FromFile.contains(date)) {
            Log.d(tag, "[onStop] mapdata already saved");
        } else {
            SharedPreferences settings = getSharedPreferences("mapData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(date, mapData);
            editor.apply();
            Log.d(tag, "[onStop] mapdata is being save with date: " + date);
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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));


    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer() {
        if (mapView == null) {
            Log.d(tag, "mapView is null");
        } else {
            if (map == null) {
                Log.d(tag, "map is null");
            } else {
                Log.d(tag, "[ERROR TESTING} SHOULD SHOW UP IF INITALIZING NEW POSITION and map is not null" );
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
            Log.d(tag,"Lastlocation is null");
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
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Log.d(tag, "Permissions: " + permissionsToExplain.toString());
        // Present toast or dialog.
    }

    @Override
    public void onPermissionResult(boolean granted) {
        Log.d(tag, "[onPermissionResult] granted == " + granted);
        if (granted) {
            enableLocation();
        } else {
            Toast.makeText(MainActivity.this, "you messed up GIVE US PERMISSION", Toast.LENGTH_LONG).show();

        }
    }

    //this is activated everytime someone clicks on a marker
    public boolean onMarkerClick(Marker mr) {

        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        CollectionReference Users = db.collection("Users");
        List<Marker> mrkrs = map.getMarkers();

        //gets currency of the marker
        String cur = getMarkercur().get(mrkrs.indexOf(mr));
        Log.d(tag, "curr is " + cur);
        //gets value of marker
        Float val = getMarkerval().get(mrkrs.indexOf(mr));
        Log.d(tag, mr.getTitle());
        //gets current location
        LatLng loc = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
        //gets a random dialouge from list
        String dialouge = dialougelist.get((int) (Math.random() * 9));

        //if user is within 25meters of the marker
        Log.d(tag, "Click logic check " +String.valueOf(markerid.contains(mr.getTitle())) +" "+String.valueOf(markerid.get(mrkrs.indexOf(mr)).isEmpty()));
        if (mr.getPosition().distanceTo(loc) <= 25 && !(markerid.get(mrkrs.indexOf(mr)).isEmpty())) {

            switch (cur) {
                case ("SHIL"):
                    //adds value to wallet
                    Float updatedshil = Walletoverlord.get(0) + val;
                    Walletoverlord.remove(0);
                    Walletoverlord.add(0, updatedshil);
                    //rewrites data in firestore
                    Map<String, Object> dataupdate = new HashMap<>();
                    //array of coinz in wallet, in the order shil, dolr, quid, peny
                    dataupdate.put("Wallet", Walletoverlord);
                    dataupdate.put("BankCoinz", Bankoverlord);
                    dataupdate.put("Friends", Friendsoverlord);
                    Users.document(email).set(dataupdate);
                    Toast.makeText(MainActivity.this, dialouge + "\n You have acquired:" + val + " shil!", Toast.LENGTH_LONG).show();
                    break;

                case ("DOLR"):
                    //adds value to wallet
                    Float updateddolr = Walletoverlord.get(1) + val;
                    Walletoverlord.remove(1);
                    Walletoverlord.add(1, updateddolr);
                    //rewrites data in firestore
                    Map<String, Object> dataupdate1 = new HashMap<>();
                    //array of coinz in wallet, in the order shil, dolr, quid, peny
                    dataupdate1.put("Wallet", Walletoverlord);
                    dataupdate1.put("BankCoinz", Bankoverlord);
                    dataupdate1.put("Friends", Friendsoverlord);
                    Users.document(email).set(dataupdate1);
                    Toast.makeText(MainActivity.this, dialouge + "\n You have acquired:" + val + " dolrs!", Toast.LENGTH_LONG).show();
                    break;

                case ("QUID"):
                    //adds value to wallet
                    Float updatedquid = Walletoverlord.get(2) + val;
                    Walletoverlord.remove(2);
                    Walletoverlord.add(2, updatedquid);
                    //rewrites data in firestore
                    Map<String, Object> dataupdate2 = new HashMap<>();
                    //array of coinz in wallet, in the order shil, dolr, quid, peny
                    dataupdate2.put("Wallet", Walletoverlord);
                    dataupdate2.put("BankCoinz", Bankoverlord);
                    dataupdate2.put("Friends", Friendsoverlord);
                    Users.document(email).set(dataupdate2);
                    Toast.makeText(MainActivity.this, dialouge + "\n You have acquired:" + val + " quid!", Toast.LENGTH_LONG).show();
                    break;

                case ("PENY"):
                    //adds value to wallet
                    Float updatedpeny = Walletoverlord.get(3) + val;
                    Walletoverlord.remove(3);
                    Walletoverlord.add(3, updatedpeny);
                    //rewrites data in firestore
                    Map<String, Object> dataupdate3 = new HashMap<>();
                    //array of coinz in wallet, in the order shil, dolr, quid, peny
                    dataupdate3.put("Wallet", Walletoverlord);
                    dataupdate3.put("BankCoinz", Bankoverlord);
                    dataupdate3.put("Friends", Friendsoverlord);
                    Users.document(email).set(dataupdate3);
                    Toast.makeText(MainActivity.this, dialouge + "\n You have acquired:" + val + " penys!", Toast.LENGTH_LONG).show();
                    break;

            }

            markerid.set(mrkrs.indexOf(mr), " ");
            map.removeMarker(mr);
            Log.d(tag, "cleek was succesful ");

            return true;

        } else {
            Log.d(tag, "Cleek error");
            return false;
        }

    }

    //method for transferring coinz with a uithread
    private void transfercoinzz() {
        new Thread() {
            @Override
            public void run() {
                DocumentReference transcoinz = db.collection("Users").document( mAuth.getCurrentUser().getEmail()+ "transferred");
                transcoinz.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(tag, "Accessing Database");
                        if (document.exists()) {
                            Float ct = Float.parseFloat(document.get("coinz").toString());
                            //sets transferred coinz from the database to the private instance
                            setTransferedcoinz(ct);
                            //this sets the transferred coinz variable to 0 in the database
                            Map<String, Object> data3 = new HashMap<>();
                            data3.put("coinz", 0);
                            db.collection("Users").document(mAuth.getCurrentUser().getEmail() + "transferred").set(data3);
                            Log.d(tag,"successfull accesss");

                        } else {
                            Log.d(tag, "No such document");
                        }
                    } else {
                        Log.d(tag, "get failed with ", task.getException());
                    }
                    //updates the user's bank balance and lets them know they've been transferred coinz
                    setBankoverlord(getBankoverlord() + getTransferedcoinz());
                    if (!(getTransferedcoinz() == 0)) {
                        Toast.makeText(MainActivity.this, "Your Friends transferred you " + getTransferedcoinz() + " GOLD", Toast.LENGTH_LONG).show();
                    }
                });

                try {
                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } catch (final Exception ex) {
                    Log.i("---", "Exception in thread");
                }
            }
        }.start();

    }
}






