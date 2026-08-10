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
import com.imeshperera.ecomapp.models.NewProductModel;
import java.util.List;

public class RelatedProductAdapter extends RecyclerView.Adapter<RelatedProductAdapter.ViewHolder> {

    private Context context;
    private List<NewProductModel> list;
    private OnRelatedProductClickListener listener;

    public interface OnRelatedProductClickListener {
        void onProductClick(NewProductModel product);
    }

    public RelatedProductAdapter(Context context, List<NewProductModel> list, OnRelatedProductClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.related_product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewProductModel product = list.get(position);
        Glide.with(context).load(product.getImg_url()).into(holder.relatedProdImg);
        holder.relatedProdName.setText(product.getName());
        holder.relatedProdPrice.setText("Rs." + product.getPrice());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView relatedProdImg;
        TextView relatedProdName, relatedProdPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relatedProdImg = itemView.findViewById(R.id.related_prod_img);
            relatedProdName = itemView.findViewById(R.id.related_prod_name);
            relatedProdPrice = itemView.findViewById(R.id.related_prod_price);
        }
    }
}
