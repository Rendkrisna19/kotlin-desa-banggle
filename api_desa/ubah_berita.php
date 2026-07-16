<?php
header('Content-Type: application/json');
include 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id = (int)$_POST['id_berita'];
    $judul = mysqli_real_escape_string($koneksi, $_POST['judul']);
    $isi = mysqli_real_escape_string($koneksi, $_POST['isi_berita']);

    $sql = "UPDATE berita_desa SET judul = '$judul', isi_berita = '$isi' WHERE id_berita = $id";
    if (mysqli_query($koneksi, $sql)) {
        echo json_encode(array('status' => 'success'));
    } else {
        echo json_encode(array('status' => 'error'));
    }
}
mysqli_close($koneksi);
?>
