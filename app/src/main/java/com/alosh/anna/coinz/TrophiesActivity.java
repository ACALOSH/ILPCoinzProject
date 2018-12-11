package com.alosh.anna.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class TrophiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophies);
        TextView txt = findViewById(R.id.textView6);
        ImageView Trophy = findViewById(R.id.trophy);
        String tag = "trophy";
        Log.d(tag,"null test" +String.valueOf(Trophy ==null));

        FloatingActionButton back = findViewById(R.id.tbacktomap);
        back.setOnClickListener(view -> startActivity(new Intent(TrophiesActivity.this, MainActivity.class)));

        //if your bank balance is over 100000 you win
        if( MainActivity.getBankoverlord()> 1000000000){
            Trophy.setImageResource(R.drawable.mapbox_compass_icon);
            txt.setText(getString(R.string.winner));
        }
    }

}
