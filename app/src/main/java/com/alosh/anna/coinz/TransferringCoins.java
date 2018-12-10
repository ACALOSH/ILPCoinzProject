package com.alosh.anna.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TransferringCoins extends AppCompatActivity {
    private String tag = "Transfer";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ArrayList<Float> fwallet;
    public void setfwallet(String w)
    {
        if (!w.contains("[")){
            String[] tok = w.split(", ");
            ArrayList<Float> nw = new ArrayList<Float>();

            for (String t : tok) {

                nw.add(Float.parseFloat(t));
            }
            fwallet = nw;}
        else {
            String p = w.substring(1, w.length()-1);

            String[] tok = p.split(", ");
            ArrayList<Float> nw = new ArrayList<Float>();

            for (String t : tok) {

                nw.add(Float.parseFloat(t));
                fwallet = nw;
            }
        }
    }
    public static ArrayList<Float> getfwallet(){
        return fwallet;
    }


    private static float fbank;
    public static void setfbank(float b) {
        fbank = b;
    }
    public static float getfbank() {
        return fbank;
    }


    private static ArrayList<String> ffrend;
    public static void setffrend(String s)
    {    if (s.contains("[")){
        String p = s.substring(1, s.length()-1);
        ArrayList<String> myList = new ArrayList<>(Arrays.asList(p.split(",")));
        ffrend = myList;}
    else{
        ArrayList<String> myList = new ArrayList<>(Arrays.asList(s.split(",")));
        ffrend = myList;}

    }
    public static ArrayList<String> getffrend(){
        return ffrend;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String VIP = MoniesPopUp.getPOI();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferring_coins);

        //Puts the edittexts in an array to be accessed later in a cute for loop
        ArrayList<EditText> amounts = new ArrayList<>();
        //this will become our array of amounts to be transferred
        ArrayList<Float> stamounts = new ArrayList<>();

        //this is so the user can see how many coinz they have
        TextView shillb = findViewById(R.id.shilltext);
        shillb.setText("From "+MainActivity.getWalletoverlord().get(0).toString()+" SHIL");
        TextView dolrb = findViewById(R.id.dolrrtext);
        dolrb.setText("From "+MainActivity.getWalletoverlord().get(1).toString()+" DOLR");
        TextView quidb = findViewById(R.id.quiddtext);
        quidb.setText("From "+MainActivity.getWalletoverlord().get(2).toString()+" QUID");
        TextView penyb = findViewById(R.id.penyytext);
        penyb.setText("From "+MainActivity.getWalletoverlord().get(3).toString()+" PENY");

        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        CollectionReference Users = db.collection("Users");


        Button send = (Button) findViewById(R.id.sendmoney);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adds to amount array
                EditText st = (EditText) findViewById(R.id.shilamount); amounts.add(st);
                EditText dt = (EditText) findViewById(R.id.dolramount); amounts.add(dt);
                EditText qt = (EditText) findViewById(R.id.quidamount); amounts.add(qt);
                EditText pt = (EditText) findViewById(R.id.penyamount); amounts.add(pt);
                //this is zero
                Float z = Float.parseFloat("0.0");
                Log.d(tag, "amount check"+amounts);
                //checks if user left a blank box and creates an array of the differing amounts
                for (EditText a : amounts) {
                    if (a.getText().toString().isEmpty()) {
                        stamounts.add(z);
                    } else {
                        stamounts.add(Float.parseFloat(a.getText().toString()));
                    }
                }

                Log.d(tag, "on click check" + stamounts);
                //self explanatory bools put them here to look nice
                boolean nothing_entered = stamounts.get(0).equals(z) && stamounts.get(1).equals(z) && stamounts.get(2).equals(z) && stamounts.get(3).equals(z);
                boolean negative_deposit = stamounts.get(0)> MainActivity.getWalletoverlord().get(0) || stamounts.get(1) > MainActivity.getWalletoverlord().get(1)
                        || stamounts.get(2) > MainActivity.getWalletoverlord().get(2) || stamounts.get(3) > MainActivity.getWalletoverlord().get(3);


                if (nothing_entered){
                    Toast.makeText(TransferringCoins.this , "Cannot deposit nothing",Toast.LENGTH_LONG).show();
                    Log.d(tag, "preforming 0 task");
                }

                else if (negative_deposit){
                    Toast.makeText(TransferringCoins.this , "Nice try \n you can't deposit more than you have",Toast.LENGTH_LONG).show();
                    Log.d(tag, "preforming 1 task");
                }

                else if (!negative_deposit && !nothing_entered) {
                    //updates wallet
                    ArrayList<Float> updatedwallet = new ArrayList<Float>();
                    for (int i =0; i<4; i++){
                        updatedwallet.add(i,(MainActivity.getWalletoverlord().get(i) - stamounts.get(i)));
                        MainActivity.getWalletoverlord().remove(i);
                        MainActivity.getWalletoverlord().add(i,updatedwallet.get(i));
                    }
                    Log.d(tag,"trans"+ MainActivity.getWalletoverlord().toString());

                    //sends firestore updated data
                    Map<String, Object> data1 = new HashMap<>();
                    //array of coinz in wallet, in the order shil, dolr, quid, peny
                    data1.put("Wallet", MainActivity.getWalletoverlord());
                    data1.put("Friends", MainActivity.getFriendsoverlord());
                    data1.put("BankCoinz", MainActivity.getBankoverlord());
                    Users.document(mAuth.getCurrentUser().getEmail()).set(data1);

                    //gets friends data to update

                    //converts transfer into gold
                    ArrayList<Float> ex = MainActivity.getCurrencyEx();
                    float transfered= stamounts.get(0)*ex.get(0) + stamounts.get(1)*ex.get(1) + stamounts.get(2)*ex.get(2) +stamounts.get(3)*ex.get(3);
                    Log.d(tag,"trans"+transfered);

                    //stores transferred gold in a seperate firestore document to be acessed went friend opens app
                    Map<String, Object> data2 = new HashMap<>();
                    data2.put("coinz", fbank+transfered);
                    Users.document(VIP+"transferred").set(data2);
                    startActivity(new Intent(TransferringCoins.this, WalletActivity.class));
                    Toast.makeText(TransferringCoins.this , "Deposit successful! \nYou transferred: "+transfered +"GOLD to ur homie",Toast.LENGTH_LONG).show();

                }
                else { Log.d(tag, "LEEK");}
            }
        });






        Button back = (Button) findViewById(R.id.button2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransferringCoins.this, WalletActivity.class));
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(width, (int) (height*.75));

    }

}
