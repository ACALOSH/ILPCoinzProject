package com.alosh.anna.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class WalletActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_wallet);

        //shows wallet
        TextView textwalletamount = findViewById(R.id.walletcoinzamount);
        ArrayList<Float> wall = MainActivity.getWalletoverlord();
        textwalletamount.setText(wall.get(0)+ " shils \n"+ wall.get(1)+" dolrs \n"+wall.get(2)+" quid \n"+wall.get(3)+" penys");


        FloatingActionButton back = findViewById(R.id.wbacktomap);
        back.setOnClickListener(view -> startActivity(new Intent(WalletActivity.this, MainActivity.class)));


        Button sendmonies = findViewById(R.id.button);
        sendmonies.setOnClickListener(view -> startActivity(new Intent(WalletActivity.this, MoniesPopUp.class)));


        //signs out
        Button signout = findViewById(R.id.signout);
        signout.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(WalletActivity.this, LoginActivity.class));
        });





    }

}
