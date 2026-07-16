<?php
include 'koneksi.php';
// ============================================================
// get_surat_selesai_warga.php — Ambil surat yang sudah Selesai
// untuk keperluan penilaian per layanan
// ============================================================
$username = $_GET['username'] ?? '';

if (empty($username)) {
    echo json_encode(["status" => "error", "message" => "Parameter username wajib diisi."]);
    exit;
}

$username = mysqli_real_escape_string($koneksi, $username);

$query = "SELECT id_pengajuan, jenis_surat, tanggal_pengajuan, status_akhir 
          FROM pengajuan 
          WHERE username = '$username' AND status_akhir = 'Selesai'
          ORDER BY tanggal_pengajuan DESC";

$result = mysqli_query($koneksi, $query);

if (!$result) {
    echo json_encode(["status" => "error", "message" => "Query gagal: " . mysqli_error($koneksi)]);
    exit;
}

$data = [];
while ($row = mysqli_fetch_assoc($result)) {
    $data[] = $row;
}

if (empty($data)) {
    echo json_encode(["status" => "empty", "message" => "Belum ada surat yang selesai diproses.", "data" => []]);
} else {
    echo json_encode(["status" => "success", "data" => $data]);
}

mysqli_close($koneksi);
?>

