<?php
header('Content-Type: application/json');
include 'koneksi.php';

$response = array();

// Mengambil surat yang sudah diverifikasi (status_rtrw tidak Menunggu)
$sql = "SELECT p.id_pengajuan as id, p.username, p.jenis_surat, p.keperluan, p.status_rtrw as status, b.url_file as dokumen 
        FROM pengajuan p
        LEFT JOIN berkas_pengajuan b ON p.id_pengajuan = b.id_pengajuan
        WHERE p.status_rtrw != 'Menunggu'
        ORDER BY p.id_pengajuan DESC";

$result = mysqli_query($koneksi, $sql);

if ($result) {
    $list_surat = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $row['id'] = (int)$row['id'];
        $list_surat[] = $row;
    }
    $response['status'] = 'success';
    $response['data'] = $list_surat;
} else {
    $response['status'] = 'error';
    $response['message'] = 'Gagal memuat data riwayat verifikasi';
}

echo json_encode($response);
mysqli_close($koneksi);
?>
