package com.imeshperera.ecomapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.adapters.AllProductAdapter;
import com.imeshperera.ecomapp.models.NewProductModel;

import java.util.ArrayList;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    AllProductAdapter allProductAdapter;
    List<NewProductModel> newProductModelList;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.show_all_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        String type = getIntent().getStringExtra("type");
        String categoryName = getIntent().getStringExtra("categoryName");

        if (categoryName != null && !categoryName.isEmpty()) {
            getSupportActionBar().setTitle(categoryName);
        } else {
            getSupportActionBar().setTitle("Products");
        }

        recyclerView = findViewById(R.id.show_all_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        newProductModelList = new ArrayList<>();
        allProductAdapter = new AllProductAdapter(this, newProductModelList);
        recyclerView.setAdapter(allProductAdapter);

        if (type == null || type.isEmpty()) {
            firestore.collection("New Products")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    NewProductModel model = doc.toObject(NewProductModel.class);
                                    newProductModelList.add(model);
                                }
                                allProductAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ShowAllActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            firestore.collection("New Products")
                    .whereEqualTo("type", type)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    NewProductModel model = doc.toObject(NewProductModel.class);
                                    newProductModelList.add(model);
                                }
                                allProductAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ShowAllActivity.this, "Failed to load products for category", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
