package com.alosh.anna.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

public class WalletActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  String tag = "WalletActivity";
    public String walletbalance ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();

        setContentView(R.layout.activity_wallet);
        TextView textwalletamount = (TextView) findViewById(R.id.walletcoinzamount);

        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.wbacktomap);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WalletActivity.this, MainActivity.class));
            }
        });
        Button sendmonies = (Button) findViewById(R.id.button);
        sendmonies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WalletActivity.this, MoniesPopUp.class));
            }
        });


        ArrayList<Float> wall = MainActivity.getWalletoverlord();
        textwalletamount.setText(wall.get(0)+ " shils \n"+ wall.get(1)+" dolrs \n"+wall.get(2)+" quid \n"+wall.get(3)+" penys");





    }

}
