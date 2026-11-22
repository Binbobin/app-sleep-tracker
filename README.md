Nightstand Smart Alarm & Sleep Environment Monitor

Aplikasi Android yang mengubah smartphone menjadi jam nakas pintar saat tidur. Aplikasi ini memantau kondisi kamar tidur sepanjang malam dan memungkinkan pengguna mengontrol alarm dengan gerakan fisik sederhana, tanpa perlu melihat layar.


Fitur Aplikasi

1. Pilih Durasi Tidur - Sebelum mulai, pengguna memilih mau tidur berapa lama lewat pop-up. Ada pilihan cepat (20 menit, 1.5 jam, 7.5 jam, dll.) atau atur manual.

2. Tampilan Jam Nakas - Saat sesi aktif, layar jadi serba hitam dengan jam digital besar. Hemat baterai dan tidak silau di kamar gelap.

3. Pantau Cahaya Kamar - Pakai Light Sensor HP untuk ukur terang/gelap kamar dalam satuan lux. Dikategorikan jadi Pitch Dark, Dim, atau Too Bright.

4. Deteksi Gangguan Gerak - Pakai Accelerometer untuk deteksi kalau HP kesenggol atau meja nakas tersentak saat tidur. Dicatat sebagai gangguan.

5. Flip-to-Snooze - Saat alarm bunyi, cukup balikkan HP menghadap meja dan alarm otomatis tunda 5 menit. Tidak perlu buka mata lihat layar.

6. Alarm Suara dan Getar - Alarm pakai nada bawaan HP, berdering terus sampai dimatikan atau di-snooze.

7. Laporan Otomatis - Selesai tidur, langsung muncul ringkasan: durasi, rata-rata cahaya, jumlah gangguan, dan rating kualitas lingkungan tidur.

8. Riwayat Tidur - Semua sesi tersimpan di database SQLite lokal. Bisa dilihat kapan saja, bisa dihapus per-sesi atau semua sekaligus.

9. Statistik - Halaman analisis menampilkan rata-rata durasi tidur, rata-rata cahaya, rata-rata gangguan dari seluruh riwayat.

10. Panel Simulasi - Untuk testing di emulator yang tidak punya sensor fisik, ada tombol simulasi lux, guncangan, dan flip.


Teknologi dan Arsitektur

Aplikasi ini ditulis menggunakan bahasa Kotlin. Seluruh tampilan dibangun dengan Jetpack Compose dan Material 3. Arsitektur yang dipakai adalah MVVM (Model-View-ViewModel). Database menggunakan SQLite native lewat SQLiteOpenHelper. State management pakai StateFlow dan Kotlin Coroutines. Alarm menggunakan MediaPlayer untuk suara dan Vibrator untuk getaran.

Penjelasan MVVM:
- Model adalah data class seperti SleepSession, EnvironmentRating, LuxCategory, dan database helper SQLite yang menangani penyimpanan data.
- View adalah layar-layar Compose (NightstandScreen, HistoryScreen, AnalyticsScreen) yang menampilkan data dari ViewModel.
- ViewModel adalah SleepViewModel yang mengatur semua logika: timer, sensor, alarm, dan operasi database.

Database SQLite menyimpan data di file nightstand_sleep.db dengan tabel sleep_sessions. Kolom yang disimpan: id, start_time, end_time, target_wake_time, avg_lux, max_lux, disturbance_count, environment_rating, dan notes.


Sensor yang Digunakan

Sensor 1: Light Sensor (Environment Sensor)
Tipe sensor: Sensor.TYPE_LIGHT. Sensor ini mengukur intensitas cahaya sekitar dalam satuan lux. Fungsinya untuk memantau terang atau gelap kamar tidur sepanjang malam. Hasilnya dikategorikan menjadi Pitch Dark (di bawah 2 lux), Dim (2 sampai 15 lux), dan Too Bright (di atas 15 lux). Polling rate diatur ke SENSOR_DELAY_NORMAL supaya hemat baterai.

Sensor 2: Accelerometer (Motion Sensor)
Tipe sensor: Sensor.TYPE_ACCELEROMETER. Sensor ini mengukur percepatan 3 sumbu (X, Y, Z). Punya dua fungsi. Pertama, deteksi guncangan atau senggolan sebagai gangguan tidur. Kedua, deteksi gerakan flip HP (posisi face-down) untuk snooze alarm otomatis saat alarm sedang bunyi. Polling rate diatur ke SENSOR_DELAY_UI.


Screenshot Aplikasi

(Tambahkan screenshot dari HP atau emulator di sini)
- Halaman Setup
- Mode Nightstand aktif
- Alarm berdering
- Pop-up pilih durasi tidur
- Dialog laporan sesi tidur
- Halaman riwayat tidur
- Halaman statistik


Cara Menjalankan

Yang dibutuhkan:
- Android Studio versi terbaru
- Android SDK (compileSdk 35)
- HP Android atau Emulator minimal Android 8.0 (API 26)

Langkah:
1. Buka folder proyek di Android Studio.
2. Tunggu Gradle sync selesai download semua dependensi.
3. Sambungkan HP Android via USB dengan USB Debugging aktif, atau buat emulator.
4. Klik tombol Run di Android Studio.

Cara pakai:
1. Pilih durasi tidur lalu tap Mulai Sekarang.
2. Taruh HP di meja nakas, layar menghadap atas.
3. Saat alarm bunyi, balikkan HP untuk snooze atau tekan tombol di layar.
4. Tap Akhiri Tidur untuk simpan laporan.

Kalau pakai emulator tanpa sensor, tekan tombol Uji Sensor di layar monitoring untuk simulasi.


