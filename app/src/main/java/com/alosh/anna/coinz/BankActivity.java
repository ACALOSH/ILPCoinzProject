package com.alosh.anna.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;

public class BankActivity extends AppCompatActivity {
    private  String tag = "BankActivity";
    //public String bankbalance = "";
    private static Float sdeposit;
    public static void setSdeposit(Float deposit) {
        BankActivity.sdeposit = deposit;
    }
    public static Float getSdeposit() {
        return sdeposit;
    }
    private static Float ddeposit;
    public static void setDdeposit(Float deposit) {
        BankActivity.ddeposit = deposit;
    }
    public static Float getDdeposit() {
        return ddeposit;
    }
    private static Float qdeposit;
    public static void setQdeposit(Float deposit) { BankActivity.qdeposit = deposit; }
    public static Float getQdeposit() { return qdeposit; }
    private static Float pdeposit;
    public static void setPdeposit(Float deposit) { BankActivity.pdeposit = deposit; }
    public static Float getPdeposit() { return pdeposit; }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);


        EditText shilbox = findViewById(R.id.shilltext);
        EditText dolrbox = findViewById(R.id.dolrrtext);
        EditText quidbox = findViewById(R.id.quiddtext);
        EditText penybox = findViewById(R.id.penyytext);
        ArrayList<EditText> boxes = new ArrayList<>(Arrays.asList(shilbox,dolrbox,quidbox,penybox));
        TextView shillb = findViewById(R.id.monieshil);
        shillb.setText("From "+MainActivity.getWalletoverlord().get(0).toString()+" SHIL");
        TextView dolrb = findViewById(R.id.moniesdolr);
        dolrb.setText("From "+MainActivity.getWalletoverlord().get(1).toString()+" DOLR");
        TextView quidb = findViewById(R.id.moniesquid);
        quidb.setText("From "+MainActivity.getWalletoverlord().get(2).toString()+" QUID");
        TextView penyb = findViewById(R.id.moniespeny);
        penyb.setText("From "+MainActivity.getWalletoverlord().get(3).toString()+" PENY");




        FloatingActionButton back= findViewById(R.id.bbacktomap);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BankActivity.this, MainActivity.class));
            }
       });

        Button pocket = findViewById(R.id.pocket);
        pocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float z = Float.parseFloat("0.0");
                ArrayList<Float> bxam = new ArrayList<>();
                for(EditText b : boxes){
                    if (b.getText().toString().isEmpty()) {
                        bxam.add(z);
                    } else {
                        bxam.add(Float.parseFloat(b.getText().toString()));
                    }
                }
                Float shil = bxam.get(0);
                Float dolr = bxam.get(1);
                Float quid = bxam.get(2);
                Float peny = bxam.get(3);

                Log.d(tag, "on click check" + shil +dolr + quid+ peny);
                boolean over50 = (shil+dolr+quid+peny)<=50;
                boolean nothing_entered = shil.equals(z) && dolr.equals(z) && quid.equals(z) && peny.equals(z);
                boolean negative_deposit = shil> MainActivity.getWalletoverlord().get(0) || dolr > MainActivity.getWalletoverlord().get(1)
                        || quid > MainActivity.getWalletoverlord().get(2) || peny > MainActivity.getWalletoverlord().get(3);

                Log.d(tag, "testy"+shil+dolr+quid+peny);

                if (nothing_entered){
                    Toast.makeText(BankActivity.this , "Cannot deposit nothing",Toast.LENGTH_LONG).show();
                    Log.d(tag, "preforming 0 task");
                }
                else if (negative_deposit){
                    Toast.makeText(BankActivity.this , "Nice try \n you can't deposit more than you have",Toast.LENGTH_LONG).show();
                    Log.d(tag, "preforming 1 task");
                }
                else if(!over50){
                    Toast.makeText(BankActivity.this , "You can only deposit 50 coinz in one day",Toast.LENGTH_LONG).show();
                    Log.d(tag, "preforming 3 task");
                }
                else if (!negative_deposit && !nothing_entered) {
                    setSdeposit(shil); setDdeposit(dolr); setQdeposit(quid); setPdeposit(peny);
                    Log.d(tag, "on click check" + getSdeposit() +getDdeposit() + getQdeposit()+ getPdeposit());
                    startActivity(new Intent(BankActivity.this, BankPopUp.class));
                    Log.d(tag, "preforming 2 task");
                }
                else { Log.d(tag, "LEEK");}
            }
        });

        TextView ex = findViewById(R.id.exchrates);
        ArrayList<Float> exchange = MainActivity.getCurrencyEx();
        ex.setText("1 SHIL = "+exchange.get(0) +" GOLD \n 1 DOLR = " + exchange.get(1) + " GOLD \n 1 QUID = "+ exchange.get(2)+ " GOLD \n 1 PENY = "+ exchange.get(3) +" GOLD");





    }

}
