package com.alosh.anna.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TrophiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView txt = findViewById(R.id.textView6);
        ImageView Trophy = findViewById(R.id.imageView);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophies);

        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.tbacktomap);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TrophiesActivity.this, MainActivity.class));
            }
        });

        //if your bank balance is over 100000 you win
        if( MainActivity.getBankoverlord()> 1000000000){
            Trophy.setImageResource(R.drawable.mapbox_compass_icon);
            txt.setText(getString(R.string.winner));
        }
    }

}
