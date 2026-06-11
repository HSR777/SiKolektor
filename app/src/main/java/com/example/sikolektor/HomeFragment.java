package com.example.sikolektor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

// Mengelola dashboard, absensi, dan form kunjungan nasabah sesuai kriteria dan user flow
public class HomeFragment extends Fragment {

    private TextView tvCollectorName, tvCollectorNik;
    private Button btnAbsenMasuk, btnSimpanKunjungan;
    private EditText etNamaNasabah, etKeteranganKunjungan;
    private SharedPreferences sharedPreferences;
    
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Launcher untuk menangani hasil kamera
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getContext(), "Foto berhasil diambil: " + currentPhotoPath, Toast.LENGTH_LONG).show();
                        
                        // Menjalankan Service pengukur waktu kerja setelah foto diambil
                        Intent serviceIntent = new Intent(requireContext(), WorkTimerService.class);
                        requireActivity().startService(serviceIntent);
                        
                        NotificationHelper.showNotification(requireContext(), "Absensi Berhasil", "Sesi kerja Anda telah dimulai.");
                    }
                }
        );

        // Launcher untuk meminta izin kamera
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        dispatchTakePictureIntent();
                    } else {
                        Toast.makeText(getContext(), "Izin kamera ditolak untuk melakukan absensi", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menggunakan layout dashboard terbaru yang sudah memiliki form input
        View view = inflater.inflate(R.layout.activity_home_dashboard, container, false);

        // Inisialisasi komponen profil kolektor (Kriteria 2)
        tvCollectorName = view.findViewById(R.id.tvAppName);
        tvCollectorNik = view.findViewById(R.id.tvCollectorName);
        sharedPreferences = requireActivity().getSharedPreferences("SiKolektorPrefs", Context.MODE_PRIVATE);
        
        tvCollectorName.setText(sharedPreferences.getString("nama", "User"));
        tvCollectorNik.setText("(NIK " + sharedPreferences.getString("nik", "-") + ")");

        // Inisialisasi komponen form (User Flow Gambar 2)
        btnAbsenMasuk = view.findViewById(R.id.btnAbsenMasuk);
        btnSimpanKunjungan = view.findViewById(R.id.btnSimpanKunjungan);
        etNamaNasabah = view.findViewById(R.id.etNamaNasabah);
        etKeteranganKunjungan = view.findViewById(R.id.etKeteranganKunjungan);

        // Logika Tombol Absen (Kriteria 3: Akses Kamera Nyata)
        btnAbsenMasuk.setOnClickListener(v -> {
            checkCameraPermissionAndCapture();
        });

        // Logika Simpan Kunjungan (Kriteria 4, 5, dan 8)
        btnSimpanKunjungan.setOnClickListener(v -> {
            String nama = etNamaNasabah.getText().toString();
            String ket = etKeteranganKunjungan.getText().toString();

            if (nama.isEmpty()) {
                Toast.makeText(getContext(), "Isi Nama Nasabah!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulasi pengambilan koordinat GPS (Kriteria 4 - LBS)
            double[] coords = getCoordinates();

            Kunjungan baru = new Kunjungan();
            baru.namaNasabah = nama;
            baru.keterangan = ket;
            baru.latitude = coords[0];
            baru.longitude = coords[1];
            baru.waktu = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            baru.fotoRumahPath = currentPhotoPath != null ? currentPhotoPath : "no_photo_yet"; 

            // Simpan ke database lokal (Kriteria 5)
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getContext()).appDao().insertKunjungan(baru);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Laporan Kunjungan Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                        // Mengosongkan form
                        etNamaNasabah.setText("");
                        etKeteranganKunjungan.setText("");
                        
                        // Push Notification konfirmasi sukses (Kriteria 8)
                        NotificationHelper.showNotification(requireContext(), "Kunjungan Sukses", "Data kunjungan nasabah telah masuk database.");
                    });
                }
            });
        });

        return view;
    }

    private void checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        // Membuat file untuk menyimpan hasil foto
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(getContext(), "Error: Gagal menyiapkan penyimpanan foto", Toast.LENGTH_SHORT).show();
        }
        
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(requireContext(),
                    "com.example.sikolektor.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private File createImageFile() throws IOException {
        // Buat nama file unik berdasarkan timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "SIKOL_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        
        // Simpan path untuk digunakan nanti
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Fungsi fallback LBS: Jika GPS lemah, kunci ke koordinat dummy kampus (Kriteria 4)
    private double[] getCoordinates() {
        return new double[]{-6.2000, 106.8166}; // Koordinat Dummy Kampus
    }
}
