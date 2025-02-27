package com.arka.cateringukom.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.arka.cateringukom.R;
import com.arka.cateringukom.api.response.RegisterResponse;
import com.arka.cateringukom.login.VerifyActivity;
import com.arka.cateringukom.networking.ApiClient;
import com.arka.cateringukom.networking.ApiInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText inputEmail, inputUser, inputPassword;
    MaterialButton btnRegister;
    String strEmail, strUser, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setInitLayout();
        setInputData();
    }

    private void setInitLayout() {
        inputEmail = findViewById(R.id.inputEmail);
        inputUser = findViewById(R.id.inputUser);
        inputPassword = findViewById(R.id.inputPassword);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setInputData() {
        btnRegister.setOnClickListener(v -> {
            strEmail = inputEmail.getText().toString().trim();
            strUser = inputUser.getText().toString().trim();
            strPassword = inputPassword.getText().toString().trim();

            if (strEmail.isEmpty() || strUser.isEmpty() || strPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Form harus diisi semua!", Toast.LENGTH_SHORT).show();
            } else {
                // Panggil method registerAction agar sesuai dengan respons register
                registerAction(strEmail, strUser, strPassword);
            }
        });
    }

    private void registerAction(String email, String user, String password) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // Disable tombol dan ubah warnanya
        btnRegister.setEnabled(false);
        btnRegister.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.button_disabled));


        Call<RegisterResponse> call = apiInterface.register(
                user,      // name
                email,     // email
                password,  // password
                password   // password_confirmation
        );

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                // Aktifkan kembali tombol jika terjadi error
                btnRegister.setEnabled(true);
                btnRegister.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.primary));

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    String message = registerResponse.getMessage();
                    String registeredEmail = registerResponse.getEmail();

                    // Tampilkan pesan dari server
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Arahkan ke VerifyActivity dan kirim data email
                    Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);
                    intent.putExtra("email", registeredEmail);
                    startActivity(intent);

                    // Tutup activity register setelah berpindah ke halaman verifikasi
                    finish();
                } else {
                    Log.e("API_ERROR", "Register response tidak sesuai atau kosong");
                    Toast.makeText(RegisterActivity.this, "Register gagal!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

                // Aktifkan kembali tombol jika terjadi error
                btnRegister.setEnabled(true);
                btnRegister.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.primary));

                Log.e("API_ERROR", "Gagal register: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}