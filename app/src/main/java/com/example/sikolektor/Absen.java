package com.example.sikolektor;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Mendefinisikan tabel untuk menyimpan data absensi kolektor
@Entity(tableName = "tabel_absen")
public class Absen implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nama;
    public String waktu;
    public String fotoPath;
    public double latitude;
    public double longitude;

    // Konstruktor kosong yang dibutuhkan oleh Room
    public Absen() {}

    // Membaca data dari Parcel untuk implementasi Parcelable
    protected Absen(Parcel in) {
        id = in.readInt();
        nama = in.readString();
        waktu = in.readString();
        fotoPath = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    // Objek pembuat untuk implementasi Parcelable
    public static final Creator<Absen> CREATOR = new Creator<Absen>() {
        @Override
        public Absen createFromParcel(Parcel in) {
            return new Absen(in);
        }

        @Override
        public Absen[] newArray(int size) {
            return new Absen[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // Menulis data ke Parcel agar bisa dikirim antar activity
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nama);
        dest.writeString(waktu);
        dest.writeString(fotoPath);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
