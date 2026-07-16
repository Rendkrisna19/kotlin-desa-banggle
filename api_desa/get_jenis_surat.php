<?php
header('Content-Type: application/json');
include 'koneksi.php';
$query = mysqli_query($koneksi, "SELECT * FROM layanan ORDER BY id DESC");
$data = array();
while ($row = mysqli_fetch_assoc($query)) {
    $data[] = array(
        'id' => (int)$row['id'],
        'nama_surat' => $row['nama_layanan']
    );
}
echo json_encode(array('status' => 'success', 'data' => $data));
mysqli_close($koneksi);
?>
