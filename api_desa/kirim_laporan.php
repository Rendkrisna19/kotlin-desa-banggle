<?php
include 'koneksi.php';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username   = mysqli_real_escape_string($koneksi, $_POST['username'] ?? '');
    $kategori   = mysqli_real_escape_string($koneksi, $_POST['kategori'] ?? '');
    $subjek     = mysqli_real_escape_string($koneksi, $_POST['subjek'] ?? '');
    $isi_laporan = mysqli_real_escape_string($koneksi, $_POST['isi_laporan'] ?? '');

    if (!empty($username) && !empty($kategori) && !empty($subjek) && !empty($isi_laporan)) {
        
        $query = "INSERT INTO laporan_users (username, kategori, subjek, isi_laporan) 
                  VALUES ('$username', '$kategori', '$subjek', '$isi_laporan')";

        if (mysqli_query($koneksi, $query)) {
            echo json_encode(["status" => "success", "message" => "Laporan users berhasil disimpan."]);
        } else {
            echo json_encode(["status" => "error", "message" => "Gagal menyimpan ke database."]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "Semua kolom form wajib diisi."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Metode request ditolak."]);
}

mysqli_close($koneksi);
?>

