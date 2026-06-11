package com.example.sikolektor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface AppDao {
    // User operations
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM tabel_user WHERE nik = :nik AND password = :password LIMIT 1")
    User login(String nik, String password);

    @Query("SELECT * FROM tabel_user WHERE nik = :nik LIMIT 1")
    User getUserByNik(String nik);

    // Absen operations
    @Insert
    void insertAbsen(Absen absen);

    @Update
    void updateAbsen(Absen absen);

    @Query("SELECT * FROM tabel_absen WHERE nik = :nik ORDER BY id DESC LIMIT 1")
    Absen getLatestAbsen(String nik);

    // Kunjungan operations
    @Insert
    void insertKunjungan(Kunjungan kunjungan);

    @Update
    void updateKunjungan(Kunjungan kunjungan);

    @Query("SELECT * FROM tabel_kunjungan ORDER BY id DESC")
    List<Kunjungan> getAllKunjungan();

    @Query("SELECT * FROM tabel_kunjungan WHERE status IN ('Prioritas', 'Terjadwal') AND isVisited = 0 ORDER BY id DESC")
    List<Kunjungan> getDaftarKunjungan();

    @Query("SELECT * FROM tabel_kunjungan WHERE status IN ('Berhasil', 'Rumah Kosong') OR isVisited = 1 ORDER BY id DESC")
    List<Kunjungan> getHistoryKunjungan();

    @Query("SELECT COUNT(*) FROM tabel_kunjungan")
    int getTotalNasabahCount();

    @Query("SELECT COUNT(*) FROM tabel_kunjungan WHERE isVisited = 1")
    int getSudahDikunjungiCount();

    @Query("SELECT COUNT(*) FROM tabel_kunjungan WHERE isVisited = 0")
    int getBelumDikunjungiCount();
}
