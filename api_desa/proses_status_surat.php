<?php
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Metode tidak diizinkan"]);
    exit();
}

$id_pengajuan = $_POST['id_pengajuan'] ?? '';
$status = $_POST['status'] ?? ''; 
$catatan = $_POST['catatan'] ?? '';

if (empty($id_pengajuan) || empty($status)) {
    echo json_encode(["status" => "error", "message" => "Parameter tidak lengkap"]);
    exit();
}

$id_pengajuan = mysqli_real_escape_string($koneksi, $id_pengajuan);
$status = mysqli_real_escape_string($koneksi, $status);
$catatan = mysqli_real_escape_string($koneksi, $catatan);

$status_admin = ($status === 'Disetujui') ? 'Terverifikasi' : 'Ditolak';

$query = "UPDATE pengajuan 
          SET status_admin = '$status_admin', 
              catatan_admin = '$catatan',
              tanggal_admin = NOW()";

if ($status_admin === 'Ditolak') {
    $query .= ", status_akhir = 'Ditolak Admin'";
} else {
    $query .= ", status_akhir = 'Menunggu Sekdes'";
}

$query .= " WHERE id_pengajuan = '$id_pengajuan'";

if (mysqli_query($koneksi, $query)) {
    echo json_encode(["status" => "success", "message" => "Status berhasil diperbarui"]);
} else {
    echo json_encode(["status" => "error", "message" => "Gagal memperbarui status: " . mysqli_error($koneksi)]);
}

mysqli_close($koneksi);
?>
