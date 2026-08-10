package com.imeshperera.ecomapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.adapters.AddressAdapter;
import com.imeshperera.ecomapp.models.AddressModel;

import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends AppCompatActivity implements AddressAdapter.AddressClickListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    FloatingActionButton fabAddAddress;
    LinearLayout emptyState;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    List<AddressModel> addressModelList;
    AddressAdapter addressAdapter;

    String mode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        mode = getIntent().getStringExtra("mode");

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.address_list_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        recyclerView = findViewById(R.id.address_list_rv);
        fabAddAddress = findViewById(R.id.fab_add_address);
        emptyState = findViewById(R.id.empty_address_state);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(this, addressModelList, this);
        recyclerView.setAdapter(addressAdapter);

        fabAddAddress.setOnClickListener(v -> {
            startActivity(new Intent(AddressListActivity.this, AddEditAddressActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void loadAddresses() {
        if (auth.getCurrentUser() != null) {
            firestore.collection("users").document(auth.getCurrentUser().getUid())
                    .collection("addresses")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                addressModelList.clear();
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    AddressModel model = doc.toObject(AddressModel.class);
                                    if (model != null) {
                                        model.setId(doc.getId());
                                        addressModelList.add(model);
                                    }
                                }
                                addressAdapter.notifyDataSetChanged();
                                
                                if (addressModelList.isEmpty()) {
                                    emptyState.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    emptyState.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(AddressListActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onAddressSelected(AddressModel addressModel) {
        if ("select".equals(mode)) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_address", addressModel);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void onEditAddress(AddressModel addressModel) {
        Intent intent = new Intent(this, AddEditAddressActivity.class);
        intent.putExtra("address", addressModel);
        startActivity(intent);
    }

    @Override
    public void onDeleteAddress(AddressModel addressModel) {
        if (auth.getCurrentUser() != null && addressModel.getId() != null) {
            firestore.collection("users").document(auth.getCurrentUser().getUid())
                    .collection("addresses").document(addressModel.getId())
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddressListActivity.this, "Address deleted", Toast.LENGTH_SHORT).show();
                            loadAddresses();
                        } else {
                            Toast.makeText(AddressListActivity.this, "Failed to delete address", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
