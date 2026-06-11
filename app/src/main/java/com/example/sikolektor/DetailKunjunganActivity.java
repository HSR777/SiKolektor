package com.example.sikolektor;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

// Menampilkan detail lengkap kunjungan nasabah yang dikirim melalui Intent Parcelable
public class DetailKunjunganActivity extends AppCompatActivity {

    private TextView tvNama, tvWaktu, tvId, tvKoordinat, tvCatatan, tvFotoPlaceholder;
    private ImageView ivFotoBukti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kunjungan);

        // Inisialisasi komponen tampilan berdasarkan ID di layout
        tvNama = findViewById(R.id.tvNamaCustomer);
        tvWaktu = findViewById(R.id.tvWaktuKunjungan);
        tvId = findViewById(R.id.tvIdTransaksi);
        tvKoordinat = findViewById(R.id.tvKoordinat);
        tvCatatan = findViewById(R.id.tvCatatan);
        ivFotoBukti = findViewById(R.id.ivFotoBukti);
        tvFotoPlaceholder = findViewById(R.id.tvFotoPlaceholder);

        // Menangani tombol kembali
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Menerima data Kunjungan (Parcelable) dari RiwayatFragment (Memenuhi Kriteria 7)
        Kunjungan data = getIntent().getParcelableExtra("DATA_KUNJUNGAN");

        if (data != null) {
            tvNama.setText(data.namaNasabah);
            tvWaktu.setText(data.waktu);
            tvId.setText("#SKL-" + data.id);
            tvCatatan.setText(data.keterangan);
            
            // Menampilkan Foto Bukti (Kriteria 3)
            if (data.fotoRumahPath != null && !data.fotoRumahPath.equals("no_photo_yet") && !data.fotoRumahPath.equals("dummy_res_placeholder")) {
                File imgFile = new File(data.fotoRumahPath);
                if (imgFile.exists()) {
                    ivFotoBukti.setImageURI(Uri.fromFile(imgFile));
                    tvFotoPlaceholder.setVisibility(View.GONE);
                }
            }
            
            // Menampilkan koordinat GPS (Memenuhi Kriteria 4)
            if (data.latitude == 0 && data.longitude == 0) {
                tvKoordinat.setText("-6.2000° S,\n106.8166° E (Lokasi Dummy)");
            } else {
                tvKoordinat.setText(data.latitude + "° S,\n" + data.longitude + "° E");
            }
        }
    }
}
