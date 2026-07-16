<?php
header('Content-Type: application/json');
include 'koneksi.php';
$id = isset($_GET['id_jenis_surat']) ? (int)$_GET['id_jenis_surat'] : 0;
$query = mysqli_query($koneksi, "SELECT * FROM syarat_surat WHERE id_jenis_surat=$id ORDER BY id ASC");
$data = array();
while ($row = mysqli_fetch_assoc($query)) {
    $row['id'] = (int)$row['id'];
    $row['id_jenis_surat'] = (int)$row['id_jenis_surat'];
    $data[] = $row;
}
echo json_encode(array('status' => 'success', 'data' => $data));
mysqli_close($koneksi);
?>
