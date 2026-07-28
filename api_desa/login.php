<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
header('Content-Type: application/json; charset=utf-8');

include 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Metode request tidak valid."]);
    exit();
}

$username = isset($_POST['username']) ? trim($_POST['username']) : '';
$password = isset($_POST['password']) ? $_POST['password'] : '';

if (empty($username) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "Username dan password wajib diisi."]);
    exit();
}

$username_esc = mysqli_real_escape_string($koneksi, $username);
$sql    = "SELECT * FROM users WHERE username = '$username_esc' LIMIT 1";
$result = mysqli_query($koneksi, $sql);

if (!$result || mysqli_num_rows($result) === 0) {
    echo json_encode(["status" => "error", "message" => "Username tidak ditemukan."]);
    exit();
}

$row = mysqli_fetch_assoc($result);

if ($row['status_akun'] === 'menunggu') {
    echo json_encode(["status" => "error", "message" => "Akun Anda sedang menunggu verifikasi dari Admin Desa."]);
    exit();
} elseif ($row['status_akun'] === 'nonaktif') {
    echo json_encode(["status" => "error", "message" => "Akun Anda telah dinonaktifkan."]);
    exit();
}

if (!password_verify($password, $row['password'])) {
    echo json_encode(["status" => "error", "message" => "Password salah."]);
    exit();
}

echo json_encode([
    "status"   => "success",
    "message"  => "Login berhasil!",
    "username" => $row['username'],
    "role"     => $row['role'],
    "nama"     => $row['nama_lengkap'] ?? ''
]);

mysqli_close($koneksi);
