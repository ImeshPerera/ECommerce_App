package com.imeshperera.ecomapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.models.OrderModel;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private Context context;
    private List<OrderModel> list;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderModel order);
    }

    public OrdersAdapter(Context context, List<OrderModel> list, OnOrderClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = list.get(position);

        String idText = order.getOrderId();
        if (idText != null && idText.length() > 8) {
            idText = idText.substring(0, 8);
        }
        holder.tvOrderId.setText("Order #" + idText);

        holder.tvDate.setText(order.getOrderDate() + " at " + order.getOrderTime());

        int count = 0;
        if (order.getItems() != null) {
            count = order.getItems().size();
        }
        holder.tvItemCount.setText(count + " Item(s)");

        holder.tvAmount.setText("Rs. " + String.format("%,.2f", order.getTotalAmount()));

        String status = order.getStatus();
        if (status == null) status = "Pending";
        holder.tvStatus.setText(status);

        if (status.equalsIgnoreCase("Pending")) {
            holder.tvStatus.getBackground().setTint(Color.parseColor("#FF9800")); // Orange
        } else if (status.equalsIgnoreCase("Processing")) {
            holder.tvStatus.getBackground().setTint(Color.parseColor("#2196F3")); // Blue
        } else if (status.equalsIgnoreCase("Shipped")) {
            holder.tvStatus.getBackground().setTint(Color.parseColor("#9C27B0")); // Purple
        } else if (status.equalsIgnoreCase("Delivered")) {
            holder.tvStatus.getBackground().setTint(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.tvStatus.getBackground().setTint(Color.parseColor("#9E9E9E")); // Grey
        }

        holder.btnViewDetails.setOnClickListener(v -> listener.onOrderClick(order));
        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvDate, tvAmount, tvStatus, tvItemCount;
        Button btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvDate = itemView.findViewById(R.id.tv_order_date);
            tvAmount = itemView.findViewById(R.id.tv_order_amount);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvItemCount = itemView.findViewById(R.id.tv_order_items_count);
            btnViewDetails = itemView.findViewById(R.id.btn_view_order_details);
        }
    }
}
