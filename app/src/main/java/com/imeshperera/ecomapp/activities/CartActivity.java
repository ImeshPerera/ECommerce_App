package com.imeshperera.ecomapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.adapters.CartAdapter;
import com.imeshperera.ecomapp.models.MyCartModel;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    List<MyCartModel> cartModelList;
    CartAdapter cartAdapter;
    TextView overAllAmount, subtotalTv, shippingTv;
    Button buyNowBtn;
    double shippingFee = 0.0;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.my_cart_toolbar);
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

        recyclerView = findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartModelList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartModelList);
        recyclerView.setAdapter(cartAdapter);

        overAllAmount = findViewById(R.id.total_price_tv);
        subtotalTv = findViewById(R.id.subtotal_tv);
        shippingTv = findViewById(R.id.shipping_tv);
        buyNowBtn = findViewById(R.id.buy_now_btn);

        if (auth.getCurrentUser() != null) {
            firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                    .collection("User")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                    if (myCartModel != null) {
                                        myCartModel.setDocumentId(doc.getId());
                                        cartModelList.add(myCartModel);
                                    }
                                }
                                cartAdapter.notifyDataSetChanged();
                                calculateTotalAmount(cartModelList);
                            } else {
                                Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartModelList.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    android.content.Intent intent = new android.content.Intent(CartActivity.this, CheckoutActivity.class);
                    intent.putExtra("itemList", (java.io.Serializable) cartModelList);
                    intent.putExtra("shippingFee", shippingFee);
                    startActivity(intent);
                }
            }
        });
    }

    public void calculateTotalAmount(List<MyCartModel> list) {
        double subtotal = 0.0;
        for (MyCartModel model : list) {
            subtotal += model.getTotalPrice();
        }
        
        if (subtotal == 0) {
            shippingFee = 0;
        } else if (subtotal < 5000) {
            shippingFee = 350.0;
        } else {
            shippingFee = 0.0;
        }
        
        double grandTotal = subtotal + shippingFee;
        
        View emptyState = findViewById(R.id.empty_cart_state);
        if (emptyState != null) {
            if (list.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
        
        if (subtotalTv != null) subtotalTv.setText("Rs. " + String.format("%,.2f", subtotal));
        
        if (shippingTv != null) {
            if (shippingFee == 0 && subtotal > 0) {
                shippingTv.setText("FREE");
            } else if (subtotal == 0) {
                shippingTv.setText("Rs. 0.00");
            } else {
                shippingTv.setText("Rs. " + String.format("%,.2f", shippingFee));
            }
        }
        
        overAllAmount.setText("Rs. " + String.format("%,.2f", grandTotal));
    }
}
