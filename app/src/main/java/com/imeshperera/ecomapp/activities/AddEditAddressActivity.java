package com.imeshperera.ecomapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.models.AddressModel;

import java.util.HashMap;
import java.util.Map;

public class AddEditAddressActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText etLabel, etName, etPhone, etStreet, etCity, etPostal;
    CheckBox cbDefault;
    Button btnSave;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    AddressModel existingAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.add_address_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        etLabel = findViewById(R.id.et_address_label);
        etName = findViewById(R.id.et_address_name);
        etPhone = findViewById(R.id.et_address_phone);
        etStreet = findViewById(R.id.et_address_street);
        etCity = findViewById(R.id.et_address_city);
        etPostal = findViewById(R.id.et_address_postal);
        cbDefault = findViewById(R.id.cb_set_default);
        btnSave = findViewById(R.id.btn_save_address);

        if (getIntent().hasExtra("address")) {
            existingAddress = (AddressModel) getIntent().getSerializableExtra("address");
            if (existingAddress != null) {
                etLabel.setText(existingAddress.getLabel());
                etName.setText(existingAddress.getName());
                etPhone.setText(existingAddress.getPhone());
                etStreet.setText(existingAddress.getAddress());
                etCity.setText(existingAddress.getCity());
                etPostal.setText(existingAddress.getPostal());
                cbDefault.setChecked(existingAddress.isDefault());
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Edit Address");
                }
            }
        }

        btnSave.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String label = etLabel.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String street = etStreet.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String postal = etPostal.getText().toString().trim();
        boolean isDefault = cbDefault.isChecked();

        if (TextUtils.isEmpty(label)) { etLabel.setError("Required"); return; }
        if (TextUtils.isEmpty(name)) { etName.setError("Required"); return; }
        if (TextUtils.isEmpty(phone)) { etPhone.setError("Required"); return; }
        if (TextUtils.isEmpty(street)) { etStreet.setError("Required"); return; }
        if (TextUtils.isEmpty(city)) { etCity.setError("Required"); return; }
        if (TextUtils.isEmpty(postal)) { etPostal.setError("Required"); return; }

        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("label", label);
        addressMap.put("name", name);
        addressMap.put("phone", phone);
        addressMap.put("address", street);
        addressMap.put("city", city);
        addressMap.put("postal", postal);
        addressMap.put("default", isDefault); // Store as "default" to map to isDefault() boolean getter

        if (existingAddress != null && existingAddress.getId() != null) {
            firestore.collection("users").document(uid).collection("addresses").document(existingAddress.getId())
                    .update(addressMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Address updated", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            firestore.collection("users").document(uid).collection("addresses")
                    .add(addressMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Address saved", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
