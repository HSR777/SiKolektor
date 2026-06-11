package com.example.sikolektor;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "tabel_user")
public class User {
    @PrimaryKey
    @NonNull
    public String nik;
    public String nama;
    public String password;

    public User(@NonNull String nik, String nama, String password) {
        this.nik = nik;
        this.nama = nama;
        this.password = password;
    }
}
