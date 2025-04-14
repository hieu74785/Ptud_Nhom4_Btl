package com.example.onlineshopapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onlineshopapp.Activity.CartActivity;
import com.example.onlineshopapp.Domain.NotificationModel;
import com.example.onlineshopapp.Helper.TinyDB;
import com.example.onlineshopapp.databinding.ViewholderNotificationBinding;
import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private ArrayList<NotificationModel> items;
    private Context context;
    private TinyDB tinyDB;

    public NotificationAdapter(ArrayList<NotificationModel> items, TinyDB tinyDB) {
        this.items = items;
        this.tinyDB = tinyDB;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderNotificationBinding binding = ViewholderNotificationBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item = items.get(position);
        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.messageTxt.setText(item.getMessage());
        holder.binding.timestampTxt.setText(item.getTimestamp());

        // Chuyển hướng khi nhấn vào thông báo
        holder.itemView.setOnClickListener(v -> {
            if ("cart".equals(item.getType())) {
                Intent intent = new Intent(context, CartActivity.class);
                context.startActivity(intent);
            }
        });

        // Xóa thông báo khi nhấn nút delete
        holder.binding.deleteBtn.setOnClickListener(v -> {
            items.remove(position);
            tinyDB.putListObject("NotificationList", items);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderNotificationBinding binding;

        public ViewHolder(ViewholderNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}