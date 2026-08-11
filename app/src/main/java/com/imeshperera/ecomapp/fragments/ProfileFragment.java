package com.imeshperera.ecomapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
        cartBtn = root.findViewById(R.id.profile_cart_btn);
        profileDisplayName = root.findViewById(R.id.profile_display_name);
        avatarImg = root.findViewById(R.id.profile_avatar_img);
        avatarEditBtn = root.findViewById(R.id.profile_avatar_edit_btn);

        if (avatarEditBtn != null) {
            avatarEditBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3001);
            });
        }

        logoutBtn = root.findViewById(R.id.profile_logout_btn);

        if (saveBtn != null) {
            saveBtn.setOnClickListener(v -> saveProfile());
        }

        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v -> {
                auth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        Button addressBtn = root.findViewById(R.id.profile_address_btn);
        if (addressBtn != null) {
            addressBtn.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), com.imeshperera.ecomapp.activities.AddressListActivity.class));
            });
        }

        if (cartBtn != null) {
            cartBtn.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), CartActivity.class));
            });
        }

        loadProfile();

        return root;
    }

    private TextView profileDisplayName;
    private ImageView avatarImg, avatarEditBtn;

    private void loadProfile() {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        profileEmail.setText(user.getEmail());

        // Default Google / Auth profile fallback
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            profileName.setText(user.getDisplayName());
            if (profileDisplayName != null) profileDisplayName.setText(user.getDisplayName());
        }
        if (user.getPhotoUrl() != null && avatarImg != null && getContext() != null) {
            com.bumptech.glide.Glide.with(getContext()).load(user.getPhotoUrl()).into(avatarImg);
        }

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String imageUrl = documentSnapshot.getString("profileImageUrl");

                        if (name != null && !name.isEmpty()) {
                            profileName.setText(name);
                            if (profileDisplayName != null) profileDisplayName.setText(name);
                        }
                        if (phone != null) profilePhone.setText(phone);
                        if (imageUrl != null && !imageUrl.isEmpty() && avatarImg != null && getContext() != null) {
                            com.bumptech.glide.Glide.with(getContext()).load(imageUrl).into(avatarImg);
                        }
                    }
                });
    }

    private android.net.Uri selectedImageUri = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3001 && resultCode == android.app.Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if (avatarImg != null) {
                avatarImg.setImageURI(selectedImageUri);
                Toast.makeText(getContext(), "Image selected! Click 'Save Changes' to upload.", Toast.LENGTH_SHORT).show();
            }
        }
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

        if (selectedImageUri != null) {
            // Upload to Firebase Storage
            com.google.firebase.storage.StorageReference storageRef = 
                    com.google.firebase.storage.FirebaseStorage.getInstance().getReference()
                            .child("profile_images/" + uid + ".jpg");

            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                updateFirestoreProfile(uid, newName, newPhone, downloadUrl);
                            }))
                    .addOnFailureListener(e -> {
                        // Fallback update without new image URL if storage fails/disabled
                        updateFirestoreProfile(uid, newName, newPhone, null);
                    });
        } else {
            updateFirestoreProfile(uid, newName, newPhone, null);
        }
    }

    private void updateFirestoreProfile(String uid, String name, String phone, @Nullable String imageUrl) {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        if (imageUrl != null) {
            updates.put("profileImageUrl", imageUrl);
        }

        db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (profileDisplayName != null) profileDisplayName.setText(name);
                    Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    HashMap<String, Object> fullMap = new HashMap<>();
                    fullMap.put("name", name);
                    fullMap.put("phone", phone);
                    fullMap.put("email", auth.getCurrentUser().getEmail());
                    fullMap.put("profileImageUrl", imageUrl != null ? imageUrl : "");
                    db.collection("users").document(uid).set(fullMap)
                            .addOnSuccessListener(aVoid2 -> {
                                if (profileDisplayName != null) profileDisplayName.setText(name);
                                Toast.makeText(getContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                            });
                });
    }
}
