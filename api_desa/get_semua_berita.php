<?php
header('Content-Type: application/json');
include 'koneksi.php';

$sql = "SELECT * FROM berita_desa ORDER BY id_berita DESC";
$result = mysqli_query($koneksi, $sql);

if ($result) {
    $data = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $row['id_berita'] = (int)$row['id_berita'];
        $data[] = $row;
    }
    echo json_encode(array('status' => 'success', 'data' => $data));
} else {
    echo json_encode(array('status' => 'error', 'message' => 'Gagal mengambil data'));
}
mysqli_close($koneksi);
?>
