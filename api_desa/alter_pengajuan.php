<?php
// ============================================================
// alter_pengajuan.php — Jalankan sekali untuk update schema
// Tambahkan kolom nama_pengaju dan no_ktp ke tabel pengajuan
// ============================================================
header('Content-Type: application/json');

$koneksi = mysqli_connect("localhost", "root", "", "db_desa");

if (!$koneksi) {
    echo json_encode(["status" => "error", "message" => "Koneksi DB gagal"]);
    exit;
}

$results = [];

// Cek apakah kolom nama_pengaju sudah ada
$check1 = mysqli_query($koneksi, "SHOW COLUMNS FROM pengajuan LIKE 'nama_pengaju'");
if (mysqli_num_rows($check1) == 0) {
    $r1 = mysqli_query($koneksi, "ALTER TABLE pengajuan ADD COLUMN nama_pengaju VARCHAR(150) DEFAULT NULL AFTER nama_pemohon");
    $results[] = $r1 ? "✅ Kolom nama_pengaju ditambahkan" : "❌ Gagal: " . mysqli_error($koneksi);
} else {
    $results[] = "ℹ️ Kolom nama_pengaju sudah ada";
}

// Cek apakah kolom no_ktp sudah ada
$check2 = mysqli_query($koneksi, "SHOW COLUMNS FROM pengajuan LIKE 'no_ktp'");
if (mysqli_num_rows($check2) == 0) {
    $r2 = mysqli_query($koneksi, "ALTER TABLE pengajuan ADD COLUMN no_ktp VARCHAR(20) DEFAULT NULL AFTER nama_pengaju");
    $results[] = $r2 ? "✅ Kolom no_ktp ditambahkan" : "❌ Gagal: " . mysqli_error($koneksi);
} else {
    $results[] = "ℹ️ Kolom no_ktp sudah ada";
}

// Cek apakah kolom jenis_layanan sudah ada di tabel penilaian_layanan
$check3 = mysqli_query($koneksi, "SHOW COLUMNS FROM penilaian_layanan LIKE 'jenis_layanan'");
if (mysqli_num_rows($check3) == 0) {
    $r3 = mysqli_query($koneksi, "ALTER TABLE penilaian_layanan ADD COLUMN jenis_layanan VARCHAR(100) DEFAULT NULL");
    $results[] = $r3 ? "✅ Kolom jenis_layanan ditambahkan ke penilaian_layanan" : "❌ Gagal: " . mysqli_error($koneksi);
} else {
    $results[] = "ℹ️ Kolom jenis_layanan sudah ada di penilaian_layanan";
}

// Cek apakah kolom id_pengajuan sudah ada di tabel penilaian_layanan
$check4 = mysqli_query($koneksi, "SHOW COLUMNS FROM penilaian_layanan LIKE 'id_pengajuan'");
if (mysqli_num_rows($check4) == 0) {
    $r4 = mysqli_query($koneksi, "ALTER TABLE penilaian_layanan ADD COLUMN id_pengajuan INT DEFAULT NULL");
    $results[] = $r4 ? "✅ Kolom id_pengajuan ditambahkan ke penilaian_layanan" : "❌ Gagal: " . mysqli_error($koneksi);
} else {
    $results[] = "ℹ️ Kolom id_pengajuan sudah ada di penilaian_layanan";
}

mysqli_close($koneksi);

echo json_encode(["status" => "success", "results" => $results]);
?>
