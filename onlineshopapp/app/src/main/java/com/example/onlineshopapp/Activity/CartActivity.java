package com.example.onlineshopapp.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.onlineshopapp.Adapter.CartAdapter;
import com.example.onlineshopapp.Domain.NotificationModel;
import com.example.onlineshopapp.Helper.ChangeNumberItemsListener;
import com.example.onlineshopapp.Helper.ManagmentCart;
import com.example.onlineshopapp.R;
import com.example.onlineshopapp.databinding.ActivityCartBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private double tax;
    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        calculatorCart();
        setVariable();
        initCartList();

        binding.checkoutBtn.setOnClickListener(v -> handleCheckout());
    }

    private void initCartList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView3.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView3.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), this, this::calculatorCart));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round((managmentCart.getTotalFee() * percentTax * 100.0)) / 100.0;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round((managmentCart.getTotalFee() * 100.0)) / 100.0;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private void handleCheckout() {
        if (managmentCart.getListCart().isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Checkout")
                .setMessage("Total: $" + binding.totalTxt.getText().toString() + "\nProceed with checkout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    managmentCart.getListCart().clear();
                    managmentCart.tinyDB.putListObject("CartList", new ArrayList<>());

                    initCartList();
                    calculatorCart();

                    // Thêm thông báo thanh toán
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    NotificationModel notification = new NotificationModel(
                            "Thanh toán thành công",
                            "Đơn hàng của bạn đã được xác nhận! Tổng cộng: " + binding.totalTxt.getText().toString(),
                            timestamp,
                            "cart"
                    );
                    addNotification(notification);

                    Toast.makeText(this, "Checkout successful! Thank you for your order.", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void addNotification(NotificationModel notification) {
        ArrayList<NotificationModel> notifications = managmentCart.tinyDB.getListObject("NotificationList", NotificationModel.class);
        if (notifications == null) notifications = new ArrayList<>();
        notifications.add(notification);
        managmentCart.tinyDB.putListObject("NotificationList", notifications);
    }
}