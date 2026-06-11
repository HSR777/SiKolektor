package com.example.sikolektor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AbsenMasukActivity extends AppCompatActivity {

    private TextView tvWaktu;
    private ImageView ivSelfiePreview;
    private Button btnAmbilFoto, btnSimpanAbsen;
    private EditText etCatatan;
    private String currentPhotoPath;
    private SharedPreferences sharedPreferences;

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
        setContentView(R.layout.activity_absen_masuk);

        tvWaktu = findViewById(R.id.tvWaktuMasuk);
        ivSelfiePreview = findViewById(R.id.ivSelfiePreview);
        btnAmbilFoto = findViewById(R.id.btnAmbilFoto);
        btnSimpanAbsen = findViewById(R.id.btnSimpanAbsen);
        etCatatan = findViewById(R.id.etCatatanAbsen);

        sharedPreferences = getSharedPreferences("SiKolektorPrefs", Context.MODE_PRIVATE);

        // Menampilkan waktu sekarang
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvWaktu.setText(currentTime);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnAmbilFoto.setOnClickListener(v -> dispatchTakePictureIntent());

        btnSimpanAbsen.setOnClickListener(v -> {
            if (currentPhotoPath == null) {
                Toast.makeText(this, "Silakan ambil foto selfie terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            saveAbsenMasuk();
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
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.sikolektor.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "ABSEN_IN_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveAbsenMasuk() {
        String nik = sharedPreferences.getString("nik", "");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String catatan = etCatatan.getText().toString();

        Absen absen = new Absen();
        absen.nik = nik;
        absen.waktuMasuk = time;
        absen.fotoMasukPath = currentPhotoPath;
        absen.catatanMasuk = catatan;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).appDao().insertAbsen(absen);
            
            // Simpan status absen di SharedPreferences agar HomeFragment tahu user sudah absen masuk
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_absen_masuk", true);
            editor.putString("waktu_absen_masuk", time);
            editor.apply();

            runOnUiThread(() -> {
                Toast.makeText(this, "Absensi masuk berhasil disimpan", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
