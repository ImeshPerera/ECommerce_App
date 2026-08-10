package com.imeshperera.ecomapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.adapters.OrderItemsAdapter;
import com.imeshperera.ecomapp.models.OrderModel;
import com.imeshperera.ecomapp.utils.InvoiceGenerator;

public class OrderDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvOrderId, tvDate, tvPayment, tvShipName, tvShipAddress, tvShipPhone;
    TextView tvSubtotal, tvShipping, tvTotal;
    TextView step1Circle, step2Circle, step3Circle, step4Circle;
    View line1, line2, line3;
    RecyclerView recyclerView;
    Button btnDownload;

    OrderModel orderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        toolbar = findViewById(R.id.order_detail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        orderModel = (OrderModel) getIntent().getSerializableExtra("order");
        if (orderModel == null) {
            finish();
            return;
        }

        initViews();
        populateData();
        updateStepper();

        btnDownload.setOnClickListener(v -> {
            InvoiceGenerator.generateAndShare(this, orderModel);
        });
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.detail_order_id);
        tvDate = findViewById(R.id.detail_order_date);
        tvPayment = findViewById(R.id.detail_order_payment);
        tvShipName = findViewById(R.id.detail_shipping_name);
        tvShipAddress = findViewById(R.id.detail_shipping_address);
        tvShipPhone = findViewById(R.id.detail_shipping_phone);
        tvSubtotal = findViewById(R.id.detail_subtotal);
        tvShipping = findViewById(R.id.detail_shipping_fee);
        tvTotal = findViewById(R.id.detail_total);

        step1Circle = findViewById(R.id.step1_circle);
        step2Circle = findViewById(R.id.step2_circle);
        step3Circle = findViewById(R.id.step3_circle);
        step4Circle = findViewById(R.id.step4_circle);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);

        recyclerView = findViewById(R.id.order_items_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        
        btnDownload = findViewById(R.id.btn_download_invoice);
    }

    private void populateData() {
        tvOrderId.setText("Order ID: " + orderModel.getOrderId());
        tvDate.setText("Placed on: " + orderModel.getOrderDate() + " " + orderModel.getOrderTime());
        tvPayment.setText("Payment Method: " + orderModel.getPaymentMethod());

        tvShipName.setText(orderModel.getCustomerName());
        tvShipAddress.setText(orderModel.getShippingAddress() + ", " + orderModel.getCity() + " " + orderModel.getPostalCode());
        tvShipPhone.setText(orderModel.getCustomerPhone());

        double shippingFee = orderModel.getShippingFee();
        double totalAmount = orderModel.getTotalAmount();
        double subtotal = totalAmount - shippingFee;

        tvSubtotal.setText("Rs. " + String.format("%,.2f", subtotal));
        if (shippingFee == 0) {
            tvShipping.setText("FREE");
        } else {
            tvShipping.setText("Rs. " + String.format("%,.2f", shippingFee));
        }
        tvTotal.setText("Rs. " + String.format("%,.2f", totalAmount));

        if (orderModel.getItems() != null) {
            OrderItemsAdapter adapter = new OrderItemsAdapter(this, orderModel.getItems());
            recyclerView.setAdapter(adapter);
        }
    }

    private void updateStepper() {
        String status = orderModel.getStatus();
        if (status == null) status = "Pending";

        int step = 0;
        if (status.equalsIgnoreCase("Pending")) step = 0;
        else if (status.equalsIgnoreCase("Processing")) step = 1;
        else if (status.equalsIgnoreCase("Shipped")) step = 2;
        else if (status.equalsIgnoreCase("Delivered")) step = 3;

        int activeColor = Color.parseColor("#FF0057"); // Primary Color
        int activeLineColor = Color.parseColor("#FF0057");
        int inactiveColor = Color.parseColor("#9E9E9E");
        int inactiveLineColor = Color.parseColor("#DDDDDD");

        // Helper function for styling (needs shape drawables ideally, but modifying colors works if simple shapes)
        // Here we just change the text color and background color dynamically

        TextView[] circles = {step1Circle, step2Circle, step3Circle, step4Circle};
        View[] lines = {line1, line2, line3};

        for (int i = 0; i < circles.length; i++) {
            if (i <= step) {
                circles[i].getBackground().setTint(activeColor);
                circles[i].setText("✓"); // checkmark
            } else {
                circles[i].getBackground().setTint(inactiveColor);
                circles[i].setText(String.valueOf(i + 1));
            }
        }

        for (int i = 0; i < lines.length; i++) {
            if (i < step) {
                lines[i].setBackgroundColor(activeLineColor);
            } else {
                lines[i].setBackgroundColor(inactiveLineColor);
            }
        }
    }
}
