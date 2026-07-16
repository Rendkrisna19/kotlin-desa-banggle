<?php
header('Content-Type: application/json');
include 'koneksi.php';

$response = array();

if (isset($_GET['id_pengajuan']) || isset($_GET['id'])) {
    $id_pengajuan = intval($_GET['id_pengajuan'] ?? $_GET['id']);
    
    $sql = "SELECT id_pengajuan, jenis_surat, nama_pemohon, nik_pemohon, keperluan, tanggal_pengajuan,
                   status_rtrw, catatan_rtrw, tanggal_rtrw,
                   status_admin, catatan_admin, tanggal_admin
            FROM pengajuan 
            WHERE id_pengajuan = $id_pengajuan LIMIT 1";
            
    $result = mysqli_query($koneksi, $sql);
    
    if ($result && mysqli_num_rows($result) > 0) {
        $response['status'] = 'success';
        $response['data'] = mysqli_fetch_assoc($result);
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Detail pengajuan tidak ditemukan';
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Parameter ID tidak lengkap';
}

echo json_encode($response);
mysqli_close($koneksi);
?>
