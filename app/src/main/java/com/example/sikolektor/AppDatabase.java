package com.example.sikolektor;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.Executors;

// Mengatur konfigurasi database Room dan inisialisasi data awal
@Database(entities = {Absen.class, Kunjungan.class, User.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "sikolektor_db")
                    .fallbackToDestructiveMigration()
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Menggunakan singleton instance yang akan segera tersedia
                            Executors.newSingleThreadExecutor().execute(() -> {
                                // Kita ambil instance lagi di dalam thread untuk memastikan tidak null
                                AppDatabase dbInstance = getInstance(context);
                                seedData(dbInstance);
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }

    private static void seedData(AppDatabase db) {
        // 1. Tambahkan Data User Demo
        User admin = new User("12345", "Budi Kolektor", "password123");
        db.appDao().insertUser(admin);

        // 2. Tambahkan Data Nasabah dengan Koordinat Google Maps Asli (Jakarta)
        Kunjungan d1 = new Kunjungan();
        d1.namaNasabah = "Ahmad Subarjo";
        d1.keterangan = "Alamat Jalan Mawar No 1 (Dekat Monas)";
        d1.waktu = "2023-10-25 09:00";
        d1.status = "Prioritas";
        d1.latitude = -6.175392;
        d1.longitude = 106.827153;
        d1.isVisited = false;

        Kunjungan d2 = new Kunjungan();
        d2.namaNasabah = "Siti Aminah";
        d2.keterangan = "Alamat Jalan Melati No 2 (Dekat Istiqlal)";
        d2.waktu = "2023-10-25 10:30";
        d2.status = "Terjadwal";
        d2.latitude = -6.170223;
        d2.longitude = 106.831525;
        d2.isVisited = false;

        Kunjungan d3 = new Kunjungan();
        d3.namaNasabah = "Bambang Pamungkas";
        d3.keterangan = "Alamat Jalan Anggrek No 3 (Dekat GBK)";
        d3.waktu = "2023-10-25 13:00";
        d3.status = "Prioritas";
        d3.latitude = -6.218335;
        d3.longitude = 106.802216;
        d3.isVisited = false;

        Kunjungan d4 = new Kunjungan();
        d4.namaNasabah = "Haji Lulung";
        d4.keterangan = "Pasar Tanah Abang Blok A";
        d4.waktu = "2023-10-25 14:30";
        d4.status = "Terjadwal";
        d4.latitude = -6.187807;
        d4.longitude = 106.812678;
        d4.isVisited = false;

        Kunjungan d5 = new Kunjungan();
        d5.namaNasabah = "Syahrini";
        d5.keterangan = "Apartemen Dekat Bundaran HI";
        d5.waktu = "2023-10-25 16:00";
        d5.status = "Prioritas";
        d5.latitude = -6.192131;
        d5.longitude = 106.822971;
        d5.isVisited = false;

        db.appDao().insertKunjungan(d1);
        db.appDao().insertKunjungan(d2);
        db.appDao().insertKunjungan(d3);
        db.appDao().insertKunjungan(d4);
        db.appDao().insertKunjungan(d5);
    }
}
