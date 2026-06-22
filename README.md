# Si Kolektor

Si Kolektor adalah aplikasi berbasis Android yang dirancang khusus untuk mengoptimalkan efisiensi dan transparansi operasional para petugas penagihan (debt collector) di lapangan. Aplikasi ini berfungsi sebagai alat kerja digital terintegrasi untuk mengelola absensi harian, menavigasi kunjungan ke rumah nasabah, serta mengirimkan laporan kondisi nasabah secara langsung dan akurat.

---

## Fitur Utama

### 1. Presensi Berbasis Lokasi (Geofencing Attendance)
Memungkinkan petugas untuk melakukan pencatatan kehadiran (absen masuk dan pulang) secara digital. Proses absensi terikat dengan validasi lokasi terkini guna memastikan kepatuhan dan kehadiran petugas di titik yang telah ditentukan.

### 2. Manajemen Kunjungan Nasabah
Menampilkan daftar nasabah yang harus dikunjungi berdasarkan prioritas dan rute optimal. Fitur ini membantu petugas merencanakan perjalanan harian dengan lebih terstruktur.

### 3. Integrasi Google Maps API
Setiap aktivitas absensi dan kunjungan telah terintegrasi dengan Google Maps. Petugas dapat melihat titik koordinat rumah nasabah, mendapatkan panduan arah (navigasi), serta memastikan penentuan posisi (positioning) yang akurat saat berada di lapangan.

### 4. Pelaporan Kondisi Lapangan (Field Reporting)
Menyediakan formulir digital terstandardisasi bagi petugas untuk melaporkan hasil kunjungan dan kondisi riil nasabah secara *real-time*. Data ini penting untuk proses pengambilan keputusan dan pembaruan status portofolio nasabah.

---

## Teknologi yang Digunakan

Aplikasi ini dibangun menggunakan arsitektur Android modern dengan spesifikasi teknologi sebagai berikut:

* **Bahasa Pemrograman:** Java
* **Layanan Peta & Lokasi:** Google Maps API & Google Play Services Location
* **Penyimpanan Data Lokal:** SQLite dengan Room Persistence Library (digunakan sebagai demo lokal dan mekanisme *caching* data sebelum sinkronisasi)

---

## Kontributor

Proyek ini dikembangkan dan dikelola secara kolaboratif oleh:

* **Hasan Saeful Rohman** - Backend & Core System Developer
* **Dimas Iqbal Jailani** - Frontend Collaborator / Android UI Developer
