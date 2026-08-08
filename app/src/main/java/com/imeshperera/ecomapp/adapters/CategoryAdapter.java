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
import com.imeshperera.ecomapp.models.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<CategoryModel> list;

    public CategoryAdapter(Context context, List<CategoryModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(list.get(position).getImg_url()).into(holder.catImage);
        holder.catName.setText(list.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(context, com.imeshperera.ecomapp.activities.ShowAllActivity.class);
                intent.putExtra("type", list.get(position).getType());
                intent.putExtra("categoryName", list.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView catImage;
        TextView catName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.cat_img);
            catName = itemView.findViewById(R.id.cat_name);
        }
    }
}
