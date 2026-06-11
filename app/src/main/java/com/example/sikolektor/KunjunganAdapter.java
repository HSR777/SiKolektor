package com.example.sikolektor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Menghubungkan data kunjungan dari database ke tampilan daftar (RecyclerView)
public class KunjunganAdapter extends RecyclerView.Adapter<KunjunganAdapter.ViewHolder> {

    private List<Kunjungan> kunjunganList;
    private OnItemClickListener listener;

    // Interface untuk menangani klik pada setiap baris data
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
        // Menggunakan layout item sederhana untuk setiap baris data
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Kunjungan item = kunjunganList.get(position);
        holder.tvNama.setText(item.namaNasabah);
        holder.tvKeterangan.setText(item.waktu + " - " + item.keterangan);
        
        // Mengatur aksi klik untuk berpindah ke halaman detail
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return kunjunganList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKeterangan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(android.R.id.text1);
            tvKeterangan = itemView.findViewById(android.R.id.text2);
        }
    }
}
