package com.example.sikolektor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

// Menangani proses masuk dan penyimpanan sesi pengguna menggunakan SharedPreferences
public class LoginActivity extends AppCompatActivity {
    private EditText etNik, etNama;
    private Button btnMasuk;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inisialisasi SharedPreferences untuk fitur Auto-Login
        sharedPreferences = getSharedPreferences("SiKolektorPrefs", MODE_PRIVATE);
        
        // Memeriksa apakah data login sudah tersimpan (jika ya, langsung ke halaman utama)
        if (sharedPreferences.contains("nik")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        
        etNik = findViewById(R.id.etNik);
        etNama = findViewById(R.id.etPassword); // Di layout ini etPassword digunakan sementara sebagai input identitas
        btnMasuk = findViewById(R.id.btnMasuk);

        btnMasuk.setOnClickListener(v -> {
            String nik = etNik.getText().toString();
            String nama = etNama.getText().toString();
            
            if (!nik.isEmpty() && !nama.isEmpty()) {
                // Menyimpan identitas kolektor ke SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nik", nik);
                editor.putString("nama", nama);
                editor.apply();

                // Berpindah ke MainActivity setelah login sukses
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
