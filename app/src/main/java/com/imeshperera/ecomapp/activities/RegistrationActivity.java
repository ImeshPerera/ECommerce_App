package com.imeshperera.ecomapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.imeshperera.ecomapp.R;

public class RegistrationActivity extends AppCompatActivity {

    EditText name,mobile,email,password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            //startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
            //finish();
        }

        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        View googleBtn = findViewById(R.id.google_signin_btn);
        if (googleBtn != null) {
            googleBtn.setOnClickListener(v -> {
                com.google.android.gms.auth.api.signin.GoogleSignInClient client = com.imeshperera.ecomapp.utils.GoogleSignInHelper.getClient(this);
                startActivityForResult(client.getSignInIntent(), 1001);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                com.google.android.gms.auth.api.signin.GoogleSignInAccount account = task.getResult(com.google.android.gms.common.api.ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    com.google.firebase.auth.AuthCredential credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(credential).addOnCompleteListener(this, t -> {
                        if (t.isSuccessful()) {
                            Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Authentication Failed: " + (t.getException() != null ? t.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (com.google.android.gms.common.api.ApiException e) {
                Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void signup(View view){

        String username = name.getText().toString();
        String usermobile = mobile.getText().toString();
        String useremail = email.getText().toString();
        String userpassword = password.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this,"Enter Your Name !",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(usermobile)){
            Toast.makeText(this,"Enter Your Mobile Number !",Toast.LENGTH_SHORT).show();
            return;
        }
        if((usermobile.length() != 10) || (!usermobile.matches("07[01245678]\\d{7}"))){
            Toast.makeText(this,"Mobile Number not in proper format !",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(useremail)){
            Toast.makeText(this,"Enter Your Email !",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
            Toast.makeText(this,"Email is not valid !",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userpassword)){
            Toast.makeText(this,"Enter Your Password !",Toast.LENGTH_SHORT).show();
            return;
        }
        if(userpassword.length() < 6){
            Toast.makeText(this,"Password is too short ! \nminimum character number is 6",Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(useremail,userpassword).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegistrationActivity.this,"Successfully Registered!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                }else {
                    Toast.makeText(RegistrationActivity.this,"Registration Failed!"+task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void tosignin(View view){
        startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
    }

}