package com.imeshperera.ecomapp.activities;

import android.app.Activity;
import android.content.Intent;
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

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutActivity extends AppCompatActivity {

    private static final int PAYHERE_REQUEST = 11001;

    Toolbar toolbar;
    EditText nameEt, phoneEt, addressEt, cityEt, postalEt;
    RadioGroup paymentRadioGroup;
    TextView subtotalTv, shippingTv, totalTv;
    Button placeOrderBtn;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    List<MyCartModel> cartModelList;
    double subtotalAmount = 0.0;
    double shippingFee = 0.0;
    double totalAmount = 0.0;
    private HashMap<String, Object> pendingOrderMap;

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
        shippingTv = findViewById(R.id.checkout_shipping);
        totalTv = findViewById(R.id.checkout_total);
        placeOrderBtn = findViewById(R.id.place_order_btn);

        loadSavedAddress();

        cartModelList = (List<MyCartModel>) getIntent().getSerializableExtra("itemList");
        if (cartModelList == null) {
            cartModelList = new ArrayList<>();
        }
        shippingFee = getIntent().getDoubleExtra("shippingFee", 0.0);

        calculateTotal();

        View btnPickAddress = findViewById(R.id.btn_pick_address);
        if (btnPickAddress != null) {
            btnPickAddress.setOnClickListener(v -> {
                Intent intent = new Intent(CheckoutActivity.this, AddressListActivity.class);
                intent.putExtra("mode", "select");
                startActivityForResult(intent, 2001);
            });
        }

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void loadSavedAddress() {
        android.content.SharedPreferences sp = getSharedPreferences("CheckoutAddress", MODE_PRIVATE);
        nameEt.setText(sp.getString("name", ""));
        phoneEt.setText(sp.getString("phone", ""));
        addressEt.setText(sp.getString("address", ""));
        cityEt.setText(sp.getString("city", ""));
        postalEt.setText(sp.getString("postal", ""));
    }

    private void saveAddressLocal(String name, String phone, String address, String city, String postal) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences("CheckoutAddress", MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("address", address);
        editor.putString("city", city);
        editor.putString("postal", postal);
        editor.apply();
    }

    private void calculateTotal() {
        subtotalAmount = 0.0;
        for (MyCartModel model : cartModelList) {
            subtotalAmount += model.getTotalPrice();
        }

        if (shippingFee == 0.0 && subtotalAmount > 0 && subtotalAmount < 5000) {
            shippingFee = 350.0;
        }

        totalAmount = subtotalAmount + shippingFee;

        if (subtotalTv != null) subtotalTv.setText("Rs. " + String.format("%,.2f", subtotalAmount));
        if (shippingTv != null) {
            if (shippingFee == 0.0) {
                shippingTv.setText("FREE");
            } else {
                shippingTv.setText("Rs. " + String.format("%,.2f", shippingFee));
            }
        }
        if (totalTv != null) totalTv.setText("Rs. " + String.format("%,.2f", totalAmount));
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

        saveAddressLocal(name, phone, address, city, postal);

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
        orderMap.put("shippingFee", shippingFee);
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

        if ("Credit / Debit Card".equals(paymentMethod)) {
            pendingOrderMap = orderMap;
            startPayHerePayment(orderMap);
        } else {
            placeOrderBtn.setEnabled(false);
            saveOrderToFirestore(orderMap);
        }
    }

    private void startPayHerePayment(final HashMap<String, Object> orderMap) {
        String email = auth.getCurrentUser() != null && auth.getCurrentUser().getEmail() != null
                ? auth.getCurrentUser().getEmail()
                : "customer@example.com";

        String fullName = (String) orderMap.get("customerName");
        String firstName = "Customer";
        String lastName = "Name";
        if (fullName != null) {
            String[] parts = fullName.trim().split("\\s+", 2);
            if (parts.length > 0) {
                firstName = parts[0];
            }
            if (parts.length > 1) {
                lastName = parts[1];
            }
        }

        InitRequest req = new InitRequest();
        req.setMerchantId("1228819");       // Merchant ID
        req.setCurrency("LKR");             // Currency code
        req.setAmount(totalAmount);         // Final Amount to be charged
        
        // Generate a unique order ID for the payment transaction
        String orderId = "ORD-" + System.currentTimeMillis();
        req.setOrderId(orderId);
        
        // Setup item description
        StringBuilder descBuilder = new StringBuilder();
        for (MyCartModel item : cartModelList) {
            if (descBuilder.length() > 0) {
                descBuilder.append(", ");
            }
            descBuilder.append(item.getProductName());
        }
        String desc = descBuilder.toString();
        if (desc.length() > 100) {
            desc = desc.substring(0, 97) + "...";
        }
        req.setItemsDescription(desc.isEmpty() ? "E-Commerce Purchase" : desc);

        req.getCustomer().setFirstName(firstName);
        req.getCustomer().setLastName(lastName);
        req.getCustomer().setEmail(email);
        String rawPhone = (String) orderMap.get("customerPhone");
        String formattedPhone = rawPhone != null ? rawPhone.trim() : "";
        if (!formattedPhone.startsWith("+")) {
            if (formattedPhone.startsWith("0")) {
                formattedPhone = "+94" + formattedPhone.substring(1);
            } else if (!formattedPhone.isEmpty()) {
                formattedPhone = "+94" + formattedPhone;
            }
        }
        req.getCustomer().setPhone(formattedPhone);
        req.getCustomer().getAddress().setAddress((String) orderMap.get("shippingAddress"));
        req.getCustomer().getAddress().setCity((String) orderMap.get("city"));
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        // Optional Params
        // req.setNotifyUrl("xxxx"); 
        
        for (MyCartModel model : cartModelList) {
            double price = 0.0;
            try {
                price = Double.parseDouble(model.getProductPrice().replaceAll("[^\\d.]", ""));
            } catch (Exception e) {
                price = model.getTotalPrice();
            }
            int qty = 1;
            try {
                qty = Integer.parseInt(model.getTotalQuantity());
            } catch (Exception e) {
                // Ignore
            }
            req.getItems().add(new lk.payhere.androidsdk.model.Item(null, model.getProductName(), qty, price));
        }

        android.content.Intent intent = new android.content.Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        startActivityForResult(intent, PAYHERE_REQUEST);
    }

    private void saveOrderToFirestore(HashMap<String, Object> orderMap) {
        firestore.collection("PlacedOrders")
                .add(orderMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            String docId = task.getResult() != null ? task.getResult().getId() : "";
                            String orderDisplayId = docId.length() > 8 ? docId.substring(0, 8) : docId;
                            com.imeshperera.ecomapp.utils.NotificationHelper.showNotification(
                                    CheckoutActivity.this,
                                    "Order Placed Successfully! 🎉",
                                    "Your order #" + orderDisplayId + " has been placed and is currently Pending."
                            );
                            Toast.makeText(CheckoutActivity.this, "Order Placed Successfully!", Toast.LENGTH_LONG).show();
                            clearCartAndFinish();
                        } else {
                            placeOrderBtn.setEnabled(true);
                            Toast.makeText(CheckoutActivity.this, "Failed to place order. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2001 && resultCode == RESULT_OK && data != null) {
            com.imeshperera.ecomapp.models.AddressModel selectedAddress = 
                (com.imeshperera.ecomapp.models.AddressModel) data.getSerializableExtra("selected_address");
            if (selectedAddress != null) {
                if (selectedAddress.getName() != null) nameEt.setText(selectedAddress.getName());
                if (selectedAddress.getPhone() != null) phoneEt.setText(selectedAddress.getPhone());
                if (selectedAddress.getAddress() != null) addressEt.setText(selectedAddress.getAddress());
                if (selectedAddress.getCity() != null) cityEt.setText(selectedAddress.getCity());
                if (selectedAddress.getPostal() != null) postalEt.setText(selectedAddress.getPostal());
            }
        } else if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
                    if (pendingOrderMap != null) {
                        pendingOrderMap.put("paymentStatus", "Paid");
                        if (response.getData() != null) {
                            pendingOrderMap.put("paymentDetails", response.getData().toString());
                        }
                        placeOrderBtn.setEnabled(false);
                        saveOrderToFirestore(pendingOrderMap);
                    } else {
                        Toast.makeText(this, "Error completing order data.", Toast.LENGTH_SHORT).show();
                        placeOrderBtn.setEnabled(true);
                    }
                } else {
                    String errorMsg = (response != null) ? response.toString() : "Payment failed";
                    android.util.Log.e("PayHereError", "Payment Failed response: " + errorMsg);
                    Toast.makeText(this, "Payment Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    placeOrderBtn.setEnabled(true);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
                    android.util.Log.e("PayHereError", "Payment Canceled response: " + response.toString());
                    Toast.makeText(this, "Payment Canceled: " + response.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    android.util.Log.e("PayHereError", "Payment Canceled: null response");
                    Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
                }
                placeOrderBtn.setEnabled(true);
            }
        }
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
