package com.imeshperera.ecomapp.adapters;

import android.annotation.SuppressLint;
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

public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.ViewHolder> {

    private final Context context;
    private final List<NewProductModel> list;

    public AllProductAdapter(Context context, List<NewProductModel> list){
        this.context = context;
        this.list = list;
    }

    public void setFilteredList(List<NewProductModel> filteredList) {
        this.list.clear();
        this.list.addAll(filteredList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.all_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(list.get(position).getImg_url()).into(holder.prodImage);
        holder.prodName.setText(list.get(position).getName());
        holder.prodBrand.setText(list.get(position).getBrand());
        holder.prodPrice.setText(list.get(position).getPrice());
        String showdetail;
        String detail = list.get(position).getDetail();
        if (detail.length() > 95) {
            showdetail = detail.substring(0, 95) + "...";
        }else {
            showdetail = detail;
        }
        holder.prodDetail.setText(showdetail);

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
        TextView prodName, prodBrand, prodPrice, prodDetail,prodRate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.prod_img);
            prodName = itemView.findViewById(R.id.prod_name);
            prodBrand = itemView.findViewById(R.id.prod_brand);
            prodPrice = itemView.findViewById(R.id.prod_price);
            prodDetail = itemView.findViewById(R.id.prod_detail);

        }
    }
}
