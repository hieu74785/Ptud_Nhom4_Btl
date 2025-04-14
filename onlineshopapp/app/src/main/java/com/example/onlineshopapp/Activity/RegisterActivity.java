package com.example.onlineshopapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshopapp.Helper.DatabaseHelper;
import com.example.onlineshopapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private DatabaseHelper dbHelper;
    private Uri avatarUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    avatarUri = result.getData().getData();
                    binding.avatarImg.setImageURI(avatarUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        // Nhấn vào avatar để chọn ảnh
        binding.avatarImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        binding.registerBtn.setOnClickListener(v -> register());
        binding.loginTxt.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void register() {
        String name = binding.nameTxt.getText().toString().trim();
        String email = binding.emailTxt.getText().toString().trim();
        String phone = binding.phoneTxt.getText().toString().trim();
        String address = binding.addressTxt.getText().toString().trim();
        String password = binding.passwordTxt.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordTxt.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.isEmailExists(email)) {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        String avatar = avatarUri != null ? avatarUri.toString() : "";

        if (dbHelper.registerUser(email, password, name, phone, address, avatar)) {
            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}