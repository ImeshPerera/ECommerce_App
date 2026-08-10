package com.imeshperera.ecomapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imeshperera.ecomapp.R;

import java.util.List;
import java.util.Map;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, Object>> list;

    public OrderItemsAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = list.get(position);

        String name = (String) item.get("productName");
        holder.productName.setText(name);

        String qty = String.valueOf(item.get("quantity"));
        holder.quantity.setText("Qty: " + qty);

        try {
            double price = Double.parseDouble(String.valueOf(item.get("totalPrice")));
            holder.itemTotal.setText("Rs. " + String.format("%,.2f", price));
        } catch (Exception e) {
            holder.itemTotal.setText("Rs. " + String.valueOf(item.get("totalPrice")));
        }

        String imgUrl = (String) item.get("imgUrl");
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(context).load(imgUrl).into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, quantity, itemTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.order_product_img);
            productName = itemView.findViewById(R.id.order_product_name);
            productPrice = itemView.findViewById(R.id.order_product_price); // Might be unused based on layout
            quantity = itemView.findViewById(R.id.order_product_qty);
            itemTotal = itemView.findViewById(R.id.order_product_price);
        }
    }
}
