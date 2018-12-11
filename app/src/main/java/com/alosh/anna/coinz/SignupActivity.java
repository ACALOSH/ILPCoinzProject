package com.alosh.anna.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        CollectionReference Users = db.collection("Users");

        //if there is a current user signed in, then goes directly to main view
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_signup);
        inputEmail = findViewById(R.id.Email);
        inputPassword = findViewById(R.id.password);
        Button btnSignup = findViewById(R.id.SignUp);
        Button btnLogin = findViewById(R.id.Login);


        //goes to login page
        btnLogin.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));

        //setting sign up button
        btnSignup.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }


            //create user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignupActivity.this, "Created account!" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                            //sets baseline info in firestore
                            Map<String, Object> data1 = new HashMap<>();
                            //array of coinz in wallet, in the order shil, dolr, quid, peny
                            data1.put("Wallet", Arrays.asList("0","0","0","0"));
                            data1.put("BankCoinz", 0);
                            data1.put("Friends", Arrays.asList("automaticfriend@notlonely.com"));
                            Users.document(mAuth.getCurrentUser().getEmail()).set(data1);

                            //goes to main
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish();
                        }
                    });

        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        }
    }
}
