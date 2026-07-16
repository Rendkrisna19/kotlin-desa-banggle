<?php
header("Content-Type: application/json; charset=UTF-8");
include_once "koneksi.php";

$query = "SELECT id_pengajuan, nama_pemohon AS nama_warga, jenis_surat, tanggal_pengajuan, status_akhir AS status, catatan_sekdes AS catatan 
          FROM pengajuan 
          WHERE status_akhir = 'Siap Disahkan'
          ORDER BY id_pengajuan DESC";

$result = mysqli_query($koneksi, $query);

if ($result) {
    $array_data = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $array_data[] = array(
            "id_pengajuan" => (int)$row['id_pengajuan'],
            "nama_warga" => $row['nama_warga'],
            "jenis_surat" => $row['jenis_surat'],
            "tanggal_pengajuan" => $row['tanggal_pengajuan'],
            "status" => $row['status'],
            "catatan" => $row['catatan'],
            "berkas_persyaratan" => array()
        );
    }
    
    echo json_encode(array(
        "status" => "success",
        "data" => $array_data
    ));
} else {
    echo json_encode(array(
        "status" => "error",
        "message" => "Gagal mengambil data: " . mysqli_error($koneksi)
    ));
}
mysqli_close($koneksi);
?>
