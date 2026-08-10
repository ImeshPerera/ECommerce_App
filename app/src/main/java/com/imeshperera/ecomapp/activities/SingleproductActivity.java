package com.imeshperera.ecomapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.models.MyCartModel;
import com.imeshperera.ecomapp.models.NewProductModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SingleproductActivity extends AppCompatActivity {

    ImageView prodImage;
    TextView prodName, prodBrand, prodPrice, prodRate, prodDetail;
    Button addtocartBtn, buynowBtn;
    ImageButton qtyMinusBtn, qtyPlusBtn;
    TextView qtyCountTv, outOfStockBadge;

    NewProductModel newProductModel = null;
    private FirebaseFirestore firestore;
    private int currentQty = 1;
    private int maxStock = Integer.MAX_VALUE;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleproduct);

        firestore = FirebaseFirestore.getInstance();

        final Object obj = getIntent().getSerializableExtra("detailed");
        if (obj instanceof NewProductModel) {
            newProductModel = (NewProductModel) obj;
        }

        prodImage = findViewById(R.id.sprodimg);
        prodName = findViewById(R.id.sprodname);
        prodBrand = findViewById(R.id.sprodbrand);
        prodRate = findViewById(R.id.sprodrate);
        prodPrice = findViewById(R.id.sprodprice);
        prodDetail = findViewById(R.id.sproddetail);
        addtocartBtn = findViewById(R.id.sprodaddcart);
        buynowBtn = findViewById(R.id.sprodbuynow);
        qtyMinusBtn = findViewById(R.id.qty_minus_btn);
        qtyPlusBtn = findViewById(R.id.qty_plus_btn);
        qtyCountTv = findViewById(R.id.qty_count_tv);
        outOfStockBadge = findViewById(R.id.out_of_stock_badge);

        if (newProductModel != null) {
            Glide.with(getApplicationContext()).load(newProductModel.getImg_url()).into(prodImage);
            prodName.setText(newProductModel.getName());
            prodBrand.setText(newProductModel.getBrand());
            prodRate.setText(newProductModel.getRate());
            prodPrice.setText(newProductModel.getPrice());
            prodDetail.setText(newProductModel.getDetail());

            // Stock check
            maxStock = newProductModel.getStock();
            if (maxStock <= 0) {
                // Out of stock
                outOfStockBadge.setVisibility(View.VISIBLE);
                addtocartBtn.setEnabled(false);
                buynowBtn.setEnabled(false);
                addtocartBtn.setAlpha(0.4f);
                buynowBtn.setAlpha(0.4f);
                qtyMinusBtn.setEnabled(false);
                qtyPlusBtn.setEnabled(false);
            }
        }

        qtyMinusBtn.setOnClickListener(v -> {
            if (currentQty > 1) {
                currentQty--;
                qtyCountTv.setText(String.valueOf(currentQty));
            }
        });

        qtyPlusBtn.setOnClickListener(v -> {
            if (maxStock <= 0 || currentQty < maxStock) {
                currentQty++;
                qtyCountTv.setText(String.valueOf(currentQty));
            } else {
                Toast.makeText(this, "Only " + maxStock + " items available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void buynow(View view) {
        if (newProductModel == null) return;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
            return;
        }

        double priceVal = 0.0;
        try {
            String cleanPrice = newProductModel.getPrice().replaceAll("[^\\d.]", "");
            priceVal = Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            // handle exception
        }

        double totalPrice = priceVal * currentQty;

        MyCartModel singleItem = new MyCartModel(
                newProductModel.getName(),
                newProductModel.getPrice(),
                String.valueOf(currentQty),
                totalPrice,
                newProductModel.getImg_url()
        );

        ArrayList<MyCartModel> list = new ArrayList<>();
        list.add(singleItem);

        android.content.Intent intent = new android.content.Intent(this, CheckoutActivity.class);
        intent.putExtra("itemList", list);
        startActivity(intent);
    }

    public void addtocart(View view) {
        if (newProductModel == null) return;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        double priceVal = 0.0;
        try {
            String cleanPrice = newProductModel.getPrice().replaceAll("[^\\d.]", "");
            priceVal = Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            // handle exception
        }

        double totalPrice = priceVal * currentQty;

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productName", newProductModel.getName());
        cartMap.put("productPrice", newProductModel.getPrice());
        cartMap.put("totalQuantity", String.valueOf(currentQty));
        cartMap.put("totalPrice", totalPrice);
        cartMap.put("imgUrl", newProductModel.getImg_url());
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("currentDate", saveCurrentDate);

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").add(cartMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SingleproductActivity.this, "Added to Cart Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(SingleproductActivity.this, "Error adding to cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}