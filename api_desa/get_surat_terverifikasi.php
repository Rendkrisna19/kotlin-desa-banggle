<?php
header('Content-Type: application/json');
include 'koneksi.php';

$response = array();

// Query memfilter double validasi: Admin = Terverifikasi DAN RT/RW = Disetujui, dan belum diproses Sekdes
$sql = "SELECT id_pengajuan, jenis_surat, nama_pemohon, nik_pemohon, status_admin, status_rtrw, tanggal_pengajuan 
        FROM pengajuan 
        WHERE status_admin = 'Terverifikasi' AND (status_akhir = 'Menunggu Sekdes' OR status_akhir = 'Menunggu')
        ORDER BY id_pengajuan DESC";

$result = mysqli_query($koneksi, $sql);

if ($result) {
    $list_surat = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $row['id_pengajuan'] = (int)$row['id_pengajuan'];
        $list_surat[] = $row;
    }
    $response['status'] = 'success';
    $response['data'] = $list_surat;
} else {
    $response['status'] = 'error';
    $response['message'] = 'Gagal memuat data pengajuan';
}

echo json_encode($response);
mysqli_close($koneksi);
?>
