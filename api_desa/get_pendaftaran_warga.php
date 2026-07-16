<?php
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

$query = "SELECT username, nama_lengkap, status_akun FROM users WHERE role = 'masyarakat' ORDER BY created_at DESC";
$result = mysqli_query($koneksi, $query);

if ($result) {
    $array_data = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $array_data[] = $row;
    }
    
    echo json_encode([
        "status" => "success",
        "data" => $array_data
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Gagal mengambil data warga: " . mysqli_error($koneksi)
    ]);
}

mysqli_close($koneksi);
?>
