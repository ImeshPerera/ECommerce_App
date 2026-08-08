package com.imeshperera.ecomapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.activities.CartActivity;
import com.imeshperera.ecomapp.activities.LoginActivity;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    TextView profileEmail;
    EditText profileName, profilePhone;
    Button saveBtn, logoutBtn, cartBtn;
    FirebaseAuth auth;
    FirebaseFirestore db;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profileEmail = root.findViewById(R.id.profile_email);
        profileName = root.findViewById(R.id.profile_name);
        profilePhone = root.findViewById(R.id.profile_phone);
        saveBtn = root.findViewById(R.id.profile_save_btn);
        logoutBtn = root.findViewById(R.id.profile_logout_btn);
        cartBtn = root.findViewById(R.id.profile_cart_btn);

        loadProfile();

        saveBtn.setOnClickListener(v -> saveProfile());

        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        cartBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CartActivity.class));
        });

        return root;
    }

    private void loadProfile() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();
        profileEmail.setText(auth.getCurrentUser().getEmail());

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        if (name != null) profileName.setText(name);
                        if (phone != null) profilePhone.setText(phone);
                    }
                });
    }

    private void saveProfile() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        String newName = profileName.getText().toString().trim();
        String newPhone = profilePhone.getText().toString().trim();

        if (newName.isEmpty()) {
            profileName.setError("Name cannot be empty");
            return;
        }
        if (newPhone.isEmpty()) {
            profilePhone.setError("Phone cannot be empty");
            return;
        }

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("phone", newPhone);

        db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // If document doesn't exist yet (e.g. legacy user), set it
                    HashMap<String, Object> fullMap = new HashMap<>();
                    fullMap.put("name", newName);
                    fullMap.put("phone", newPhone);
                    fullMap.put("email", auth.getCurrentUser().getEmail());
                    fullMap.put("profileImageUrl", "");
                    db.collection("users").document(uid).set(fullMap)
                            .addOnSuccessListener(aVoid2 -> Toast.makeText(getContext(), "Profile saved!", Toast.LENGTH_SHORT).show());
                });
    }
}
