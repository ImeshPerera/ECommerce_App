package com.imeshperera.ecomapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.activities.SingleproductActivity;
import com.imeshperera.ecomapp.models.NewProductModel;

import java.util.List;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.ViewHolder> {

    private final Context context;
    private final List<NewProductModel> list;

    public NewProductAdapter(Context context, List<NewProductModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(list.get(position).getImg_url()).into(holder.prodImage);
        holder.prodName.setText(list.get(position).getName());
        holder.prodBrand.setText(list.get(position).getBrand());
        holder.prodPrice.setText(list.get(position).getPrice());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleproductActivity.class);
            intent.putExtra("detailed", list.get(position));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView prodImage;
        TextView prodName;
        TextView prodBrand;
        TextView prodPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.new_img);
            prodName = itemView.findViewById(R.id.new_product_name);
            prodBrand = itemView.findViewById(R.id.new_product_brand);
            prodPrice = itemView.findViewById(R.id.new_price);

        }
    }
}
