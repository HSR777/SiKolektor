package com.example.sikolektor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

// Mengelola detail kunjungan nasabah, pengambilan bukti foto, dan simpan status (Tahap 6)
public class DetailKunjunganActivity extends AppCompatActivity {

    private TextView tvNama, tvWaktu, tvId, tvKoordinat;
    private EditText etCatatan;
    private CheckBox cbRumahKosong;
    private ImageView ivFotoBukti;
    private Button btnAmbilFoto, btnSimpan;
    
    private Kunjungan currentKunjungan;
    private String currentPhotoPath;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    ivFotoBukti.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
                    btnAmbilFoto.setText("Ubah Foto");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kunjungan);

        // Inisialisasi UI
        tvNama = findViewById(R.id.tvNamaCustomer);
        tvWaktu = findViewById(R.id.tvWaktuKunjungan);
        tvId = findViewById(R.id.tvIdTransaksi);
        tvKoordinat = findViewById(R.id.tvKoordinat);
        
        ivFotoBukti = findViewById(R.id.ivFotoBukti);
        btnAmbilFoto = findViewById(R.id.btnAmbilFotoKunjungan);
        etCatatan = findViewById(R.id.etCatatanKunjungan);
        cbRumahKosong = findViewById(R.id.cbRumahKosong);
        btnSimpan = findViewById(R.id.btnSimpanKunjungan);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Menerima data Kunjungan
        currentKunjungan = getIntent().getParcelableExtra("DATA_KUNJUNGAN");

        if (currentKunjungan != null) {
            displayData();
        }

        btnAmbilFoto.setOnClickListener(v -> dispatchTakePictureIntent());
        btnSimpan.setOnClickListener(v -> saveKunjungan());
    }

    private void displayData() {
        tvNama.setText(currentKunjungan.namaNasabah);
        tvWaktu.setText(currentKunjungan.waktu);
        tvId.setText("#SKL-" + currentKunjungan.id);
        tvKoordinat.setText(currentKunjungan.latitude + ", " + currentKunjungan.longitude);

        // Jika sudah dikunjungi (mode riwayat)
        if (currentKunjungan.isVisited) {
            etCatatan.setText(currentKunjungan.keterangan);
            etCatatan.setEnabled(false);
            cbRumahKosong.setChecked("Rumah Kosong".equals(currentKunjungan.status));
            cbRumahKosong.setEnabled(false);
            btnAmbilFoto.setVisibility(View.GONE);
            btnSimpan.setVisibility(View.GONE);
            
            if (currentKunjungan.fotoRumahPath != null) {
                File imgFile = new File(currentKunjungan.fotoRumahPath);
                if (imgFile.exists()) {
                    ivFotoBukti.setImageURI(Uri.fromFile(imgFile));
                }
            }
        }
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
        String imageFileName = "VISIT_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveKunjungan() {
        if (currentPhotoPath == null && !cbRumahKosong.isChecked()) {
            Toast.makeText(this, "Ambil bukti foto atau centang Rumah Kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        String catatan = etCatatan.getText().toString();
        if (catatan.isEmpty()) {
            Toast.makeText(this, "Catatan kunjungan wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        currentKunjungan.keterangan = catatan;
        currentKunjungan.fotoRumahPath = currentPhotoPath;
        currentKunjungan.isVisited = true;
        currentKunjungan.waktu = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        
        if (cbRumahKosong.isChecked()) {
            currentKunjungan.status = "Rumah Kosong";
        } else {
            currentKunjungan.status = "Berhasil";
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).appDao().updateKunjungan(currentKunjungan);
            runOnUiThread(() -> {
                Toast.makeText(this, "Laporan kunjungan berhasil disimpan", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
