package com.arka.cateringukom.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.arka.cateringukom.R;
import com.arka.cateringukom.api.model.User;
import com.arka.cateringukom.api.response.VerifyResponse;
import com.arka.cateringukom.main.MainActivity;
import com.arka.cateringukom.networking.ApiClient;
import com.arka.cateringukom.networking.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyActivity extends AppCompatActivity {

    private EditText inputVerificationCode;
    private Button btnVerify;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify);

        // Menyesuaikan layout agar tidak tertutup status bar / navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ambil email yang dikirim dari RegisterActivity
        email = getIntent().getStringExtra("email");

        // Inisialisasi View
        inputVerificationCode = findViewById(R.id.inputVerificationCode);
        btnVerify = findViewById(R.id.btnVerify);

        // Event tombol verify
        btnVerify.setOnClickListener(v -> {
            String verificationCode = inputVerificationCode.getText().toString().trim();
            if (verificationCode.isEmpty()) {
                Toast.makeText(VerifyActivity.this, "Kode verifikasi harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                verifyEmail(email, verificationCode);
            }
        });
    }

    private void verifyEmail(String email, String code) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // Disable tombol dan ubah warnanya
        btnVerify.setEnabled(false);
        btnVerify.setBackgroundColor(ContextCompat.getColor(VerifyActivity.this, R.color.button_disabled));


        Call<VerifyResponse> call = apiInterface.verifyEmail(email, code);
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {

                // Disable tombol dan ubah warnanya
                btnVerify.setEnabled(true);
                btnVerify.setBackgroundColor(ContextCompat.getColor(VerifyActivity.this, R.color.primary));


                if (response.isSuccessful() && response.body() != null) {
                    VerifyResponse verifyResponse = response.body();
                    // Dapatkan data user dari response
//                    User user = verifyResponse.getUser();
//                    String message = verifyResponse.getMessage();

                    // Tampilkan pesan
//                    Toast.makeText(VerifyActivity.this, message, Toast.LENGTH_SHORT).show();


                    User user = verifyResponse.getUser();
                    String message = verifyResponse.getMessage();
                    String token = verifyResponse.getToken(); // jika ada token


                    // Contoh penggunaan data user
                    Log.d("VerifyActivity", "User ID: " + user.getId());
                    Log.d("VerifyActivity", "User Name: " + user.getName());
                    Log.d("VerifyActivity", "User Email: " + user.getEmail());

                    // Simpan ke SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("token", token);                // token login
                    editor.putString("user_id", String.valueOf(user.getId()));
                    editor.putString("user_name", user.getName());
                    editor.putString("user_email", user.getEmail());
                    // dsb. jika ada field lain

                    editor.apply(); // commit perubahan

                    Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                    startActivity(intent);

                    // Jika verifikasi sukses, Anda bisa menutup activity atau pindah ke activity lain
                    finish();
                } else {
                    // Respons gagal atau body null
                    Log.e("API_ERROR", "Verifikasi gagal atau body kosong");
                    Toast.makeText(VerifyActivity.this, "Verifikasi gagal!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {

                // Disable tombol dan ubah warnanya
                btnVerify.setEnabled(true);
                btnVerify.setBackgroundColor(ContextCompat.getColor(VerifyActivity.this, R.color.primary));



                Log.e("API_ERROR", "Gagal verifikasi: " + t.getMessage());
                Toast.makeText(VerifyActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}