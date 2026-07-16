<?php
header('Content-Type: application/json');
include 'koneksi.php';
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id = (int)$_POST['id_jenis_surat'];
    $nama = mysqli_real_escape_string($koneksi, $_POST['nama_syarat']);
    $sql = "INSERT INTO syarat_surat (id_jenis_surat, nama_syarat) VALUES ($id, '$nama')";
    if (mysqli_query($koneksi, $sql)) {
        echo json_encode(array('status' => 'success', 'message' => 'Berhasil'));
    } else {
        echo json_encode(array('status' => 'error', 'message' => mysqli_error($koneksi)));
    }
}
mysqli_close($koneksi);
?>
