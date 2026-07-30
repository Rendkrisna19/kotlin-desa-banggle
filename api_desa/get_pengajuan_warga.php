<?php
header("Content-Type: application/json; charset=UTF-8");
include_once "koneksi.php";

$query = "SELECT 
            p.id_pengajuan AS id, 
            p.username, 
            p.jenis_surat, 
            p.keperluan, 
            p.status_rtrw AS status,
            (SELECT url_file FROM berkas_pengajuan WHERE id_pengajuan = p.id_pengajuan AND nama_syarat = 'Foto KTP' LIMIT 1) AS file_ktp,
            (SELECT url_file FROM berkas_pengajuan WHERE id_pengajuan = p.id_pengajuan AND nama_syarat = 'Kartu Keluarga (KK)' LIMIT 1) AS file_kk,
            (SELECT url_file FROM berkas_pengajuan WHERE id_pengajuan = p.id_pengajuan AND nama_syarat = 'Berkas Utama' LIMIT 1) AS dokumen
          FROM pengajuan p 
          WHERE p.status_rtrw = 'Menunggu' 
          ORDER BY p.id_pengajuan DESC";

$result = mysqli_query($koneksi, $query);

if ($result) {
    $array_data = array();
    while ($row = mysqli_fetch_assoc($result)) {
        $array_data[] = array(
            "id" => (int)$row['id'],
            "id_pengajuan" => (int)$row['id'],
            "username" => $row['username'],
            "jenis_surat" => $row['jenis_surat'],
            "keperluan" => $row['keperluan'],
            "status" => $row['status'],
            "dokumen" => $row['dokumen'],
            "file_ktp" => $row['file_ktp'],
            "file_kk" => $row['file_kk']
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
