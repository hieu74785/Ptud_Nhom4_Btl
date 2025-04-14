package com.example.onlineshopapp.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.onlineshopapp.databinding.ActivitySplashBinding;


public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Khởi tạo SharedPreferences
        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);


        // Xóa trạng thái đăng nhập mỗi khi mở ứng dụng
        preferences.edit().clear().apply();


        // Nút "Let's Get Started" luôn dẫn đến LoginActivity
        binding.startBtn.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            // Không gọi finish() để người dùng có thể quay lại Splash nếu cần
        });
    }
}
