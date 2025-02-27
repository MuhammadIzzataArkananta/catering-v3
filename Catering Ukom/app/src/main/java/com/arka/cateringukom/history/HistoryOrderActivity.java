package com.arka.cateringukom.history;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arka.cateringukom.R;
import com.arka.cateringukom.api.response.OrderResponse;
import com.arka.cateringukom.networking.ApiClient;
import com.arka.cateringukom.networking.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryOrderActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tvNotFound;
    private HistoryAdapter adapter;
    // Tipe list sesuai dengan API yang mengembalikan List<OrderResponse>
    private List<OrderResponse> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);

        setToolbar();
        setInitLayout();
    }

    // Untuk toolbar
    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    // Inisialisasi view dan menampilkan data list
    private void setInitLayout() {
        recyclerView = findViewById(R.id.rvHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvNotFound = findViewById(R.id.tvNotFound);

        // Buat adapter dengan list order
        adapter = new HistoryAdapter(this, orderList);
        recyclerView.setAdapter(adapter);

        // Panggil method untuk fetch data dari server
        fetchOrderHistory();
    }

    private void fetchOrderHistory() {
        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<OrderResponse>> call = apiInterface.getUserOrder("Bearer " + token);

        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {

                Log.e("HISTORY_RESPONSE", response.toString());

                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    // Tampilkan not found jika list kosong
                    tvNotFound.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Log.e("API_ERROR", "Response tidak sesuai atau kosong");
                    tvNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.e("API_ERROR", "Gagal fetch order: " + t.getMessage());
                tvNotFound.setVisibility(View.VISIBLE);
            }
        });
    }
}
