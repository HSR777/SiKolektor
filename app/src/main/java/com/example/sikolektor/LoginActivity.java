package com.example.sikolektor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.Executors;

// Menangani proses masuk dengan verifikasi database dan fitur "Ingat Saya" (Tahap 2)
public class LoginActivity extends AppCompatActivity {
    private EditText etNik, etPassword;
    private Button btnMasuk;
    private CheckBox cbRemember;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sharedPreferences = getSharedPreferences("SiKolektorPrefs", MODE_PRIVATE);
        
        // Fitur Auto-Login: Periksa apakah "Ingat Saya" aktif dan sesi tersedia
        if (sharedPreferences.getBoolean("is_remembered", false) && sharedPreferences.contains("nik")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        
        etNik = findViewById(R.id.etNik);
        etPassword = findViewById(R.id.etPassword);
        btnMasuk = findViewById(R.id.btnMasuk);
        cbRemember = findViewById(R.id.cbRemember);

        btnMasuk.setOnClickListener(v -> {
            String nik = etNik.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            
            // Validasi input tidak boleh kosong (Sesuai Revisi)
            if (nik.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "NIK dan Kata Sandi wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifikasi login ke database (Tahap 2)
            Executors.newSingleThreadExecutor().execute(() -> {
                User user = AppDatabase.getInstance(this).appDao().login(nik, pass);
                
                runOnUiThread(() -> {
                    if (user != null) {
                        // Simpan sesi ke SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nik", user.nik);
                        editor.putString("nama", user.nama);
                        // Simpan status "Ingat Saya" (Sesuai Revisi)
                        editor.putBoolean("is_remembered", cbRemember.isChecked());
                        editor.apply();

                        Toast.makeText(this, "Login Berhasil. Halo, " + user.nama, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "NIK atau Kata Sandi salah!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }
}
