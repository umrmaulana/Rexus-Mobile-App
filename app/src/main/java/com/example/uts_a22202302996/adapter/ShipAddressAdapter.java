package com.example.uts_a22202302996.adapter;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_a22202302996.R;
import com.example.uts_a22202302996.activity.EditAddressActivity;
import com.example.uts_a22202302996.model.ShipAddress;

import java.io.Serializable;
import java.util.List;


public class ShipAddressAdapter extends RecyclerView.Adapter<ShipAddressAdapter.ViewHolder> {
    private List<ShipAddress> addressList;
    private Context context;
    private OnAddressSelectedListener listener;

    public interface OnAddressSelectedListener {
        void onSelected(int addressId);
        void onDelete(int addressId);
        void onEdit(int addressId);
    }

    public ShipAddressAdapter(Context context, List<ShipAddress> addressList, OnAddressSelectedListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShipAddress address = addressList.get(position);
        holder.tvName.setText(address.getRecipt_name());
        holder.tvAddress.setText(address.getAddress());
        holder.tvProvince.setText(address.getProvince_name());
        holder.tvCity.setText(address.getCity_name());
        holder.tvPostalCode.setText(String.valueOf(address.getPostal_code()));
        holder.tvPhone.setText(address.getNo_tlp());
        holder.radioSelected.setChecked("1".equals(address.getIs_active()));

        // Tambahkan onClickListener untuk seluruh item view
        holder.itemView.setOnClickListener(v -> {
            // Panggil listener untuk update alamat aktif
            listener.onSelected(address.getId());

            // Update status checked untuk semua item
            for (ShipAddress addr : addressList) {
                addr.setIs_active(addr.getId() == address.getId() ? "1" : "0");
            }
            notifyDataSetChanged();
        });

        holder.radioSelected.setOnClickListener(v -> {
            // Mencegah perubahan manual pada radio button
            holder.radioSelected.setChecked("1".equals(address.getIs_active()));
            // Alihkan ke itemView click
            holder.itemView.performClick();
        });

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(address.getId()));
        holder.btnDelete.setVisibility(address.getIs_active().equals("1") ? View.GONE : View.VISIBLE);
        holder.btnEdit.setOnClickListener(v ->listener.onEdit(address.getId()));

    }

    private void startActivityForResult(Intent intent, int i) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, i);
        } else {
            throw new IllegalStateException("Context must be an instance of Activity");
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvProvince, tvCity, tvPostalCode, tvPhone;
        RadioButton radioSelected;
        ImageView btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvProvince = itemView.findViewById(R.id.tvProvince);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvPostalCode = itemView.findViewById(R.id.tvPostalCode);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            radioSelected = itemView.findViewById(R.id.radioSelected);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}

