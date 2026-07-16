<?php
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Metode tidak diizinkan"]);
    exit();
}

$id_surat = $_POST['id_pengajuan'] ?? ($_POST['id_surat'] ?? '');
$status_baru = $_POST['status'] ?? ($_POST['status_baru'] ?? '');
$catatan_rtrw = $_POST['catatan'] ?? ($_POST['catatan_rtrw'] ?? '');

if (empty($id_surat) || empty($status_baru)) {
    echo json_encode(["status" => "error", "message" => "Parameter tidak lengkap"]);
    exit();
}

$id_surat = mysqli_real_escape_string($koneksi, $id_surat);
$status_baru = mysqli_real_escape_string($koneksi, $status_baru);
$catatan_rtrw = mysqli_real_escape_string($koneksi, $catatan_rtrw);

// Update status untuk RT/RW
$query = "UPDATE pengajuan 
          SET status_rtrw = '$status_baru', 
              catatan_rtrw = '$catatan_rtrw',
              tanggal_rtrw = NOW()";

// Jika RT/RW menolak, langsung set status_akhir ke ditolak
if (strtolower($status_baru) === 'ditolak' || strtolower($status_baru) === 'ditolak rt/rw') {
    $query .= ", status_akhir = 'Ditolak RT/RW'";
} else {
    // Hack/Fix: Set status_admin dan status_akhir agar flow bisa lanjut ke Sekdes
    $query .= ", status_admin = 'Terverifikasi', status_akhir = 'Menunggu Sekdes'";
}

$query .= " WHERE id_pengajuan = '$id_surat'";

if (mysqli_query($koneksi, $query)) {
    echo json_encode(["status" => "success", "message" => "Verifikasi berhasil disimpan"]);
} else {
    echo json_encode(["status" => "error", "message" => "Gagal menyimpan verifikasi: " . mysqli_error($koneksi)]);
}

mysqli_close($koneksi);
?>
