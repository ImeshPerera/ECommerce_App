package com.imeshperera.ecomapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

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
        holder.qty.setText("Quantity: " + model.getTotalQuantity());

        Glide.with(context).load(model.getImgUrl()).into(holder.img);

        holder.deleteBtn.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null && model.getDocumentId() != null) {
                firestore.collection("AddToCart")
                        .document(auth.getCurrentUser().getUid())
                        .collection("User")
                        .document(model.getDocumentId())
                        .delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                list.remove(position);
                                notifyDataSetChanged();
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img, deleteBtn;
        TextView name, price, qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.cart_img);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            name = itemView.findViewById(R.id.cart_prod_name);
            price = itemView.findViewById(R.id.cart_prod_price);
            qty = itemView.findViewById(R.id.cart_prod_qty);
        }
    }
}
