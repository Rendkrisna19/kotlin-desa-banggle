<?php
header('Content-Type: application/json');
include 'koneksi.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    // 1. Hitung total semua pengajuan surat
    $qTotal = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan");
    $rTotal = mysqli_fetch_assoc($qTotal);
    $total_surat = intval($rTotal['total']);

    // 2. Hitung berdasarkan status spesifik
    // Menunggu verifikasi RT/RW atau Admin
    $qMenunggu = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan WHERE status_akhir = 'Menunggu'");
    $rMenunggu = mysqli_fetch_assoc($qMenunggu);

    // Sedang diproses / diverifikasi admin (termasuk Siap Disahkan dan Menunggu Sekdes)
    $qDiproses = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan WHERE status_akhir = 'Diproses' OR status_akhir = 'Siap Disahkan' OR status_akhir = 'Menunggu Sekdes'");
    $rDiproses = mysqli_fetch_assoc($qDiproses);

    // Surat selesai / disetujui Sekdes (Siap ambil)
    $qSelesai = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan WHERE status_akhir = 'Selesai'");
    $rSelesai = mysqli_fetch_assoc($qSelesai);

    // Pengajuan ditolak (Baik oleh RT/RW, Admin, maupun Sekdes)
    $qDitolak = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan WHERE status_akhir LIKE 'Ditolak%'");
    $rDitolak = mysqli_fetch_assoc($qDitolak);

    // Hitung total aspirasi warga
    $qAspirasi = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM laporan_users");
    $rAspirasi = mysqli_fetch_assoc($qAspirasi);

    // Susun data ke dalam response json
    $response['status'] = 'success';
    $response['data'] = array(
        'total_surat' => $total_surat,
        'menunggu' => intval($rMenunggu['total']),
        'diproses' => intval($rDiproses['total']),
        'selesai' => intval($rSelesai['total']),
        'ditolak' => intval($rDitolak['total']),
        'total_aspirasi' => intval($rAspirasi['total'])
    );
} else {
    $response['status'] = 'error';
    $response['message'] = 'Metode request tidak valid';
}

echo json_encode($response);
mysqli_close($koneksi);
?>
