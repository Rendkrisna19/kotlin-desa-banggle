<?php
header('Content-Type: application/json');
include 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $judul = mysqli_real_escape_string($koneksi, $_POST['judul']);
    $isi = mysqli_real_escape_string($koneksi, $_POST['isi_berita']);

    $sql = "INSERT INTO berita_desa (judul, isi_berita, tanggal_post) VALUES ('$judul', '$isi', NOW())";
    if (mysqli_query($koneksi, $sql)) {
        echo json_encode(array('status' => 'success'));
    } else {
        echo json_encode(array('status' => 'error'));
    }
}
mysqli_close($koneksi);
?>
