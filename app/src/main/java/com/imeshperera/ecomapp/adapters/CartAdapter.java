package com.imeshperera.ecomapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.imeshperera.ecomapp.activities.CartActivity;
import com.imeshperera.ecomapp.models.MyCartModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<MyCartModel> list;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public CartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.list = list;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MyCartModel model = list.get(position);

        holder.name.setText(model.getProductName());
        holder.price.setText("Rs. " + model.getProductPrice());

        int qty = 1;
        try {
            qty = Integer.parseInt(model.getTotalQuantity());
        } catch (Exception e) {
            qty = 1;
        }
        holder.qtyTv.setText(String.valueOf(qty));

        double itemTotal = model.getTotalPrice();
        holder.itemTotal.setText("Total: Rs. " + String.format("%,.2f", itemTotal));

        Glide.with(context).load(model.getImgUrl()).into(holder.img);

        // Minus button
        holder.qtyMinus.setOnClickListener(v -> {
            int currentQty;
            try {
                currentQty = Integer.parseInt(model.getTotalQuantity());
            } catch (Exception e) {
                currentQty = 1;
            }
            if (currentQty > 1) {
                int newQty = currentQty - 1;
                updateQuantityInFirestore(model, newQty, position);
            } else {
                Toast.makeText(context, "Minimum quantity is 1", Toast.LENGTH_SHORT).show();
            }
        });

        // Plus button
        holder.qtyPlus.setOnClickListener(v -> {
            int currentQty;
            try {
                currentQty = Integer.parseInt(model.getTotalQuantity());
            } catch (Exception e) {
                currentQty = 1;
            }
            int newQty = currentQty + 1;
            updateQuantityInFirestore(model, newQty, position);
        });

        // Delete button
        holder.deleteBtn.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null && model.getDocumentId() != null) {
                firestore.collection("AddToCart")
                        .document(auth.getCurrentUser().getUid())
                        .collection("User")
                        .document(model.getDocumentId())
                        .delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                int pos = list.indexOf(model);
                                if (pos >= 0) {
                                    list.remove(pos);
                                    notifyItemRemoved(pos);
                                    notifyItemRangeChanged(pos, list.size());
                                }
                                if (context instanceof CartActivity) {
                                    ((CartActivity) context).calculateTotalAmount(list);
                                }
                                Toast.makeText(context, "Item Removed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void updateQuantityInFirestore(MyCartModel model, int newQty, int position) {
        if (auth.getCurrentUser() == null || model.getDocumentId() == null) return;

        // Recalculate total price based on unit price
        double unitPrice = 0.0;
        try {
            String cleanPrice = model.getProductPrice().replaceAll("[^\\d.]", "");
            unitPrice = Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            unitPrice = model.getTotalPrice() / Math.max(1, Double.parseDouble(model.getTotalQuantity()));
        }
        double newTotalPrice = unitPrice * newQty;

        Map<String, Object> updates = new HashMap<>();
        updates.put("totalQuantity", String.valueOf(newQty));
        updates.put("totalPrice", newTotalPrice);

        firestore.collection("AddToCart")
                .document(auth.getCurrentUser().getUid())
                .collection("User")
                .document(model.getDocumentId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    model.setTotalQuantity(String.valueOf(newQty));
                    model.setTotalPrice(newTotalPrice);
                    notifyItemChanged(list.indexOf(model));
                    if (context instanceof CartActivity) {
                        ((CartActivity) context).calculateTotalAmount(list);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img, deleteBtn;
        TextView name, price, qtyTv, itemTotal;
        ImageButton qtyMinus, qtyPlus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.cart_img);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            name = itemView.findViewById(R.id.cart_prod_name);
            price = itemView.findViewById(R.id.cart_prod_price);
            qtyTv = itemView.findViewById(R.id.cart_qty_tv);
            qtyMinus = itemView.findViewById(R.id.cart_qty_minus);
            qtyPlus = itemView.findViewById(R.id.cart_qty_plus);
            itemTotal = itemView.findViewById(R.id.cart_item_total);
        }
    }
}
