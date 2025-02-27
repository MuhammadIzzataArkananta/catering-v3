package com.arka.cateringukom.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.arka.cateringukom.R;
import com.arka.cateringukom.api.model.User;
import com.arka.cateringukom.api.response.LoginResponse;
import com.arka.cateringukom.main.MainActivity;
import com.arka.cateringukom.networking.ApiClient;
import com.arka.cateringukom.networking.ApiInterface;
import com.arka.cateringukom.register.RegisterActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    MaterialButton btnLogin, btnRegister;
    TextInputEditText inputEmail, inputPassword;
    String strEmail, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek apakah user sudah login dengan memeriksa token di SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token != null) {
            // Token sudah ada, langsung pindah ke MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        setInitLayout();
        setInputData();
    }

    private void setInitLayout() {
        inputEmail = findViewById(R.id.inputEmailLogin);
        inputPassword = findViewById(R.id.inputPasswordLogin);
        btnLogin = findViewById(R.id.btnLoginLogin);
        btnRegister = findViewById(R.id.btnRegisterLogin);
    }

    private void setInputData() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = inputEmail.getText().toString().trim();
                strPassword = inputPassword.getText().toString().trim();

                if (strEmail.isEmpty() || strPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Ups, Form harus diisi semua!", Toast.LENGTH_LONG).show();
                } else {
                    doLogin(strEmail, strPassword);
                }
            }
        });
    }

    private void doLogin(String email, String password) {
        // Disable tombol login dan ubah warnanya agar tidak terjadi spam
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.button_disabled));

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Aktifkan kembali tombol login dan kembalikan warnanya
                btnLogin.setEnabled(true);
                btnLogin.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.primary));

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String message = loginResponse.getMessage();
                    String token = loginResponse.getAccessToken();

                    // Simpan token dan data user ke SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", token);

                    User user = loginResponse.getUser();


                    editor.putString("token", token);                // token login
                    editor.putString("user_id", String.valueOf(user.getId()));
                    editor.putString("user_name", user.getName());
                    editor.putString("user_email", user.getEmail());

                    // Simpan data user sebagai JSON (gunakan Gson)
                    Gson gson = new Gson();
                    String userJson = gson.toJson(loginResponse.getUser());
                    editor.putString("user_data", userJson);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Pindah ke MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Ups, Username atau Password Anda salah!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.primary));
                Toast.makeText(LoginActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}