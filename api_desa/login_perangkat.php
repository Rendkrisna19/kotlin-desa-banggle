<?php
error_reporting(0);
ini_set('display_errors', 0);
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

// Cari akun perangkat desa (sekdes, kades, rtrw) di tabel users terpadu
$query  = mysqli_query($koneksi, "SELECT * FROM users WHERE username='$username_esc' LIMIT 1");

if (!$query || mysqli_num_rows($query) === 0) {
    echo json_encode(["status" => "error", "message" => "Username tidak ditemukan."]);
    exit();
}

$row = mysqli_fetch_assoc($query);

if (!password_verify($password, $row['password'])) {
    echo json_encode(["status" => "error", "message" => "Username atau password salah."]);
    exit();
}

// Hanya izinkan role perangkat desa
$allowed_roles = ['kades', 'sekdes', 'rtrw'];
if (!in_array($row['role'], $allowed_roles)) {
    echo json_encode(["status" => "error", "message" => "Akun ini bukan perangkat desa."]);
    exit();
}

echo json_encode([
    "status"  => "success",
    "message" => "Login berhasil",
    "data"    => [
        "id"       => intval($row['id_user']),
        "username" => $row['username'],
        "nama"     => $row['nama_lengkap'],
        "role"     => $row['role']
    ]
]);

mysqli_close($koneksi);
