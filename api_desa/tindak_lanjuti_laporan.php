<?php
header('Content-Type: application/json');
include 'koneksi.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Menangkap parameter kiriman dari form @Field Retrofit
    $id_laporan = isset($_POST['id_laporan']) ? (int)$_POST['id_laporan'] : 0;
    $status_baru = isset($_POST['status_baru']) ? mysqli_real_escape_string($koneksi, $_POST['status_baru']) : '';
    $tanggapan   = isset($_POST['tanggapan']) ? mysqli_real_escape_string($koneksi, $_POST['tanggapan']) : '';

    if ($id_laporan > 0 && !empty($status_baru) && !empty($tanggapan)) {
        
        // Update status aduan dan simpan catatan respons resmi perangkat desa
        $sql_update = "UPDATE laporan_users 
                       SET status_tindak_lanjut = '$status_baru', tanggapan_admin = '$tanggapan' 
                       WHERE id_laporan = $id_laporan";

        if (mysqli_query($koneksi, $sql_update)) {
            $response['status'] = 'success';
            $response['message'] = 'Status tindak lanjut aduan berhasil diperbarui';
        } else {
            $response['status'] = 'error';
            $response['message'] = 'Gagal memperbarui data pada database';
        }
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Parameter input tidak lengkap atau kosong';
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Metode request tidak valid';
}

echo json_encode($response);
mysqli_close($koneksi);
?>
