<?php
header("Content-Type: application/json; charset=UTF-8");
include_once "koneksi.php";

// Ambil pengajuan yang masih berstatus Menunggu untuk RT/RW
$query = "SELECT p.id_pengajuan AS id, p.username, p.jenis_surat, p.keperluan, p.status_rtrw AS status, b.url_file AS dokumen 
          FROM pengajuan p 
          LEFT JOIN berkas_pengajuan b ON p.id_pengajuan = b.id_pengajuan 
          WHERE p.status_rtrw = 'Menunggu' 
          ORDER BY p.id_pengajuan DESC";

$result = mysqli_query($koneksi, $query);

if ($result) {
    $array_data = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $array_data[] = array(
            "id" => (int)$row['id'],
            "username" => $row['username'],
            "jenis_surat" => $row['jenis_surat'],
            "keperluan" => $row['keperluan'],
            "status" => $row['status'],
            "dokumen" => $row['dokumen']
        );
    }
    
    echo json_encode(array(
        "status" => "success",
        "message" => "Data pengajuan berhasil dimuat",
        "data" => $array_data
    ));
} else {
    echo json_encode(array(
        "status" => "error",
        "message" => "Gagal mengeksekusi data: " . mysqli_error($koneksi),
        "data" => []
    ));
}

mysqli_close($koneksi);
?>
