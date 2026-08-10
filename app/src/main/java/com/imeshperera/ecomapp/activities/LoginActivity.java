package com.imeshperera.ecomapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.imeshperera.ecomapp.R;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    private FirebaseAuth auth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        sharedPreferences = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime",true);
        if(isFirstTime){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime",false);
            editor.commit();

            Intent intent = new Intent(LoginActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }

        View googleBtn = findViewById(R.id.google_signin_btn2);
        if (googleBtn != null) {
            googleBtn.setOnClickListener(v -> {
                com.google.android.gms.auth.api.signin.GoogleSignInClient client = com.imeshperera.ecomapp.utils.GoogleSignInHelper.getClient(this);
                startActivityForResult(client.getSignInIntent(), 1002);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {
            Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                com.google.android.gms.auth.api.signin.GoogleSignInAccount account = task.getResult(com.google.android.gms.common.api.ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    com.google.firebase.auth.AuthCredential credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(credential).addOnCompleteListener(this, t -> {
                        if (t.isSuccessful()) {
                            Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

    public void toForgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    public void toForgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    public void tosignup(View view){
        startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
    }

    public void signin(View view){

        String useremail = email.getText().toString();
        String userpassword = password.getText().toString();

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

        auth.signInWithEmailAndPassword(useremail,userpassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this,"Log In Successful!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this,"Log In Failed!"+ (task.getException() != null ? task.getException().getMessage() : ""),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}