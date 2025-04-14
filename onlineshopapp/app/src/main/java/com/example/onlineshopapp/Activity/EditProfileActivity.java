package com.example.onlineshopapp.Activity;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.example.onlineshopapp.Helper.DatabaseHelper;
import com.example.onlineshopapp.databinding.ActivityEditProfileBinding;


public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private DatabaseHelper dbHelper;
    private String userEmail;
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
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dbHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("email");


        // Hiển thị thông tin hiện tại
        Cursor cursor = dbHelper.getUserByEmail(userEmail);
        if (cursor != null && cursor.moveToFirst()) {
            binding.nameTxt.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            binding.phoneTxt.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            binding.addressTxt.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar"));
            if (avatar != null && !avatar.isEmpty()) {
                binding.avatarImg.setImageURI(Uri.parse(avatar));
            }
            cursor.close();
        }


        // Nhấn vào avatar để chọn ảnh
        binding.avatarImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });


        // Nút quay lại
        binding.backBtn.setOnClickListener(v -> finish());


        // Nút lưu
        binding.saveBtn.setOnClickListener(v -> saveProfile());
    }


    private void saveProfile() {
        String name = binding.nameTxt.getText().toString().trim();
        String phone = binding.phoneTxt.getText().toString().trim();
        String address = binding.addressTxt.getText().toString().trim();
        String avatar = avatarUri != null ? avatarUri.toString() : "";


        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        if (dbHelper.updateUser(userEmail, name, phone, address, avatar)) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
