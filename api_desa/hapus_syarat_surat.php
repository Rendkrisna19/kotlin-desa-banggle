<?php
header('Content-Type: application/json');
include 'koneksi.php';
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id = (int)$_POST['id'];
    $sql = "DELETE FROM syarat_surat WHERE id=$id";
    if (mysqli_query($koneksi, $sql)) {
        echo json_encode(array('status' => 'success', 'message' => 'Berhasil'));
    } else {
        echo json_encode(array('status' => 'error', 'message' => mysqli_error($koneksi)));
    }
}
mysqli_close($koneksi);
?>
