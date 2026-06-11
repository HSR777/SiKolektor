package com.example.sikolektor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// Mengelola tampilan riwayat kunjungan (Tahap 3: Hanya Berhasil & Rumah Kosong)
public class RiwayatFragment extends Fragment {

    private RecyclerView recyclerView;
    private KunjunganAdapter adapter;
    private List<Kunjungan> listKunjungan = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menggunakan layout riwayat yang sudah ada
        View view = inflater.inflate(R.layout.activity_riwayat_kunjungan, container, false);

        recyclerView = view.findViewById(R.id.rvRiwayat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new KunjunganAdapter(listKunjungan, kunjungan -> {
            Intent intent = new Intent(getActivity(), DetailKunjunganActivity.class);
            intent.putExtra("DATA_KUNJUNGAN", kunjungan); 
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        loadDataFromDatabase();

        return view;
    }

    private void loadDataFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Filter: Hanya menampilkan Berhasil atau Rumah Kosong (Sesuai Revisi Tahap 3)
            List<Kunjungan> data = AppDatabase.getInstance(getContext()).appDao().getHistoryKunjungan();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    listKunjungan.clear();
                    listKunjungan.addAll(data);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromDatabase();
    }
}
