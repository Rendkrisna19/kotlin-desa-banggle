<?php
header('Content-Type: application/json');
include 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id = (int)$_POST['id_berita'];

    $sql = "DELETE FROM berita_desa WHERE id_berita = $id";
    if (mysqli_query($koneksi, $sql)) {
        echo json_encode(array('status' => 'success'));
    } else {
        echo json_encode(array('status' => 'error'));
    }
}
mysqli_close($koneksi);
?>
