package com.example.sikolektor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AbsenPulangActivity extends AppCompatActivity {

    private TextView tvWaktuMasuk, tvWaktuPulang, tvDurasi, tvTotal, tvBerhasil, tvBelum;
    private ImageView ivSelfiePreview;
    private Button btnAmbilSelfie, btnSimpan;
    private EditText etCatatan;
    private String currentPhotoPath;
    private SharedPreferences sharedPreferences;
    private Absen currentAbsen;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    ivSelfiePreview.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_pulang);

        tvWaktuMasuk = findViewById(R.id.tvWaktuMasukDetail);
        tvWaktuPulang = findViewById(R.id.tvWaktuPulangDetail);
        tvDurasi = findViewById(R.id.tvTotalDurasiDetail);
        tvTotal = findViewById(R.id.tvTotalKunjunganPulang);
        tvBerhasil = findViewById(R.id.tvBerhasilPulang);
        tvBelum = findViewById(R.id.tvBelumPulang);
        ivSelfiePreview = findViewById(R.id.ivSelfiePulangPreview);
        btnAmbilSelfie = findViewById(R.id.btnAmbilSelfiePulang);
        btnSimpan = findViewById(R.id.btnSimpanAbsenPulang);
        etCatatan = findViewById(R.id.etCatatanHarian);

        sharedPreferences = getSharedPreferences("SiKolektorPrefs", Context.MODE_PRIVATE);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnAmbilSelfie.setOnClickListener(v -> dispatchTakePictureIntent());
        btnSimpan.setOnClickListener(v -> saveAbsenPulang());

        loadInitialData();
    }

    private void loadInitialData() {
        String nik = sharedPreferences.getString("nik", "");
        String timeNow = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        tvWaktuPulang.setText(timeNow);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDao dao = AppDatabase.getInstance(this).appDao();
            currentAbsen = dao.getLatestAbsen(nik);
            int total = dao.getTotalNasabahCount();
            int berhasil = dao.getSudahDikunjungiCount();
            int belum = dao.getBelumDikunjungiCount();

            runOnUiThread(() -> {
                tvTotal.setText(String.valueOf(total));
                tvBerhasil.setText(String.valueOf(berhasil));
                tvBelum.setText(String.valueOf(belum));

                if (currentAbsen != null && currentAbsen.waktuMasuk != null) {
                    try {
                        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date dateMasuk = fullFormat.parse(currentAbsen.waktuMasuk);
                        tvWaktuMasuk.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateMasuk));
                        
                        long diff = new Date().getTime() - dateMasuk.getTime();
                        long hours = diff / (60 * 60 * 1000);
                        long minutes = (diff / (60 * 1000)) % 60;
                        tvDurasi.setText(String.format(Locale.getDefault(), "◷ Total Durasi: %02d Jam %02d Menit", hours, minutes));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(this, "Gagal membuat file gambar", Toast.LENGTH_SHORT).show();
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.sikolektor.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("ABSEN_OUT_" + timeStamp + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveAbsenPulang() {
        if (currentPhotoPath == null) {
            Toast.makeText(this, "Silakan ambil foto selfie verifikasi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentAbsen == null) {
            Toast.makeText(this, "Data absen masuk tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        currentAbsen.waktuPulang = time;
        currentAbsen.fotoPulangPath = currentPhotoPath;
        currentAbsen.catatanPulang = etCatatan.getText().toString();
        currentAbsen.durasiKerja = tvDurasi.getText().toString();

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).appDao().updateAbsen(currentAbsen);
            
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_absen_masuk", false); // Reset status harian
            editor.putBoolean("is_absen_pulang_done", true); // Flag hari ini selesai
            editor.apply();

            runOnUiThread(() -> {
                Toast.makeText(this, "Absensi pulang berhasil disimpan. Selesai bekerja!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            });
        });
    }
}
