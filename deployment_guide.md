# Panduan Deployment & Konfigurasi Server (Kotlin & PHP)

Dokumen ini adalah panduan yang harus kamu atau *client*-mu ikuti ketika memindahkan aplikasi dari komputer lokal (Laragon/XAMPP) ke Server Publik/Hosting sungguhan agar aplikasi Android bisa terhubung dengan *backend* secara *online*.

---

## TAHAP 1: Konfigurasi Database & Backend (Sisi Server / cPanel)

Ketika kamu meng-upload folder `api_desa` ke dalam *File Manager* di cPanel (misalnya diletakkan di dalam folder `public_html/api_desa`), kamu wajib menghubungkannya kembali dengan *Database* MySQL yang baru.

1. Buat Database MySQL baru di cPanel (Menu: *MySQL® Databases*).
2. Buka file `api_desa/koneksi.php`.
3. Sesuaikan konfigurasi berikut dengan kredensial database di cPanel:

```php
<?php
// Buka api_desa/koneksi.php
$host = "localhost"; // Biarkan localhost jika database & file berada di hosting yang sama
$user = "u1234567_admin"; // Ganti dengan Username Database di cPanel
$pass = "password_rahasia_123"; // Ganti dengan Password Database di cPanel
$db   = "u1234567_desa_kotlin"; // Ganti dengan Nama Database di cPanel
```
*(Jangan lupa import/dump file `.sql` kamu ke phpMyAdmin di cPanel agar tabel-tabelnya terbentuk).*

---

## TAHAP 2: Konfigurasi API Client (Sisi Android / Kotlin)

Aplikasi Android tidak akan tahu di mana letak server baru-mu kecuali kamu memberitahu alamat (URL) pastinya. Sebelum kamu menekan tombol **Build > Generate Signed Bundle / APK** di Android Studio, kamu **WAJIB** mengubah satu baris kode krusial.

1. Buka Android Studio.
2. Navigasi ke lokasi file: `app/src/main/java/com/desabanggle/ovylia/ApiClient.kt`.
3. Cari baris ke-13 yang berisi variabel `BASE_URL`.
4. Ubah IP Localhost `10.0.2.2` menjadi **Domain Hosting Client**.

**SEBELUM (Mode Testing Lokal):**
```kotlin
const val BASE_URL = "http://10.0.2.2/aplikasi-kotlin/api_desa/"
```

**SESUDAH (Mode Production / Online):**
```kotlin
const val BASE_URL = "https://www.namadomainclient.com/api_desa/"
```
*(Catatan Krusial: Pastikan selalu menambahkan tanda garis miring `/` di akhir URL! Jika terlewat, aplikasi akan mengalami error "Base URL must end in /").*

---

## TAHAP 3: Validasi File Media (Google Drive & Path)

Jika aplikasi ini sudah dipindahkan ke server lain, pastikan hal-hal berikut terkait file tidak *error*:
1. **Google Drive API:** Jika kamu menggunakan OAuth `token.json` yang sudah dibuat (lihat panduan Google Drive OAuth sebelumnya), pastikan file `token.json` dan `credentials_oauth.json` ikut ter-upload ke dalam folder `api_desa` di hosting. Token tersebut tidak akan kadaluwarsa selama tidak dicabut dari Google Account.
2. **Folder Uploads:** Pastikan folder `api_desa/uploads/` memiliki izin Tulis (*Write Permission / CHMOD 777 atau 755*) agar PHP bisa menyimpan foto profil/dokumen lokal tanpa kendala akses (*Permission Denied*).

## Checklist Akhir Sebelum Serah Terima (Handover):
- [ ] Database sudah di-import ke server online.
- [ ] `koneksi.php` sudah menggunakan *User* dan *Password* cPanel.
- [ ] `ApiClient.kt` di Android sudah menggunakan Domain Client, bukan IP 10.0.2.2.
- [ ] Folder `uploads` di cPanel sudah *writable*.
- [ ] Berhasil *Build APK* tanpa error, dan APK sudah bisa di-install.
