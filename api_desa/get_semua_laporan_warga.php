<?php
// get_semua_laporan_warga.php
// Mengembalikan status yang tepat: "Pending" atau "Laporan Diterima"
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

$response = array();

$sql = "SELECT id_laporan, username, kategori, subjek, isi_laporan, tanggal_kirim, status_tindak_lanjut, tanggapan_admin 
        FROM laporan_users 
        ORDER BY id_laporan DESC";

$result = mysqli_query($koneksi, $sql);

if ($result) {
    $data_laporan = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $row['id_laporan'] = (int)$row['id_laporan'];

        // Normalisasi status_tindak_lanjut agar sesuai tampilan aplikasi
        $status_raw = strtolower(trim($row['status_tindak_lanjut'] ?? ''));
        if ($status_raw === 'ditindaklanjuti' || $status_raw === 'selesai' || $status_raw === 'diterima') {
            $row['status_tindak_lanjut'] = 'Laporan Diterima';
        } else {
            $row['status_tindak_lanjut'] = 'Pending';
        }

        $data_laporan[] = $row;
    }

    $response['status'] = 'success';
    $response['data']   = $data_laporan;
} else {
    $response['status']  = 'error';
    $response['message'] = 'Gagal memuat antrean laporan users';
}

echo json_encode($response);
mysqli_close($koneksi);
?>
