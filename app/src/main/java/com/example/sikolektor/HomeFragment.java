package com.example.sikolektor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

// Mengelola dashboard utama (Home) sesuai revisi Tahap 3, 4, & 5
public class HomeFragment extends Fragment {

    private TextView tvCollectorName, tvCollectorNik, tvCurrentTime;
    private TextView tvTotal, tvSelesai, tvBelum, tvDurasi, tvStatusAktif;
    private Button btnAbsenMasuk, btnAbsenPulang;
    private ImageButton btnLogout;
    private SharedPreferences sharedPreferences;
    
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startActivity(new Intent(requireContext(), AbsenMasukActivity.class));
                    } else {
                        Toast.makeText(getContext(), "Izin kamera diperlukan untuk absensi", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_dashboard, container, false);

        // Inisialisasi Komponen UI
        tvCollectorName = view.findViewById(R.id.tvAppName);
        tvCollectorNik = view.findViewById(R.id.tvCollectorName);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        btnLogout = view.findViewById(R.id.btnLogout);
        
        tvTotal = view.findViewById(R.id.tvTotalKunjungan);
        tvSelesai = view.findViewById(R.id.tvSelesai);
        tvBelum = view.findViewById(R.id.tvBelum);
        tvDurasi = view.findViewById(R.id.tvDurasiKerja);
        tvStatusAktif = view.findViewById(R.id.tvStatusAktif);
        
        btnAbsenMasuk = view.findViewById(R.id.btnAbsenMasuk);
        btnAbsenPulang = view.findViewById(R.id.btnAbsenPulang);

        sharedPreferences = requireActivity().getSharedPreferences("SiKolektorPrefs", Context.MODE_PRIVATE);
        
        tvCollectorName.setText(sharedPreferences.getString("nama", "User"));
        tvCollectorNik.setText("(NIK " + sharedPreferences.getString("nik", "-") + ")");
        tvCurrentTime.setText(new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(new Date()));

        // Tombol Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                sharedPreferences.edit().clear().apply();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            });
        }

        // Tombol Absen Masuk
        btnAbsenMasuk.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(requireContext(), AbsenMasukActivity.class));
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // Tombol Absen Pulang
        btnAbsenPulang.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AbsenPulangActivity.class));
        });

        updateUIBasedOnStatus();

        return view;
    }

    private void updateUIBasedOnStatus() {
        boolean isAbsenMasuk = sharedPreferences.getBoolean("is_absen_masuk", false);
        boolean isAbsenPulangDone = sharedPreferences.getBoolean("is_absen_pulang_done", false);

        if (isAbsenPulangDone) {
            tvStatusAktif.setText("Selesai");
            tvStatusAktif.setBackgroundResource(R.drawable.bg_status_done);
            btnAbsenMasuk.setEnabled(false);
            btnAbsenMasuk.setAlpha(0.5f);
            btnAbsenPulang.setEnabled(false);
            btnAbsenPulang.setAlpha(0.5f);
        } else if (isAbsenMasuk) {
            tvStatusAktif.setText("Aktif");
            tvStatusAktif.setBackgroundResource(R.drawable.bg_status_active);
            btnAbsenMasuk.setEnabled(false);
            btnAbsenMasuk.setAlpha(0.5f);
            
            // Absen pulang hanya bisa jika sudah ada minimal 1 kunjungan
            // Kita akan cek jumlah kunjungan selesai di loadDashboardStats()
            btnAbsenPulang.setEnabled(true);
            btnAbsenPulang.setAlpha(1.0f);
        } else {
            tvStatusAktif.setText("Tidak Aktif");
            tvStatusAktif.setBackgroundResource(R.drawable.bg_status_gps); // Menggunakan warna abu-abu
            btnAbsenMasuk.setEnabled(true);
            btnAbsenMasuk.setAlpha(1.0f);
            btnAbsenPulang.setEnabled(false);
            btnAbsenPulang.setAlpha(0.5f);
        }
        
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDao dao = AppDatabase.getInstance(getContext()).appDao();
            int total = dao.getTotalNasabahCount();
            int selesai = dao.getSudahDikunjungiCount();
            int belum = dao.getBelumDikunjungiCount();
            
            boolean isAbsenMasuk = sharedPreferences.getBoolean("is_absen_masuk", false);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvTotal.setText(String.valueOf(total));
                    tvSelesai.setText(String.valueOf(selesai));
                    tvBelum.setText(String.valueOf(belum));
                    
                    // Revisi: Tombol absen pulang aktif jika minimal 1 kunjungan dilakukan
                    if (isAbsenMasuk) {
                        if (selesai > 0) {
                            btnAbsenPulang.setEnabled(true);
                            btnAbsenPulang.setAlpha(1.0f);
                        } else {
                            btnAbsenPulang.setEnabled(false);
                            btnAbsenPulang.setAlpha(0.5f);
                            // Opsional: beri info kenapa belum bisa pulang
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUIBasedOnStatus();
    }
}
