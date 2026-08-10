package com.imeshperera.ecomapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.imeshperera.ecomapp.R;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, mobile, email, password;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);

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

    public void signup(View view) {

        String username = name.getText().toString().trim();
        String usermobile = mobile.getText().toString().trim();
        String useremail = email.getText().toString().trim();
        String userpassword = password.getText().toString();

        if (TextUtils.isEmpty(username)) {
            name.setError("Enter Your Name!");
            name.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(usermobile)) {
            mobile.setError("Enter Your Mobile Number!");
            mobile.requestFocus();
            return;
        }
        if ((usermobile.length() != 10) || (!usermobile.matches("07[01245678]\\d{7}"))) {
            mobile.setError("Mobile Number not in proper format!");
            mobile.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(useremail)) {
            email.setError("Enter Your Email!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()) {
            email.setError("Email is not valid!");
            email.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(userpassword)) {
            password.setError("Enter Your Password!");
            password.requestFocus();
            return;
        }
        if (userpassword.length() < 6) {
            password.setError("Password must be at least 6 characters!");
            password.requestFocus();
            return;
        }

        progressDialog.show();

        auth.createUserWithEmailAndPassword(useremail, userpassword)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Save user profile to Firestore
                            String uid = auth.getCurrentUser().getUid();
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", username);
                            userMap.put("phone", usermobile);
                            userMap.put("email", useremail);
                            userMap.put("profileImageUrl", "");
                            userMap.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

                            firestore.collection("users").document(uid)
                                    .set(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task2) {
                                            progressDialog.dismiss();
                                            if (task2.isSuccessful()) {
                                                Toast.makeText(RegistrationActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(RegistrationActivity.this, "Account created but profile save failed.", Toast.LENGTH_SHORT).show();
                                            }
                                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Registration Failed! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void tosignin(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }
}