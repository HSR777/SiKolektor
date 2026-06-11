package com.example.sikolektor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// Mengelola tampilan daftar kunjungan dengan filter dan logika redirect (Tahap 6)
public class KunjunganFragment extends Fragment {

    private RecyclerView recyclerView;
    private KunjunganAdapter adapter;
    private List<Kunjungan> listKunjungan = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private TextView chipSemua, chipPrioritas, chipTerjadwal, chipSelesai;
    private String currentFilter = "Semua";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_daftar_kunjungan, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("SiKolektorPrefs", Context.MODE_PRIVATE);
        
        // Sembunyikan tombol back
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setVisibility(View.GONE);

        // Inisialisasi Chips
        chipSemua = view.findViewById(R.id.chipSemua);
        chipPrioritas = view.findViewById(R.id.chipPrioritas);
        chipTerjadwal = view.findViewById(R.id.chipTerjadwal);
        chipSelesai = view.findViewById(R.id.chipSelesai);

        setupChips();

        recyclerView = view.findViewById(R.id.rvKunjungan);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new KunjunganAdapter(listKunjungan, kunjungan -> {
            if (!sharedPreferences.getBoolean("is_absen_masuk", false)) {
                Toast.makeText(getContext(), "Anda harus Absen Masuk terlebih dahulu!", Toast.LENGTH_LONG).show();
                return;
            }

            // Logika Redirect (Revisi Dosen Tahap 6)
            // Jika status Selesai (Berhasil/Rumah Kosong), pindah ke History tab dulu
            if (kunjungan.isVisited || "Berhasil".equals(kunjungan.status) || "Rumah Kosong".equals(kunjungan.status)) {
                Toast.makeText(getContext(), "Mengalihkan ke Riwayat...", Toast.LENGTH_SHORT).show();
                
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.riwayatFragment);
                
                // Baru kemudian buka detail
                openDetail(kunjungan);
            } else {
                openDetail(kunjungan);
            }
        });

        recyclerView.setAdapter(adapter);
        checkStatusAndLoadData();

        return view;
    }

    private void openDetail(Kunjungan kunjungan) {
        Intent intent = new Intent(getActivity(), DetailKunjunganActivity.class);
        intent.putExtra("DATA_KUNJUNGAN", kunjungan);
        startActivity(intent);
    }

    private void setupChips() {
        chipSemua.setOnClickListener(v -> updateFilter("Semua"));
        chipPrioritas.setOnClickListener(v -> updateFilter("Prioritas"));
        chipTerjadwal.setOnClickListener(v -> updateFilter("Terjadwal"));
        chipSelesai.setOnClickListener(v -> updateFilter("Selesai"));
    }

    private void updateFilter(String filter) {
        currentFilter = filter;
        
        // Reset style chips
        chipSemua.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipSemua.setTextColor(getResources().getColor(R.color.primary));
        chipPrioritas.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipPrioritas.setTextColor(getResources().getColor(R.color.primary));
        chipTerjadwal.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipTerjadwal.setTextColor(getResources().getColor(R.color.primary));
        chipSelesai.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipSelesai.setTextColor(getResources().getColor(R.color.primary));

        // Set active chip
        TextView selected = null;
        if (filter.equals("Semua")) selected = chipSemua;
        else if (filter.equals("Prioritas")) selected = chipPrioritas;
        else if (filter.equals("Terjadwal")) selected = chipTerjadwal;
        else if (filter.equals("Selesai")) selected = chipSelesai;

        if (selected != null) {
            selected.setBackgroundResource(R.drawable.bg_chip_active);
            selected.setTextColor(getResources().getColor(R.color.white));
        }

        loadDataFromDatabase();
    }

    private void checkStatusAndLoadData() {
        if (!sharedPreferences.getBoolean("is_absen_masuk", false)) {
            listKunjungan.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Daftar kunjungan terkunci. Silakan Absen Masuk.", Toast.LENGTH_SHORT).show();
        } else {
            loadDataFromDatabase();
        }
    }

    private void loadDataFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDao dao = AppDatabase.getInstance(getContext()).appDao();
            List<Kunjungan> data;
            
            switch (currentFilter) {
                case "Prioritas":
                    data = filterByStatus(dao.getAllKunjungan(), "Prioritas", false);
                    break;
                case "Terjadwal":
                    data = filterByStatus(dao.getAllKunjungan(), "Terjadwal", false);
                    break;
                case "Selesai":
                    data = dao.getHistoryKunjungan();
                    break;
                default:
                    data = dao.getAllKunjungan();
                    break;
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    listKunjungan.clear();
                    listKunjungan.addAll(data);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    private List<Kunjungan> filterByStatus(List<Kunjungan> source, String status, boolean isVisited) {
        List<Kunjungan> filtered = new ArrayList<>();
        for (Kunjungan k : source) {
            if (status.equals(k.status) && k.isVisited == isVisited) {
                filtered.add(k);
            }
        }
        return filtered;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkStatusAndLoadData();
    }
}
