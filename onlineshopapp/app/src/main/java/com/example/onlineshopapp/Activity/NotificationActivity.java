package com.example.onlineshopapp.Activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.onlineshopapp.Adapter.NotificationAdapter;
import com.example.onlineshopapp.Domain.NotificationModel;
import com.example.onlineshopapp.Helper.TinyDB;
import com.example.onlineshopapp.databinding.ActivityNotificationBinding;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;
    private TinyDB tinyDB;
    private ArrayList<NotificationModel> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tinyDB = new TinyDB(this);
        initNotificationList();

        binding.backBtn.setOnClickListener(v -> finish());

        binding.clearAllBtn.setOnClickListener(v -> {
            notifications.clear();
            tinyDB.putListObject("NotificationList", notifications);
            initNotificationList();
        });
    }

    private void initNotificationList() {
        notifications = tinyDB.getListObject("NotificationList", NotificationModel.class);
        if (notifications == null || notifications.isEmpty()) {
            notifications = new ArrayList<>();
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.notificationView.setVisibility(View.GONE);
            binding.clearAllBtn.setVisibility(View.GONE);
        } else {
            // Sắp xếp theo thời gian (mới nhất lên đầu)
            sortNotificationsByTimestamp();
            binding.emptyTxt.setVisibility(View.GONE);
            binding.notificationView.setVisibility(View.VISIBLE);
            binding.clearAllBtn.setVisibility(View.VISIBLE);
            binding.notificationView.setLayoutManager(new LinearLayoutManager(this));
            binding.notificationView.setAdapter(new NotificationAdapter(notifications, tinyDB));
        }
    }

    private void sortNotificationsByTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Collections.sort(notifications, (o1, o2) -> {
            try {
                Date date1 = sdf.parse(o1.getTimestamp());
                Date date2 = sdf.parse(o2.getTimestamp());
                return date2.compareTo(date1); // Sắp xếp giảm dần
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }
}