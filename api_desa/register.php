<?php
error_reporting(0);
ini_set('display_errors', 0);
header("Content-Type: application/json; charset=UTF-8");

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    include "koneksi.php";

    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        echo json_encode(["status" => "error", "message" => "Metode request tidak valid."]);
        exit();
    }

    $username     = isset($_POST['username'])     ? trim($_POST['username'])     : '';
    $password     = isset($_POST['password'])     ? trim($_POST['password'])     : '';
    $nama_lengkap = isset($_POST['nama_lengkap']) ? trim($_POST['nama_lengkap']) : '';
    $nik          = isset($_POST['nik'])          ? trim($_POST['nik'])          : '';

    // Validasi field kosong
    if (empty($username) || empty($password) || empty($nama_lengkap) || empty($nik)) {
        echo json_encode(["status" => "error", "message" => "Semua kolom wajib diisi."]);
        exit();
    }

    // Validasi panjang NIK
    if (strlen($nik) !== 16 || !ctype_digit($nik)) {
        echo json_encode(["status" => "error", "message" => "NIK harus berupa 16 digit angka."]);
        exit();
    }

    // Sanitasi input
    $username_esc     = mysqli_real_escape_string($koneksi, $username);
    $nama_lengkap_esc = mysqli_real_escape_string($koneksi, $nama_lengkap);
    $nik_esc          = mysqli_real_escape_string($koneksi, $nik);

    // Cek apakah username sudah digunakan
    // PERBAIKAN: kolom PK tabel users adalah 'id_user', bukan 'id_users'
    $cek_username = mysqli_query($koneksi, "SELECT id_user FROM users WHERE username = '$username_esc'");
    if (mysqli_num_rows($cek_username) > 0) {
        echo json_encode(["status" => "error", "message" => "Username sudah terdaftar. Gunakan username lain."]);
        exit();
    }

    // Cek apakah NIK sudah terdaftar
    // PERBAIKAN: kolom PK tabel users adalah 'id_user', bukan 'id_users'
    $cek_nik = mysqli_query($koneksi, "SELECT id_user FROM users WHERE nik = '$nik_esc'");
    if (mysqli_num_rows($cek_nik) > 0) {
        echo json_encode(["status" => "error", "message" => "NIK ini sudah pernah didaftarkan."]);
        exit();
    }

    // Hash password sebelum disimpan ke database
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);

    // Insert data warga baru ke tabel users terpadu (status_akun default aktif)
    $query = "INSERT INTO users (username, password, nama_lengkap, nik, role, status_akun)
              VALUES ('$username_esc', '$hashed_password', '$nama_lengkap_esc', '$nik_esc', 'masyarakat', 'aktif')";

    if (mysqli_query($koneksi, $query)) {
        echo json_encode(["status" => "success", "message" => "Pendaftaran berhasil! Silakan masuk dengan akun Anda."]);
    } else {
        echo json_encode(["status" => "error", "message" => "Gagal mendaftar: " . mysqli_error($koneksi)]);
    }

    mysqli_close($koneksi);

} catch (mysqli_sql_exception $e) {
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
    exit();
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => "Server error: " . $e->getMessage()]);
    exit();
}
