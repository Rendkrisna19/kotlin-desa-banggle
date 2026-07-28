<?php
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Metode tidak diizinkan"]);
    exit();
}

$id_pengajuan = $_POST['id_pengajuan'] ?? ($_POST['id_surat'] ?? ($_POST['id'] ?? ''));
$status       = $_POST['status'] ?? ($_POST['status_baru'] ?? ''); 
$catatan      = $_POST['catatan'] ?? ($_POST['catatan_rtrw'] ?? ($_POST['catatan_admin'] ?? ''));

if ($id_pengajuan === '' || $status === '') {
    echo json_encode(["status" => "error", "message" => "Parameter tidak lengkap"]);
    exit();
}

$id_pengajuan = mysqli_real_escape_string($koneksi, $id_pengajuan);
$status = mysqli_real_escape_string($koneksi, $status);
$catatan = mysqli_real_escape_string($koneksi, $catatan);

$status_rtrw  = (stripos($status, 'Disetujui') !== false) ? 'Disetujui' : 'Ditolak';
$status_admin = (stripos($status, 'Disetujui') !== false) ? 'Terverifikasi' : 'Ditolak';

$query = "UPDATE pengajuan 
          SET status_rtrw  = '$status_rtrw',
              catatan_rtrw = '$catatan',
              tanggal_rtrw = NOW(),
              status_admin = '$status_admin', 
              catatan_admin = '$catatan',
              tanggal_admin = NOW()";

if ($status_rtrw === 'Ditolak') {
    $query .= ", status_akhir = 'Ditolak RT/RW'";
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
