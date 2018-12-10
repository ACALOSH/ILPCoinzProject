package com.alosh.anna.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.Query;

import java.util.ArrayList;

public class MoniesPopUp extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //POI means person of interest aka the person the user want to send money to
    private static String POI;
    public void setPOI(String POI) {
        this.POI = POI;
    }
    public static String getPOI() {
        return POI;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monies_pop_up);
        CollectionReference Users = db.collection("Users");

        //allows friends to show up as a list
        ArrayList<String> friendlist = MainActivity.getFriendsoverlord();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, friendlist);
        ListView frens = (ListView) findViewById(R.id.listy);
        frens.setAdapter(adapter);
        frens.setOnItemClickListener(mMessageClickedHandler);



        Button bye = (Button) findViewById(R.id.bye);
        bye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoniesPopUp.this, WalletActivity.class));
            }
        });

        EditText hemail = findViewById(R.id.addhomie);
        Button add = findViewById(R.id.addhomiebutton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hemail ==null || hemail.getText().toString()==" ") {
                    Toast.makeText(MoniesPopUp.this, "enter an email!", Toast.LENGTH_LONG).show();
                }
                else {
                    //tried to get the query system to work but idk why its  not working
                    //Query isuser = db.collection("User").whereEqualTo("name", hemail.getText().toString());

                    ArrayList<String> updatefriends = MainActivity.getFriendsoverlord();
                    String hm = hemail.getText().toString();
                    //checks if added friend is already a friend
                    if (!(updatefriends.contains(hm))){
                    updatefriends.add(hm);
                    MainActivity.setFriendsoverlord(updatefriends.toString());
                    }
                }

            }
        });

        //this makes it a popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(width, (int) (height*.75));

    }//end of on create
    //method for when user clicks on friend
    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Log.d("HelloListView", "You clicked Item: " + id + " at position:" + position);
            //gets instance of friend and sets it as the person of interst
            String vip = MainActivity.getFriendsoverlord().get((int)(long)id);
            setPOI(vip);
            Log.d("HelloListView", "frend clicked" +POI);
            //goes to transfer page
            startActivity(new Intent(MoniesPopUp.this, TransferringCoins.class));

        }
    };



}

