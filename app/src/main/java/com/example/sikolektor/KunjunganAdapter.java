package com.example.sikolektor;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Menghubungkan data kunjungan ke tampilan daftar dengan style kartu (Tahap 3)
public class KunjunganAdapter extends RecyclerView.Adapter<KunjunganAdapter.ViewHolder> {

    private List<Kunjungan> kunjunganList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Kunjungan kunjungan);
    }

    public KunjunganAdapter(List<Kunjungan> kunjunganList, OnItemClickListener listener) {
        this.kunjunganList = kunjunganList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout item kustom yang menyerupai style dashboard
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kunjungan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Kunjungan item = kunjunganList.get(position);
        holder.tvNama.setText(item.namaNasabah);
        holder.tvAlamat.setText("⌖ " + item.keterangan);
        holder.tvWaktu.setText("◷ " + (item.waktu != null && item.waktu.length() > 11 ? item.waktu.substring(11) : item.waktu));
        
        // Atur Status dan Warna Badge secara dinamis
        if (item.status != null) {
            holder.tvStatus.setText(item.status);
            updateStatusStyle(holder.tvStatus, item.status);
        } else {
            holder.tvStatus.setText("Prioritas");
            updateStatusStyle(holder.tvStatus, "Prioritas");
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    private void updateStatusStyle(TextView tv, String status) {
        switch (status) {
            case "Prioritas":
                tv.setBackgroundResource(R.drawable.bg_label_priority);
                tv.setTextColor(Color.parseColor("#B71C1C"));
                break;
            case "Terjadwal":
                tv.setBackgroundResource(R.drawable.bg_label_schedule);
                tv.setTextColor(Color.parseColor("#007BFF")); // Primary blue
                break;
            case "Berhasil":
                tv.setBackgroundResource(R.drawable.bg_label_done);
                tv.setTextColor(Color.parseColor("#2E7D32")); // Secondary green
                break;
            case "Rumah Kosong":
                tv.setBackgroundResource(R.drawable.bg_badge_warning);
                tv.setTextColor(Color.parseColor("#6B2D00"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return kunjunganList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvAlamat, tvWaktu, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaNasabah);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
