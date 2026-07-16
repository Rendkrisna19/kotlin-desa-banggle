<?php
header('Content-Type: application/json');
include 'koneksi.php';
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $nama = mysqli_real_escape_string($koneksi, $_POST['nama_surat'] ?? '');
    $desk = '';
    $sql = "INSERT INTO layanan (nama_layanan, deskripsi) VALUES ('$nama', '$desk')";
    if (mysqli_query($koneksi, $sql)) {
        echo json_encode(array('status' => 'success', 'message' => 'Berhasil'));
    } else {
        echo json_encode(array('status' => 'error', 'message' => mysqli_error($koneksi)));
    }
}
mysqli_close($koneksi);
?>
