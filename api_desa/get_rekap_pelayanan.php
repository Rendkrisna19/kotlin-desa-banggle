<?php
header('Content-Type: application/json');
include 'koneksi.php';
$qTotal = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan");
$rTotal = mysqli_fetch_assoc($qTotal);
$qMenunggu = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan WHERE status_rtrw = 'Menunggu' OR status_admin = 'Menunggu'");
$rMenunggu = mysqli_fetch_assoc($qMenunggu);
$qDisetujui = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan WHERE status_admin = 'Terverifikasi'");
$rDisetujui = mysqli_fetch_assoc($qDisetujui);
$qDitolak = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan WHERE status_akhir LIKE 'Ditolak%' OR status_admin = 'Ditolak' OR status_rtrw LIKE 'Ditolak%'");
$rDitolak = mysqli_fetch_assoc($qDitolak);
$qSelesai = mysqli_query($koneksi, "SELECT COUNT(*) as total FROM pengajuan WHERE status_akhir = 'Selesai'");
$rSelesai = mysqli_fetch_assoc($qSelesai);
$data = array(
    'total_pengajuan' => (int)$rTotal['total'],
    'surat_pending' => (int)$rMenunggu['total'],
    'surat_disetujui' => (int)$rDisetujui['total'],
    'surat_ditolak' => (int)$rDitolak['total'],
    'surat_selesai' => (int)$rSelesai['total']
);
echo json_encode(array('status' => 'success', 'data' => $data));
mysqli_close($koneksi);
?>
