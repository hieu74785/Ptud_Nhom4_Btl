package com.example.onlineshopapp.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.onlineshopapp.Helper.DatabaseHelper;
import com.example.onlineshopapp.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);


        // Xóa kiểm tra trạng thái đăng nhập tự động
        // Không còn tự động vào MainActivity


        binding.loginBtn.setOnClickListener(v -> login());
        binding.registerTxt.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }


    private void login() {
        String email = binding.emailTxt.getText().toString().trim();
        String password = binding.passwordTxt.getText().toString().trim();


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        if (dbHelper.loginUser(email, password)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("email", email);
            editor.apply();


            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
