package com.example.sikolektor;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Mendefinisikan tabel untuk menyimpan data absensi kolektor (Tahap 1: Mendukung Absen Masuk & Pulang)
@Entity(tableName = "tabel_absen")
public class Absen implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nik;
    public String waktuMasuk;
    public String waktuPulang;
    public String fotoMasukPath;
    public String fotoPulangPath;
    public String catatanMasuk;
    public String catatanPulang;
    public String durasiKerja;
    public double latitude;
    public double longitude;

    public Absen() {}

    protected Absen(Parcel in) {
        id = in.readInt();
        nik = in.readString();
        waktuMasuk = in.readString();
        waktuPulang = in.readString();
        fotoMasukPath = in.readString();
        fotoPulangPath = in.readString();
        catatanMasuk = in.readString();
        catatanPulang = in.readString();
        durasiKerja = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nik);
        dest.writeString(waktuMasuk);
        dest.writeString(waktuPulang);
        dest.writeString(fotoMasukPath);
        dest.writeString(fotoPulangPath);
        dest.writeString(catatanMasuk);
        dest.writeString(catatanPulang);
        dest.writeString(durasiKerja);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
