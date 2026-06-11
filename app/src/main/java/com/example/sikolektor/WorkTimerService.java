package com.example.sikolektor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class WorkTimerService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Implementasi pengukur waktu kerja bisa ditambahkan di sini
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
