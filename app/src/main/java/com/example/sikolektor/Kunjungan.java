package com.example.sikolektor;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Mendefinisikan tabel untuk menyimpan data laporan kunjungan nasabah
@Entity(tableName = "tabel_kunjungan")
public class Kunjungan implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String namaNasabah;
    public String keterangan;
    public String fotoRumahPath;
    public double latitude;
    public double longitude;
    public String waktu;

    // Konstruktor kosong yang dibutuhkan oleh Room
    public Kunjungan() {}

    // Membaca data dari objek Parcel untuk pengiriman data antar halaman
    protected Kunjungan(Parcel in) {
        id = in.readInt();
        namaNasabah = in.readString();
        keterangan = in.readString();
        fotoRumahPath = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        waktu = in.readString();
    }

    // Pembuat objek Kunjungan dari data Parcel
    public static final Creator<Kunjungan> CREATOR = new Creator<Kunjungan>() {
        @Override
        public Kunjungan createFromParcel(Parcel in) {
            return new Kunjungan(in);
        }

        @Override
        public Kunjungan[] newArray(int size) {
            return new Kunjungan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // Menulis data ke dalam Parcel agar objek bisa dikirim lewat Intent
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(namaNasabah);
        dest.writeString(keterangan);
        dest.writeString(fotoRumahPath);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(waktu);
    }
}
