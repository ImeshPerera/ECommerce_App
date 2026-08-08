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

    EditText name,mobile,email,password;
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
                }else {
                    Toast.makeText(LoginActivity.this,"Log In Failed!"+task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}