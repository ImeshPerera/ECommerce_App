package com.imeshperera.ecomapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.activities.SingleproductActivity;
import com.imeshperera.ecomapp.models.NewProductModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.ViewHolder> {

    private final Context context;
    private final List<NewProductModel> list;
    private Set<String> wishlistedNames = new HashSet<>();
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    public NewProductAdapter(Context context, List<NewProductModel> list) {
        this.context = context;
        this.list = list;
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void setWishlistedNames(Set<String> names) {
        this.wishlistedNames = names;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewProductModel model = list.get(position);

        Glide.with(context).load(model.getImg_url()).into(holder.prodImage);
        holder.prodName.setText(model.getName());
        holder.prodBrand.setText(model.getBrand());
        holder.prodPrice.setText(model.getPrice());

        // Wishlist heart
        boolean isWishlisted = wishlistedNames.contains(model.getName());
        if (holder.heartBtn != null) {
            holder.heartBtn.setImageResource(isWishlisted ? R.drawable.heart_orange_fill : R.drawable.heart);

            holder.heartBtn.setOnClickListener(v -> {
                if (auth.getCurrentUser() == null) {
                    Toast.makeText(context, "Please log in first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String uid = auth.getCurrentUser().getUid();
                String docId = model.getName() != null ? model.getName().replaceAll("\\s+", "_") : "item_" + position;

                if (wishlistedNames.contains(model.getName())) {
                    firestore.collection("wishlist").document(uid)
                            .collection("items").document(docId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                wishlistedNames.remove(model.getName());
                                notifyItemChanged(position);
                                Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("img_url", model.getImg_url());
                    data.put("name", model.getName());
                    data.put("brand", model.getBrand());
                    data.put("detail", model.getDetail());
                    data.put("price", model.getPrice());
                    data.put("rate", model.getRate());
                    data.put("type", model.getType());
                    data.put("stock", model.getStock());

                    firestore.collection("wishlist").document(uid)
                            .collection("items").document(docId)
                            .set(data)
                            .addOnSuccessListener(aVoid -> {
                                wishlistedNames.add(model.getName());
                                notifyItemChanged(position);
                                Toast.makeText(context, "Added to wishlist!", Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleproductActivity.class);
            intent.putExtra("detailed", model);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView prodImage;
        TextView prodName, prodBrand, prodPrice;
        ImageButton heartBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.new_img);
            prodName = itemView.findViewById(R.id.new_product_name);
            prodBrand = itemView.findViewById(R.id.new_product_brand);
            prodPrice = itemView.findViewById(R.id.new_price);
            heartBtn = itemView.findViewById(R.id.new_prod_wishlist_btn);
        }
    }
}
