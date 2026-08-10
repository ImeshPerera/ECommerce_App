package com.imeshperera.ecomapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.models.AddressModel;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<AddressModel> list;
    private AddressClickListener listener;

    public interface AddressClickListener {
        void onAddressSelected(AddressModel addressModel);
        void onEditAddress(AddressModel addressModel);
        void onDeleteAddress(AddressModel addressModel);
    }

    public AddressAdapter(Context context, List<AddressModel> list, AddressClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressModel addressModel = list.get(position);
        
        String label = addressModel.getLabel();
        if (addressModel.isDefault()) {
            label += " (Default)";
        }
        holder.tvLabel.setText(label);
        holder.tvName.setText(addressModel.getName());
        holder.tvAddress.setText(addressModel.getAddress() + ", " + addressModel.getCity() + " " + addressModel.getPostal());
        holder.tvPhone.setText(addressModel.getPhone());
        
        holder.radioDefault.setChecked(addressModel.isDefault());
        
        holder.itemView.setOnClickListener(v -> listener.onAddressSelected(addressModel));
        holder.radioDefault.setOnClickListener(v -> listener.onAddressSelected(addressModel));
        
        holder.btnEdit.setOnClickListener(v -> listener.onEditAddress(addressModel));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteAddress(addressModel));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvName, tvAddress, tvPhone;
        RadioButton radioDefault;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_address_label);
            tvName = itemView.findViewById(R.id.tv_address_name);
            tvAddress = itemView.findViewById(R.id.tv_address_full);
            tvPhone = itemView.findViewById(R.id.tv_address_phone);
            radioDefault = itemView.findViewById(R.id.radio_default);
            btnEdit = itemView.findViewById(R.id.btn_edit_address);
            btnDelete = itemView.findViewById(R.id.btn_delete_address);
        }
    }
}
