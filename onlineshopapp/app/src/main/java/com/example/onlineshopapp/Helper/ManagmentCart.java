package com.example.onlineshopapp.Helper;

import android.content.Context;
import android.widget.Toast;
import com.example.onlineshopapp.Domain.ItemsModel;
import com.example.onlineshopapp.Domain.NotificationModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ManagmentCart {
    private Context context;
    public TinyDB tinyDB;

    public ManagmentCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertItem(ItemsModel item) {
        ArrayList<ItemsModel> listItem = getListCart();
        boolean existAlready = false;
        int n = 0;
        for (int y = 0; y < listItem.size(); y++) {
            if (listItem.get(y).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = y;
                break;
            }
        }
        if (existAlready) {
            listItem.get(n).setNumberinCart(item.getNumberinCart());
        } else {
            listItem.add(item);
        }
        tinyDB.putListObject("CartList", listItem);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();

        // Thêm thông báo
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        NotificationModel notification = new NotificationModel(
                "Sản phẩm mới trong giỏ",
                "Bạn vừa thêm " + item.getTitle() + " vào giỏ hàng!",
                timestamp,
                "cart"
        );
        addNotification(notification);
    }

    private void addNotification(NotificationModel notification) {
        ArrayList<NotificationModel> notifications = tinyDB.getListObject("NotificationList", NotificationModel.class);
        if (notifications == null) notifications = new ArrayList<>();
        notifications.add(notification);
        tinyDB.putListObject("NotificationList", notifications);
    }

    public ArrayList<ItemsModel> getListCart() {
        return tinyDB.getListObject("CartList", ItemsModel.class);
    }

    public void minusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listItem.get(position).getNumberinCart() == 1) {
            listItem.remove(position);
        } else {
            listItem.get(position).setNumberinCart(listItem.get(position).getNumberinCart() - 1);
        }
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.changed();
    }

    public void plusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listItem.get(position).setNumberinCart(listItem.get(position).getNumberinCart() + 1);
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.changed();
    }

    public Double getTotalFee() {
        ArrayList<ItemsModel> listItem2 = getListCart();
        double fee = 0;
        for (int i = 0; i < listItem2.size(); i++) {
            fee = fee + (listItem2.get(i).getPrice() * listItem2.get(i).getNumberinCart());
        }
        return fee;
    }
}