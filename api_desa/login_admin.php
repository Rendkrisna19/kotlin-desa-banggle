<?php
error_reporting(0);
ini_set('display_errors', 0);
header('Content-Type: application/json; charset=utf-8');

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    include 'koneksi.php';

    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        echo json_encode(["status" => "error", "message" => "Metode request tidak valid."]);
        exit();
    }

    $username = isset($_POST['username']) ? trim($_POST['username']) : '';
    $password = isset($_POST['password']) ? $_POST['password'] : '';

    if (empty($username) || empty($password)) {
        echo json_encode(["status" => "error", "message" => "Semua kolom wajib diisi."]);
        exit();
    }

    $username_esc = mysqli_real_escape_string($koneksi, $username);

    // Query ke tabel users terpadu — BUKAN admin_desa (tabel itu sudah dihapus)
    $sql    = "SELECT * FROM users WHERE username = '$username_esc' AND role = 'admin' LIMIT 1";
    $result = mysqli_query($koneksi, $sql);

    if (!$result || mysqli_num_rows($result) === 0) {
        echo json_encode(["status" => "error", "message" => "Akun admin tidak ditemukan."]);
        exit();
    }

    $row = mysqli_fetch_assoc($result);

    if (!password_verify($password, $row['password'])) {
        echo json_encode(["status" => "error", "message" => "Username atau password salah."]);
        exit();
    }

    echo json_encode([
        "status"  => "success",
        "message" => "Login berhasil, Selamat datang!",
        "data"    => [
            "id_admin"     => intval($row['id_user']),
            "username"     => $row['username'],
            "nama_lengkap" => $row['nama_lengkap'],
            "jabatan"      => "Administrator Sistem"
        ]
    ]);

    mysqli_close($koneksi);

} catch (mysqli_sql_exception $e) {
    echo json_encode([
        "status"  => "error",
        "message" => "Database error: " . $e->getMessage()
    ]);
    exit();
} catch (Exception $e) {
    echo json_encode([
        "status"  => "error",
        "message" => "Server error: " . $e->getMessage()
    ]);
    exit();
}
