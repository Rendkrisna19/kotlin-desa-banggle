<?php
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Metode tidak diizinkan"]);
    exit();
}

$username_warga = $_POST['username_warga'] ?? '';
$status_baru = $_POST['status_baru'] ?? '';

if (empty($username_warga) || empty($status_baru)) {
    echo json_encode(["status" => "error", "message" => "Parameter tidak lengkap"]);
    exit();
}

$username_warga = mysqli_real_escape_string($koneksi, $username_warga);
$status_baru = mysqli_real_escape_string($koneksi, $status_baru);

// status_baru expected to be 'aktif' or 'nonaktif'
$query = "UPDATE users SET status_akun = '$status_baru' WHERE username = '$username_warga' AND role = 'masyarakat'";

if (mysqli_query($koneksi, $query)) {
    echo json_encode(["status" => "success", "message" => "Status akun berhasil diperbarui"]);
} else {
    echo json_encode(["status" => "error", "message" => "Gagal memperbarui status akun: " . mysqli_error($koneksi)]);
}

mysqli_close($koneksi);
?>
