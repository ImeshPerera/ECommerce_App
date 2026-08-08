package com.imeshperera.ecomapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.imeshperera.ecomapp.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEt;
    Button sendBtn;
    ImageView backBtn;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.forgot_email);
        sendBtn = findViewById(R.id.forgot_send_btn);
        backBtn = findViewById(R.id.forgot_back_btn);
        progressBar = findViewById(R.id.forgot_progress);

        backBtn.setOnClickListener(v -> finish());

        sendBtn.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {
        String email = emailEt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Please enter your email address");
            emailEt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Enter a valid email address");
            emailEt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        sendBtn.setEnabled(false);

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    sendBtn.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Password reset email sent! Check your inbox.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String errorMsg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Failed to send reset email";
                        Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
