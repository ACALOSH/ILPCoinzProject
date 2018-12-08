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

public class WalletActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  String tag = "WalletActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();

        setContentView(R.layout.activity_wallet);
        TextView walletamount = (TextView) findViewById(R.id.walletcoinzamount);

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

        DocumentReference docRef = db.collection("Users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String wallet = document.get("Wallet").toString();
                    if (document.exists()) {
                        Log.d(tag, "DocumentSnapshot data: " + document.getData());
                        //array of coinz in wallet, in the order shil, dolr, quid, peny
                        walletamount.setText(wallet.charAt(1)+ " shils \n"+ wallet.charAt(4)+" dolrs \n"+wallet.charAt(7)+" quid \n"+wallet.charAt(10)+" penys");
                    } else {
                        Log.d(tag, "No such document");
                    }
                } else {
                    Log.d(tag, "get failed with ", task.getException());
                }
            }

        });





    }

}
