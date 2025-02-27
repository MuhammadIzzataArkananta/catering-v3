package com.arka.cateringukom.history;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.arka.cateringukom.R;

public class DetailPesananActivity extends AppCompatActivity {

    TextView tvDetailNama, tvDetailJml, tvDetailHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);

        tvDetailNama = findViewById(R.id.tvDetailNama);
        tvDetailJml = findViewById(R.id.tvDetailJml);
        tvDetailHarga = findViewById(R.id.tvDetailHarga);

        // Ambil data dari intent
        Intent intent = getIntent();
        String nama = intent.getStringExtra("nama");
        String jumlah = intent.getStringExtra("jumlah");
        String harga = intent.getStringExtra("harga");

        // Set data ke TextView
        tvDetailNama.setText(nama);
        tvDetailJml.setText("Jumlah: " + jumlah);
        tvDetailHarga.setText("Harga: " + harga);
    }
}