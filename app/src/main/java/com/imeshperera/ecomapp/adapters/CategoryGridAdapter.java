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
import com.imeshperera.ecomapp.activities.ShowAllActivity;
import com.imeshperera.ecomapp.models.CategoryModel;

import java.util.List;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.ViewHolder> {

    private final Context context;
    private final List<CategoryModel> list;

    public CategoryGridAdapter(Context context, List<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_grid_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel model = list.get(position);
        Glide.with(context).load(model.getImg_url()).into(holder.catImg);
        holder.catName.setText(model.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowAllActivity.class);
            intent.putExtra("type", model.getType());
            intent.putExtra("categoryName", model.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImg;
        TextView catName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImg = itemView.findViewById(R.id.cat_grid_img);
            catName = itemView.findViewById(R.id.cat_grid_name);
        }
    }
}
