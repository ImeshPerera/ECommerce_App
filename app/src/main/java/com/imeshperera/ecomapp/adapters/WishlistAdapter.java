package com.imeshperera.ecomapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.activities.SingleproductActivity;
import com.imeshperera.ecomapp.models.NewProductModel;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    public interface OnWishlistActionListener {
        void onRemoveFromWishlist(NewProductModel product, int position);
    }

    private final Context context;
    private final List<NewProductModel> list;
    private final OnWishlistActionListener listener;

    public WishlistAdapter(Context context, List<NewProductModel> list, OnWishlistActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NewProductModel model = list.get(position);

        Glide.with(context).load(model.getImg_url()).into(holder.prodImage);
        holder.prodName.setText(model.getName());
        holder.prodBrand.setText(model.getBrand());
        holder.prodPrice.setText("Rs. " + model.getPrice());

        holder.removeHeartBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveFromWishlist(model, position);
            }
        });

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
        ImageButton removeHeartBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.wishlist_item_img);
            prodName = itemView.findViewById(R.id.wishlist_item_name);
            prodBrand = itemView.findViewById(R.id.wishlist_item_brand);
            prodPrice = itemView.findViewById(R.id.wishlist_item_price);
            removeHeartBtn = itemView.findViewById(R.id.wishlist_remove_btn);
        }
    }
}
