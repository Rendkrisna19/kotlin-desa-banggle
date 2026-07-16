<?php
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

$results = [];

// ==========================================
// 1. TAMBAH KOLOM GOOGLE DRIVE (PHASE 1)
// ==========================================

// Cek kolom di berkas_pengajuan
$check1 = mysqli_query($koneksi, "SHOW COLUMNS FROM berkas_pengajuan LIKE 'drive_file_id'");
if ($check1 === false) {
    // Tabel berkas_pengajuan tidak ada, buat tabelnya
    $create_table = "CREATE TABLE IF NOT EXISTS `berkas_pengajuan` (
        `id_berkas` INT(11) NOT NULL AUTO_INCREMENT,
        `id_pengajuan` INT(11) NOT NULL,
        `nama_syarat` VARCHAR(200) NOT NULL,
        `file_path` VARCHAR(500) NOT NULL,
        `url_file` VARCHAR(500) DEFAULT NULL,
        `drive_file_id` VARCHAR(255) DEFAULT NULL,
        `drive_url` TEXT DEFAULT NULL,
        `tanggal_upload` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (`id_berkas`),
        FOREIGN KEY (`id_pengajuan`) REFERENCES `pengajuan`(`id_pengajuan`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    
    if (mysqli_query($koneksi, $create_table)) {
        $results[] = "✅ Tabel berkas_pengajuan berhasil dibuat beserta kolom Google Drive";
    } else {
        $results[] = "❌ Gagal membuat tabel berkas_pengajuan: " . mysqli_error($koneksi);
    }
} else {
    if (mysqli_num_rows($check1) == 0) {
        mysqli_query($koneksi, "ALTER TABLE berkas_pengajuan ADD COLUMN drive_file_id VARCHAR(255) DEFAULT NULL AFTER url_file");
        mysqli_query($koneksi, "ALTER TABLE berkas_pengajuan ADD COLUMN drive_url TEXT DEFAULT NULL AFTER drive_file_id");
        $results[] = "✅ Kolom drive_file_id dan drive_url ditambahkan ke berkas_pengajuan";
    } else {
        $results[] = "ℹ️ Kolom Google Drive di berkas_pengajuan sudah ada";
    }
}

// Cek kolom di pengajuan
$check2 = mysqli_query($koneksi, "SHOW COLUMNS FROM pengajuan LIKE 'dokumen_drive_url'");
if ($check2 === false) {
    $results[] = "❌ Tabel pengajuan tidak ditemukan di database '" . $db . "'.";
} else {
    if (mysqli_num_rows($check2) == 0) {
        mysqli_query($koneksi, "ALTER TABLE pengajuan ADD COLUMN dokumen_drive_url TEXT DEFAULT NULL AFTER dokumen_hasil");
        $results[] = "✅ Kolom dokumen_drive_url ditambahkan ke pengajuan";
    } else {
        $results[] = "ℹ️ Kolom dokumen_drive_url di pengajuan sudah ada";
    }
}

// ==========================================
// 2. TAMBAH LAYANAN BARU KIA, KTP, KK (PHASE 3)
// ==========================================

$layanan_baru = [
    'Pembuatan KIA' => 'Kartu Identitas Anak (0-17 th)',
    'Permohonan KTP' => 'Pembuatan KTP Baru/Hilang',
    'Permohonan KK' => 'Pembuatan KK Baru/Perubahan'
];

foreach ($layanan_baru as $nama => $deskripsi) {
    $cek = mysqli_query($koneksi, "SELECT id FROM layanan WHERE nama_surat = '$nama'");
    if (mysqli_num_rows($cek) == 0) {
        mysqli_query($koneksi, "INSERT INTO layanan (nama_surat, deskripsi) VALUES ('$nama', '$deskripsi')");
        $id_layanan = mysqli_insert_id($koneksi);
        
        // Tambahkan syarat KTP dan KK secara otomatis untuk setiap layanan ini
        mysqli_query($koneksi, "INSERT INTO syarat_surat (id_jenis_surat, nama_syarat) VALUES ('$id_layanan', 'Foto KTP')");
        mysqli_query($koneksi, "INSERT INTO syarat_surat (id_jenis_surat, nama_syarat) VALUES ('$id_layanan', 'Kartu Keluarga (KK)')");
        
        $results[] = "✅ Layanan '$nama' beserta syarat KTP & KK ditambahkan";
    } else {
        $results[] = "ℹ️ Layanan '$nama' sudah ada";
    }
}

// ==========================================
// 3. TAMBAH TEMPLATE SURAT (PHASE 3)
// ==========================================
$templates = [
    'Pembuatan KIA' => ['KIA/01/2026', 'Menerangkan bahwa anak dari pemohon berikut mengajukan pembuatan Kartu Identitas Anak (KIA).'],
    'Permohonan KTP' => ['KTP/02/2026', 'Menerangkan bahwa warga tersebut di atas mengajukan permohonan pembuatan KTP/E-KTP.'],
    'Permohonan KK' => ['KK/03/2026', 'Menerangkan bahwa warga tersebut di atas mengajukan permohonan Kartu Keluarga (KK) baru/perubahan.']
];

foreach ($templates as $jenis => $data) {
    $cek = mysqli_query($koneksi, "SELECT id FROM template_surat WHERE jenis_surat = '$jenis'");
    if (mysqli_num_rows($cek) == 0) {
        $kode = $data[0];
        $pembuka = $data[1];
        mysqli_query($koneksi, "INSERT INTO template_surat (jenis_surat, kode_surat, isi_pembuka, isi_penutup) 
                                VALUES ('$jenis', '$kode', '$pembuka', 'Demikian surat pengantar ini dibuat untuk dipergunakan sebagaimana mestinya.')");
        $results[] = "✅ Template Surat untuk '$jenis' ditambahkan";
    }
}

echo json_encode(["status" => "success", "message" => "Update Database Selesai", "log" => $results]);
mysqli_close($koneksi);
?>
