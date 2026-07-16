<?php
header('Content-Type: application/json');
include 'koneksi.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['id_pengajuan']) && isset($_POST['status_akhir'])) {
        $id_pengajuan = intval($_POST['id_pengajuan']);
        $status_akhir = mysqli_real_escape_string($koneksi, $_POST['status_akhir']);
        $catatan_sekdes = isset($_POST['catatan_sekdes']) ? mysqli_real_escape_string($koneksi, $_POST['catatan_sekdes']) : '';
        $tanggal_now = date('Y-m-d H:i:s');

        // Cukup update status di database saja
        $sql = "UPDATE pengajuan SET 
                status_akhir = '$status_akhir', 
                catatan_sekdes = '$catatan_sekdes',
                tanggal_sekdes = '$tanggal_now' 
                WHERE id_pengajuan = $id_pengajuan";

        if (mysqli_query($koneksi, $sql)) {
            $response['status'] = 'success';
            $response['message'] = 'Persetujuan akhir berhasil disimpan';
        } else {
            $response['status'] = 'error';
            $response['message'] = 'Gagal memperbarui database: ' . mysqli_error($koneksi);
        }
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Parameter tidak lengkap';
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Metode request tidak valid';
}

echo json_encode($response);
mysqli_close($koneksi);
?>
