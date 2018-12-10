package com.alosh.anna.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BankPopUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String tag ="Bank popup";
    //cpd is current coin count as to not go over the 50 coin limit
    private float cpd; public void setCpd(float cpd) { this.cpd = cpd; } public float getCpd() { return cpd; }
    //this is to help keep track of the day so the limit resets
    private String saveddate; public void setSaveddate(String saveddate) { this.saveddate = saveddate; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        CollectionReference Users = db.collection("Users");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_pop_up);
        TextView gold = findViewById(R.id.goldamount);


        ArrayList<Float> ex = MainActivity.getCurrencyEx();
        //deposit amount is sum of each currency multiplied by the exchange rate
        float deposit = BankActivity.getSdeposit()*ex.get(0) + BankActivity.getDdeposit()*ex.get(1) + BankActivity.getQdeposit()*ex.get(2) +BankActivity.getPdeposit()*ex.get(3);
        //how many coinz are being deposited
        float coinzadded = BankActivity.getDdeposit()+BankActivity.getPdeposit()+BankActivity.getQdeposit()+BankActivity.getSdeposit();
        gold.setText(deposit + " GOLD");

        //checks the daay, if savedate ==null then its the user's first day and we set it at the current date
        String toddate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        if(saveddate ==null){setSaveddate(toddate);}
        Log.d(tag,toddate+"  "+saveddate);
        //if the day is not the same then we reset the coin limit counter and save the date as today
        if(!(saveddate.compareTo(toddate)==0)){
            setCpd(Float.parseFloat("0.0"));
            setSaveddate(toddate);
        }



        Button back = (Button) findViewById(R.id.backk);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BankPopUp.this, BankActivity.class));
            }
        });




        Button depositt = findViewById(R.id.depositt);
        depositt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checks how many coinz have been deposited
                if (cpd>50){Toast.makeText(BankPopUp.this , "You've gone over your 50 coin limit+",Toast.LENGTH_LONG).show();
                }

                //updates bank balance
                float bankbalance = (MainActivity.getBankoverlord() +deposit);

                //update wallet
                ArrayList<Float> depositwallet = new ArrayList<Float> (Arrays.asList(BankActivity.getSdeposit(), BankActivity.getDdeposit(), BankActivity.getQdeposit(), BankActivity.getPdeposit()));
                ArrayList<Float> updatedwallet = new ArrayList<Float>();
                for (int i =0; i<4; i++){
                    updatedwallet.add(i,(MainActivity.getWalletoverlord().get(i) - depositwallet.get(i)));
                    MainActivity.getWalletoverlord().remove(i);
                    MainActivity.getWalletoverlord().add(i,updatedwallet.get(i));
                }

                //updates values in firestore
                Map<String, Object> data1 = new HashMap<>();
                //array of coinz in wallet, in the order shil, dolr, quid, peny
                data1.put("Wallet", MainActivity.getWalletoverlord());
                data1.put("BankCoinz", bankbalance);
                data1.put("Friends", MainActivity.getFriendsoverlord());
                Users.document(mAuth.getCurrentUser().getEmail()).set(data1);

                //updates coin limit counter
                setCpd(getCpd()+coinzadded);

                //goes back to bank
                startActivity(new Intent(BankPopUp.this, BankActivity.class));
                Toast.makeText(BankPopUp.this , "Deposit successful! \nBank balance: "+bankbalance,Toast.LENGTH_LONG).show();
            }
        });








        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(width, (int) (height*.75));
    }

}
