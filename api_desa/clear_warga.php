<?php
include 'koneksi.php';
mysqli_query($koneksi, "DELETE FROM users WHERE username='warga_demo'");
mysqli_query($koneksi, "DELETE FROM pengajuan WHERE username='warga_demo'");
echo 'OK';
?>
