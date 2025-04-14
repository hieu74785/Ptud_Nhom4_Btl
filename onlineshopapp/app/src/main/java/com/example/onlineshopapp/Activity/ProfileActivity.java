package com.example.onlineshopapp.Activity;




import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;




import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;




import com.example.onlineshopapp.Helper.DatabaseHelper;
import com.example.onlineshopapp.databinding.ActivityProfileBinding;




public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private SharedPreferences preferences;
    private DatabaseHelper dbHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);




        // Lấy email từ SharedPreferences
        String userEmail = preferences.getString("email", "Guest");




        // Lấy thông tin người dùng từ SQLite
        Cursor cursor = dbHelper.getUserByEmail(userEmail);
        if (cursor != null && cursor.moveToFirst()) {
            binding.emailTxt.setText(userEmail);
            binding.nameTxt.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            binding.phoneTxt.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            binding.addressTxt.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar"));
            if (avatar != null && !avatar.isEmpty()) {
                binding.avatarImg.setImageURI(Uri.parse(avatar));
            }
            cursor.close();
        }




        // Nút quay lại
        binding.backBtn.setOnClickListener(v -> finish());




        // Nút chỉnh sửa
        binding.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });




        // Nút đăng xuất
        binding.logoutBtn.setOnClickListener(v -> {
            preferences.edit().clear().apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }




    @Override
    protected void onResume() {
        super.onResume();
        String userEmail = preferences.getString("email", "Guest");
        Cursor cursor = dbHelper.getUserByEmail(userEmail);
        if (cursor != null && cursor.moveToFirst()) {
            binding.emailTxt.setText(userEmail);
            binding.nameTxt.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            binding.phoneTxt.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            binding.addressTxt.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar"));
            if (avatar != null && !avatar.isEmpty()) {
                binding.avatarImg.setImageURI(Uri.parse(avatar));
            }
            cursor.close();
        }
    }
}
