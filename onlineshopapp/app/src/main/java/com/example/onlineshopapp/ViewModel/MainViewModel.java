package com.example.onlineshopapp.ViewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


import com.example.onlineshopapp.Domain.BannerModel;
import com.example.onlineshopapp.Domain.CategoryModel;
import com.example.onlineshopapp.Domain.ItemsModel;
import com.example.onlineshopapp.Repository.MainRepository;


import java.util.ArrayList;


public class MainViewModel extends ViewModel {
    private final MainRepository repository=new MainRepository();


    public LiveData<ArrayList<CategoryModel>> loadCategory(){
        return repository.loadCategory();
    }


    public LiveData<ArrayList<BannerModel>> loadBanner() {
        return repository.loadBanner();
    }


    public LiveData<ArrayList<ItemsModel>> loadPopular() {
        return repository.loadPopular();
    }
}
