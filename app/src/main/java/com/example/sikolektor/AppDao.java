package com.example.sikolektor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

// Antarmuka untuk menentukan operasi database yang bisa dilakukan
@Dao
public interface AppDao {
    // Menyimpan data absensi ke dalam database
    @Insert
    void insertAbsen(Absen absen);

    // Menyimpan data kunjungan nasabah ke dalam database
    @Insert
    void insertKunjungan(Kunjungan kunjungan);

    // Mengambil semua data kunjungan dari yang terbaru
    @Query("SELECT * FROM tabel_kunjungan ORDER BY id DESC")
    List<Kunjungan> getAllKunjungan();
}
