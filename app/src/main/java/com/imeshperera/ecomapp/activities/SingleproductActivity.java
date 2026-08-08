package com.imeshperera.ecomapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.models.NewProductModel;
import com.imeshperera.ecomapp.models.MyCartModel;

public class SingleproductActivity extends AppCompatActivity {

    ImageView prodImage;
    TextView prodName, prodBrand, prodPrice, prodRate, prodDetail;
    Button addtocartBtn, buynowBtn;
    NewProductModel newProductModel = null;
    private FirebaseFirestore firestore;

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

        if (newProductModel != null) {
            Glide.with(getApplicationContext()).load(newProductModel.getImg_url()).into(prodImage);
            prodName.setText(newProductModel.getName());
            prodBrand.setText(newProductModel.getBrand());
            prodRate.setText(newProductModel.getRate());
            prodPrice.setText(newProductModel.getPrice());
            prodDetail.setText(newProductModel.getDetail());
        }
    }

    public void buynow(View view) {
        if (newProductModel == null) return;

        com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
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

        MyCartModel singleItem = new MyCartModel(
                newProductModel.getName(),
                newProductModel.getPrice(),
                "1",
                priceVal,
                newProductModel.getImg_url()
        );

        java.util.ArrayList<MyCartModel> list = new java.util.ArrayList<>();
        list.add(singleItem);

        android.content.Intent intent = new android.content.Intent(this, CheckoutActivity.class);
        intent.putExtra("itemList", list);
        startActivity(intent);
    }

    public void addtocart(View view) {
        if (newProductModel == null) return;

        com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String saveCurrentDate, saveCurrentTime;
        java.util.Calendar calForDate = java.util.Calendar.getInstance();

        java.text.SimpleDateFormat currentDate = new java.text.SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        java.text.SimpleDateFormat currentTime = new java.text.SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        double priceVal = 0.0;
        try {
            String cleanPrice = newProductModel.getPrice().replaceAll("[^\\d.]", "");
            priceVal = Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            // handle exception
        }

        final java.util.HashMap<String, Object> cartMap = new java.util.HashMap<>();
        cartMap.put("productName", newProductModel.getName());
        cartMap.put("productPrice", newProductModel.getPrice());
        cartMap.put("totalQuantity", "1");
        cartMap.put("totalPrice", priceVal);
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