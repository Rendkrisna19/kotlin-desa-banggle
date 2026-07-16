<?php
// ============================================================
// alter_berkas.php — Migrasi DB untuk kolom file_ktp & file_kk
// Jalankan SEKALI di browser: http://localhost/api_desa/alter_berkas.php
// ============================================================
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php'; // Gunakan koneksi.php — kompatibel XAMPP & Laragon

$log = [];

// 1. Drop kolom file_lampiran jika masih ada (kolom lama)
$cek = mysqli_query($koneksi, "SHOW COLUMNS FROM pengajuan LIKE 'file_lampiran'");
if (mysqli_num_rows($cek) > 0) {
    if (mysqli_query($koneksi, "ALTER TABLE pengajuan DROP COLUMN file_lampiran")) {
        $log[] = "✅ Kolom file_lampiran dihapus.";
    } else {
        $log[] = "❌ Gagal hapus file_lampiran: " . mysqli_error($koneksi);
    }
} else {
    $log[] = "ℹ️ Kolom file_lampiran tidak ada (OK).";
}

// 2. Tambah kolom file_ktp jika belum ada
$cek = mysqli_query($koneksi, "SHOW COLUMNS FROM pengajuan LIKE 'file_ktp'");
if (mysqli_num_rows($cek) === 0) {
    if (mysqli_query($koneksi, "ALTER TABLE pengajuan ADD COLUMN file_ktp VARCHAR(500) NULL")) {
        $log[] = "✅ Kolom file_ktp ditambahkan.";
    } else {
        $log[] = "❌ Gagal tambah file_ktp: " . mysqli_error($koneksi);
    }
} else {
    $log[] = "ℹ️ Kolom file_ktp sudah ada (OK).";
}

// 3. Tambah kolom file_kk jika belum ada
$cek = mysqli_query($koneksi, "SHOW COLUMNS FROM pengajuan LIKE 'file_kk'");
if (mysqli_num_rows($cek) === 0) {
    if (mysqli_query($koneksi, "ALTER TABLE pengajuan ADD COLUMN file_kk VARCHAR(500) NULL")) {
        $log[] = "✅ Kolom file_kk ditambahkan.";
    } else {
        $log[] = "❌ Gagal tambah file_kk: " . mysqli_error($koneksi);
    }
} else {
    $log[] = "ℹ️ Kolom file_kk sudah ada (OK).";
}

// 4. Pastikan tabel laporan_users ada (keluhan/aspirasi)
$cek = mysqli_query($koneksi, "SHOW TABLES LIKE 'laporan_users'");
if (mysqli_num_rows($cek) === 0) {
    $create = "CREATE TABLE `laporan_users` (
        `id_laporan` INT(11) NOT NULL AUTO_INCREMENT,
        `username` VARCHAR(100) NOT NULL,
        `kategori` VARCHAR(100) NOT NULL,
        `subjek` VARCHAR(200) NOT NULL,
        `isi_laporan` TEXT NOT NULL,
        `tanggal_kirim` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        `status_tindak_lanjut` VARCHAR(50) NOT NULL DEFAULT 'Pending',
        `tanggapan_admin` TEXT DEFAULT NULL,
        PRIMARY KEY (`id_laporan`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    if (mysqli_query($koneksi, $create)) {
        $log[] = "✅ Tabel laporan_users dibuat.";
    } else {
        $log[] = "❌ Gagal buat tabel laporan_users: " . mysqli_error($koneksi);
    }
} else {
    $log[] = "ℹ️ Tabel laporan_users sudah ada (OK).";
}

// 5. Pastikan tabel berkas_pengajuan ada
$cek = mysqli_query($koneksi, "SHOW TABLES LIKE 'berkas_pengajuan'");
if (mysqli_num_rows($cek) === 0) {
    $create = "CREATE TABLE `berkas_pengajuan` (
        `id_berkas` INT(11) NOT NULL AUTO_INCREMENT,
        `id_pengajuan` INT(11) NOT NULL,
        `nama_syarat` VARCHAR(200) NOT NULL,
        `file_path` VARCHAR(500) NOT NULL,
        `url_file` VARCHAR(500) DEFAULT NULL,
        `tanggal_upload` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (`id_berkas`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    if (mysqli_query($koneksi, $create)) {
        $log[] = "✅ Tabel berkas_pengajuan dibuat.";
    } else {
        $log[] = "❌ Gagal buat tabel berkas_pengajuan: " . mysqli_error($koneksi);
    }
} else {
    $log[] = "ℹ️ Tabel berkas_pengajuan sudah ada (OK).";
}

// Tampilkan hasil
echo json_encode([
    "status" => "success",
    "log"    => $log,
    "pesan"  => "Migrasi selesai! Semua tabel dan kolom siap digunakan."
], JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);

mysqli_close($koneksi);
?>
