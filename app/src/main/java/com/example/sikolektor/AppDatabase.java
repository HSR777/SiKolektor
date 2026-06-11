package com.example.sikolektor;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.Executors;

// Mengatur konfigurasi database Room dan inisialisasi data awal (Tahap 1: Update entitas & data demo)
@Database(entities = {Absen.class, Kunjungan.class, User.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "sikolektor_db")
                    .fallbackToDestructiveMigration() // Menghapus db lama jika ada perubahan skema (cocok untuk demo)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                seedData();
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }

    private static void seedData() {
        // 1. Tambahkan Data User Demo (NIK, Nama, Password)
        User admin = new User("12345", "Budi Kolektor", "password123");
        instance.appDao().insertUser(admin);

        // 2. Tambahkan Data Nasabah dengan Status (Tahap 1)
        Kunjungan d1 = new Kunjungan();
        d1.namaNasabah = "Ahmad Subarjo";
        d1.keterangan = "Alamat Jalan Mawar No 1";
        d1.waktu = "2023-10-25 09:00";
        d1.status = "Prioritas";
        d1.isVisited = false;

        Kunjungan d2 = new Kunjungan();
        d2.namaNasabah = "Siti Aminah";
        d2.keterangan = "Alamat Jalan Melati No 2";
        d2.waktu = "2023-10-25 10:30";
        d2.status = "Terjadwal";
        d2.isVisited = false;

        Kunjungan d3 = new Kunjungan();
        d3.namaNasabah = "Bambang Pamungkas";
        d3.keterangan = "Alamat Jalan Anggrek No 3";
        d3.waktu = "2023-10-25 13:00";
        d3.status = "Prioritas";
        d3.isVisited = false;

        instance.appDao().insertKunjungan(d1);
        instance.appDao().insertKunjungan(d2);
        instance.appDao().insertKunjungan(d3);
    }
}
