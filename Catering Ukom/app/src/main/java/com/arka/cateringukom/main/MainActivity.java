package com.arka.cateringukom.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arka.cateringukom.R;
import com.arka.cateringukom.api.model.User;
import com.arka.cateringukom.history.HistoryOrderActivity;
import com.arka.cateringukom.login.LoginActivity;
import com.arka.cateringukom.networking.ApiClient;
import com.arka.cateringukom.networking.ApiInterface;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    List<ModelCategories> modelCategoriesList = new ArrayList<>();
    List<ModelTrending> modelTrendingList = new ArrayList<>();
    CategoriesAdapter categoriesAdapter;
    TrendingAdapter trendingAdapter;
    ModelCategories modelCategories;
    ModelTrending modelTrending;
    RecyclerView rvCategories, rvTrending;
    CardView cvHistory;

    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusbar();
        setInitLayout();
        setCategories();
        setTrending();
    }

    private void setInitLayout() {
        cvHistory = findViewById(R.id.cvHistory);
        cvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryOrderActivity.class);
                startActivity(intent);
            }
        });

        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        rvCategories.setHasFixedSize(true);

        rvTrending = findViewById(R.id.rvTrending);
        rvTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTrending.setHasFixedSize(true);

        ivProfile = findViewById(R.id.user_profile);

        // Ketika ikon di-klik, tampilkan Popup Menu
        ivProfile.setOnClickListener(view -> showProfileMenu(view));


        // Ambil SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);

        // Ambil data user dalam bentuk JSON
        String userJson = prefs.getString("user_data", null);

        TextView tvHaloName = findViewById(R.id.txt_username);

        if (userJson != null) {
            // Parse JSON jadi object User
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            if (user != null) {
                // Set text sesuai nama user
                tvHaloName.setText("Halo, " + user.getName());
            } else {
                // Jika user null, fallback
                tvHaloName.setText("Halo, Guest");
            }
        } else {
            // Jika belum ada user_data, fallback
            tvHaloName.setText("Halo, Guest");
        }
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        // Listener untuk item menu
        popup.setOnMenuItemClickListener(item -> onMenuItemClick(item));
        popup.show();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Panggil method untuk logout
            doLogout();
            return true;
        }
        return false;
    }

    private void doLogout() {
        // Ambil token dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            // Jika token tidak ada, langsung logout lokal
            goToLogin();
            return;
        }

        // Panggil endpoint logout di server
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> call = apiInterface.logout("Bearer " + token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Logout berhasil", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Gagal logout di server", Toast.LENGTH_SHORT).show();
                }
                // Setelah respons, bersihkan data SharedPreferences
                clearLocalSession();
                goToLogin();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Tetap hapus data lokal walaupun server tidak respons
                clearLocalSession();
                goToLogin();
            }
        });
    }

    private void clearLocalSession() {
        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        // Hapus token dan data user
        editor.clear();
        editor.apply();
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void setCategories() {
        modelCategories = new ModelCategories(R.drawable.ic_complete, "Complete Package");
        modelCategoriesList.add(modelCategories);
//        modelCategories = new ModelCategories(R.drawable.ic_saving, "Saving Package");
//        modelCategoriesList.add(modelCategories);
//        modelCategories = new ModelCategories(R.drawable.ic_healthy, "Healthy Package");
//        modelCategoriesList.add(modelCategories);
//        modelCategories = new ModelCategories(R.drawable.ic_fast, "FastFood");
//        modelCategoriesList.add(modelCategories);
//        modelCategories = new ModelCategories(R.drawable.ic_event, "Event Packages");
//        modelCategoriesList.add(modelCategories);
//        modelCategories = new ModelCategories(R.drawable.ic_more_food, "Others");
//        modelCategoriesList.add(modelCategories);

        categoriesAdapter = new CategoriesAdapter(this, modelCategoriesList);
        rvCategories.setAdapter(categoriesAdapter);
    }

    private void setTrending() {
        modelTrending = new ModelTrending(R.drawable.complete_1,"Menu 1", "2.200 disukai");
        modelTrendingList.add(modelTrending);
        modelTrending = new ModelTrending(R.drawable.complete_2,"Menu 2", "1.220 disukai");
        modelTrendingList.add(modelTrending);
        modelTrending = new ModelTrending(R.drawable.complete_3,"Menu 3", "345 disukai");
        modelTrendingList.add(modelTrending);
        modelTrending = new ModelTrending(R.drawable.complete_4,"Menu 4", "590 disukai");
        modelTrendingList.add(modelTrending);

        trendingAdapter = new TrendingAdapter(this, modelTrendingList);
        rvTrending.setAdapter(trendingAdapter);
    }

    public void setStatusbar() {
        if (Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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

}