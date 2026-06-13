package com.example.sikolektor;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Activity utama yang mengelola navigasi antar fragment menggunakan Jetpack Navigation
public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<IntentSenderRequest> locationSettingsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi launcher untuk menangani hasil dari popup lokasi (Kriteria 4)
        locationSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Lokasi telah diaktifkan", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Lokasi diperlukan untuk fungsionalitas aplikasi", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Menghubungkan BottomNavigationView dengan NavController (Kriteria 1)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);
        }

        // Jalankan pengecekan status lokasi saat aplikasi dibuka
        checkLocationSettings();
    }

    /**
     * Fungsi untuk mengecek apakah setting lokasi (GPS) aktif.
     * Jika tidak aktif, akan memunculkan popup sistem Android untuk mengaktifkannya.
     */
    private void checkLocationSettings() {
        // Konfigurasi request lokasi (High Accuracy)
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true); // Memaksa popup muncul meskipun user pernah menolak sebelumnya

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        task.addOnCompleteListener(t -> {
            try {
                // Jika lokasi sudah aktif, t.getResult akan berjalan normal
                t.getResult(ApiException.class);
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Lokasi tidak aktif, tapi bisa diperbaiki dengan memunculkan dialog resolusi
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(
                                    resolvable.getResolution().getIntentSender()
                            ).build();
                            
                            // Jalankan popup sistem Android
                            locationSettingsLauncher.launch(intentSenderRequest);
                        } catch (Exception e) {
                            // Abaikan error eksekusi
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Perangkat tidak mendukung fitur ini
                        break;
                }
            }
        });
    }
}
