package com.example.onlineshopapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.onlineshopapp.Adapter.CategoryAdapter;
import com.example.onlineshopapp.Adapter.PopularAdapter;
import com.example.onlineshopapp.Adapter.SliderAdapter;
import com.example.onlineshopapp.Domain.BannerModel;
import com.example.onlineshopapp.Domain.ItemsModel;
import com.example.onlineshopapp.Helper.DatabaseHelper;
import com.example.onlineshopapp.R;
import com.example.onlineshopapp.ViewModel.MainViewModel;
import com.example.onlineshopapp.databinding.ActivityMainBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private SharedPreferences preferences;
    private DatabaseHelper dbHelper;
    private ArrayList<ItemsModel> allItems; // Danh sách tất cả sản phẩm
    private PopularAdapter popularAdapter; // Adapter để cập nhật RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new MainViewModel();

        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);

        // Lấy email từ SharedPreferences
        String userEmail = preferences.getString("email", "Guest");
        binding.textView5.setText(userEmail);

        // Lấy avatar từ SQLite
        Cursor cursor = dbHelper.getUserByEmail(userEmail);
        if (cursor != null && cursor.moveToFirst()) {
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar"));
            if (avatar != null && !avatar.isEmpty()) {
                binding.imageView2.setImageURI(Uri.parse(avatar));
            }
            cursor.close();
        }

        initCategory();
        initSlider();
        initPopular();
        initSearch();
        bottomNavigation();

        // Thêm sự kiện cho cartBtn
        binding.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        // Thêm sự kiện cho nút chuông (imageView3)
        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });
    }

    private void initSearch() {
        binding.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không cần xử lý
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterItems(s.toString());
            }
        });
    }

    private void filterItems(String query) {
        if (allItems == null) return;

        ArrayList<ItemsModel> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(allItems); // Hiển thị lại toàn bộ danh sách nếu query rỗng
        } else {
            for (ItemsModel item : allItems) {
                if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        // Cập nhật RecyclerView với danh sách đã lọc
        popularAdapter = new PopularAdapter(filteredList);
        binding.popularView.setAdapter(popularAdapter);
    }

    private void bottomNavigation() {
        binding.bottomNavigation.setItemSelected(R.id.home, true);
        binding.bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if (i == R.id.home) {
                    // Đã ở MainActivity
                } else if (i == R.id.cart) {
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                } else if (i == R.id.profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
            }
        });
    }

    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        viewModel.loadPopular().observeForever(itemsModels -> {
            if (!itemsModels.isEmpty()) {
                allItems = new ArrayList<>(itemsModels); // Lưu danh sách gốc
                binding.popularView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                popularAdapter = new PopularAdapter(itemsModels);
                binding.popularView.setAdapter(popularAdapter);
                binding.popularView.setNestedScrollingEnabled(true);
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
        viewModel.loadPopular();
    }

    private void initSlider() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        viewModel.loadBanner().observeForever(bannerModels -> {
            if (bannerModels != null && !bannerModels.isEmpty()) {
                banners(bannerModels);
                binding.progressBarSlider.setVisibility(View.GONE);
            }
        });
        viewModel.loadBanner();
    }

    private void banners(ArrayList<BannerModel> bannerModels) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(binding.viewPagerSlider, bannerModels));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
    }

    private void initCategory() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        viewModel.loadCategory().observeForever(categoryModels -> {
            binding.categoryView.setLayoutManager(new LinearLayoutManager(
                    MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.categoryView.setAdapter(new CategoryAdapter(categoryModels));
            binding.categoryView.setNestedScrollingEnabled(true);
            binding.progressBarCategory.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.bottomNavigation.setItemSelected(R.id.home, true);
    }
}