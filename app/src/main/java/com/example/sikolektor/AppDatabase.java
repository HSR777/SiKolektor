package com.example.sikolektor;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.Executors;

// Mengatur konfigurasi database Room dan inisialisasi data awal
@Database(entities = {Absen.class, Kunjungan.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();
    private static AppDatabase instance;

    // Mendapatkan instance tunggal dari database
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "sikolektor_db")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Mengisi data awal secara otomatis saat database pertama kali dibuat
                            Executors.newSingleThreadExecutor().execute(() -> {
                                seedData();
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }

    // Menambahkan data nasabah dummy ke dalam database
    private static void seedData() {
        Kunjungan d1 = new Kunjungan();
        d1.namaNasabah = "Nasabah Dummy 1";
        d1.keterangan = "Alamat Jalan Mawar No 1";
        d1.waktu = "2023-10-01 09:00";

        Kunjungan d2 = new Kunjungan();
        d2.namaNasabah = "Nasabah Dummy 2";
        d2.keterangan = "Alamat Jalan Melati No 2";
        d2.waktu = "2023-10-01 10:30";

        Kunjungan d3 = new Kunjungan();
        d3.namaNasabah = "Nasabah Dummy 3";
        d3.keterangan = "Alamat Jalan Anggrek No 3";
        d3.waktu = "2023-10-01 13:00";

        instance.appDao().insertKunjungan(d1);
        instance.appDao().insertKunjungan(d2);
        instance.appDao().insertKunjungan(d3);
    }
}
