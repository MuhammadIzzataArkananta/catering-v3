package com.arka.cateringukom.order;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arka.cateringukom.R;
import com.arka.cateringukom.adapter.MenuAdapter;
import com.arka.cateringukom.api.model.Menu;
import com.arka.cateringukom.api.model.OrderItem;
import com.arka.cateringukom.api.model.User;
import com.arka.cateringukom.api.request.OrderRequest;
import com.arka.cateringukom.api.response.OrderResponse;
import com.arka.cateringukom.main.MainActivity;
import com.arka.cateringukom.networking.ApiClient;
import com.arka.cateringukom.networking.ApiInterface;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity  implements MenuAdapter.OnMenuItemChangeListener {

    public static final String DATA_TITLE = "TITLE";

    ImageView imageAdd1, imageAdd2, imageAdd3, imageAdd4, imageAdd5, imageAdd6,
            imageMinus1, imageMinus2, imageMinus3, imageMinus4, imageMinus5, imageMinus6;
    Toolbar toolbar;
    TextView tvPaket1, tvPaket2, tvPaket3, tvPaket4, tvPaket5, tvPaket6,
            tvPaket11, tvJumlahItem, tvTotalPrice;
    MaterialButton btnCheckout;
    OrderViewModel orderViewModel;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<Menu> menuList = new ArrayList<>();

    // Contoh penampung data total
    private int totalItems = 0;
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setStatusbar();
        setInitLayout();

        fetchMenuData();
    }


    private void fetchMenuData() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Menu>> call = apiInterface.getMenus();

        call.enqueue(new Callback<List<Menu>>() {
            @Override
            public void onResponse(Call<List<Menu>> call, Response<List<Menu>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    // Kosongkan list lama dan tambahkan data baru
                    menuList.clear();
                    menuList.addAll(response.body());
                    // Beritahu adapter bahwa data sudah berubah
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "Response tidak sesuai atau kosong");
                }
            }

            @Override
            public void onFailure(Call<List<Menu>> call, Throwable t) {
                Log.e("API_ERROR", "Gagal fetch menu: " + t.getMessage());
            }
        });
    }

    private void setInitLayout() {

        // Setup RecyclerView
        recyclerView = findViewById(R.id.rv_list_produk);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        // Buat adapter dengan list kosong
        adapter = new MenuAdapter(this, menuList, this);
        recyclerView.setAdapter(adapter);

        tvJumlahItem = findViewById(R.id.tvJumlahItem);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnCheckout = findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(totalPrice != 0 && totalItems != 0) {
                   doOrder();
               }
            }
        });


        // Panggil method untuk fetch data dari server
        fetchMenuData();
        updateTotal();

    }

    @Override
    public void onQuantityChanged() {
        updateTotal();
    }

    private void updateTotal() {
        int totalItemsCount = 0;
        double totalPriceCount = 0;
        for (Menu menu : menuList) {
            totalItemsCount += menu.getQuantity();
            totalPriceCount += Double.parseDouble(menu.getPrice()) * menu.getQuantity();
        }


        this.totalItems = totalItemsCount;
        this.totalPrice = totalPriceCount;


        updateTotalUI(this.totalItems, this.totalPrice);

    }


    public void setStatusbar() {
        if (Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(@NonNull Activity activity, final int bits, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

    private void doOrder() {
        // 1) Ambil user_id dari SharedPreferences (contoh)
        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        String userJson = prefs.getString("user_data", null);
        Gson gson = new Gson();
        User user = gson.fromJson(userJson, User.class);

        int userId = user.getId();  // Pastikan user_id disimpan saat login
        String token = prefs.getString("token", null); // Jika perlu Bearer token

        // 2) Kumpulkan item yang quantity > 0
        List<OrderItem> orderItems = new ArrayList<>();
        int sumPrice = 0;
        for (Menu menu : menuList) {
            if (menu.getQuantity() > 0) {


                double subtotal = Double.parseDouble(menu.getPrice()) * menu.getQuantity();
                sumPrice += subtotal;

                OrderItem item = new OrderItem(
                        menu.getId(),subtotal, menu.getQuantity()
                );
                orderItems.add(item);
            }
        }

        // 3) Buat request object
        //    Status "pending" sesuai contoh, total_price = sumPrice
        OrderRequest orderRequest = new OrderRequest(
                userId,
                sumPrice,
                "pending",
                orderItems
        );

        // 4) Panggil endpoint createOrder
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Gson gsonn = new Gson();
        Log.e("ORDER_REQUEST_JSON", gsonn.toJson(orderRequest));
        Log.e("ORDER_TOKEN", token);
        Call<OrderResponse> call = apiInterface.createOrder(
                "Bearer " + token,  // Jika API butuh Authorization: Bearer ...
                orderRequest
        );



        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                Log.e("ERROR_ORDER", response.toString());


                if (response.isSuccessful() && response.body() != null) {
                    // Sukses order
                    OrderResponse orderResp = response.body();
                    Toast.makeText(OrderActivity.this, "Order Berhasil: ",
                            Toast.LENGTH_SHORT).show();

                    // Bisa clear cart, reset quantity, dsb.
                    resetOrder();

                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // Gagal order
                    Toast.makeText(OrderActivity.this, "Order Gagal: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Terjadi kesalahan: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("ORDER_ERROR", t.getMessage());
            }
        });
    }

    /**
     * Contoh reset order setelah sukses.
     */
    private void resetOrder() {
        for (Menu menu : menuList) {
            menu.setQuantity(0);
        }
        adapter.notifyDataSetChanged();
        updateTotalUI(0, 0);
    }

    private void updateTotalUI(int items, double price) {
        tvJumlahItem.setText("Total Items: " + items);
        tvTotalPrice.setText("Total Price: Rp " + price);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
