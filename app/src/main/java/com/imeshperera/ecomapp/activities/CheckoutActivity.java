package com.imeshperera.ecomapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.models.MyCartModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText nameEt, phoneEt, addressEt, cityEt, postalEt;
    RadioGroup paymentRadioGroup;
    TextView subtotalTv;
    Button placeOrderBtn;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    List<MyCartModel> cartModelList;
    double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.checkout_toolbar);
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

        nameEt = findViewById(R.id.checkout_name);
        phoneEt = findViewById(R.id.checkout_phone);
        addressEt = findViewById(R.id.checkout_address);
        cityEt = findViewById(R.id.checkout_city);
        postalEt = findViewById(R.id.checkout_postal);
        paymentRadioGroup = findViewById(R.id.payment_radio_group);
        subtotalTv = findViewById(R.id.checkout_subtotal);
        placeOrderBtn = findViewById(R.id.place_order_btn);

        cartModelList = (List<MyCartModel>) getIntent().getSerializableExtra("itemList");
        if (cartModelList == null) {
            cartModelList = new ArrayList<>();
        }

        calculateTotal();

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (MyCartModel model : cartModelList) {
            totalAmount += model.getTotalPrice();
        }
        subtotalTv.setText("Rs. " + String.format("%,.2f", totalAmount));
    }

    private void placeOrder() {
        String name = nameEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();
        String address = addressEt.getText().toString().trim();
        String city = cityEt.getText().toString().trim();
        String postal = postalEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEt.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            phoneEt.setError("Phone is required");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            addressEt.setError("Address is required");
            return;
        }
        if (TextUtils.isEmpty(city)) {
            cityEt.setError("City is required");
            return;
        }
        if (TextUtils.isEmpty(postal)) {
            postalEt.setError("Postal code is required");
            return;
        }

        int selectedPaymentId = paymentRadioGroup.getCheckedRadioButtonId();
        RadioButton paymentButton = findViewById(selectedPaymentId);
        String paymentMethod = paymentButton != null ? paymentButton.getText().toString() : "Cash on Delivery";

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        String saveCurrentDate = currentDate.format(cal.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(cal.getTime());

        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("userId", userId);
        orderMap.put("customerName", name);
        orderMap.put("customerPhone", phone);
        orderMap.put("shippingAddress", address);
        orderMap.put("city", city);
        orderMap.put("postalCode", postal);
        orderMap.put("paymentMethod", paymentMethod);
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("orderDate", saveCurrentDate);
        orderMap.put("orderTime", saveCurrentTime);
        orderMap.put("status", "Pending");

        List<HashMap<String, Object>> itemsList = new ArrayList<>();
        for (MyCartModel model : cartModelList) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("productName", model.getProductName());
            item.put("productPrice", model.getProductPrice());
            item.put("quantity", model.getTotalQuantity());
            item.put("totalPrice", model.getTotalPrice());
            item.put("imgUrl", model.getImgUrl());
            itemsList.add(item);
        }
        orderMap.put("items", itemsList);

        placeOrderBtn.setEnabled(false);

        firestore.collection("PlacedOrders")
                .add(orderMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CheckoutActivity.this, "Order Placed Successfully!", Toast.LENGTH_LONG).show();
                            clearCartAndFinish();
                        } else {
                            placeOrderBtn.setEnabled(true);
                            Toast.makeText(CheckoutActivity.this, "Failed to place order. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void clearCartAndFinish() {
        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        // We only clear the cart items if they have documentId set (which means they were loaded from Firestore cart collection)
        boolean hasCartDocs = false;
        WriteBatch batch = firestore.batch();
        for (MyCartModel model : cartModelList) {
            if (model.getDocumentId() != null) {
                hasCartDocs = true;
                DocumentReference docRef = firestore.collection("AddToCart")
                        .document(auth.getCurrentUser().getUid())
                        .collection("User")
                        .document(model.getDocumentId());
                batch.delete(docRef);
            }
        }

        if (hasCartDocs) {
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                }
            });
        } else {
            finish();
        }
    }
}
