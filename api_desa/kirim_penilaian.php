<?php
include 'koneksi.php';
// ============================================================
// kirim_penilaian.php — Kirim penilaian per layanan
// Parameter: username, rating, ulasan, jenis_layanan (opsional), id_pengajuan (opsional)
// ============================================================
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username       = mysqli_real_escape_string($koneksi, $_POST['username'] ?? '');
    $rating         = (int)($_POST['rating'] ?? 0);
    $ulasan         = mysqli_real_escape_string($koneksi, $_POST['ulasan'] ?? '');
    $jenis_layanan  = mysqli_real_escape_string($koneksi, $_POST['jenis_layanan'] ?? '');
    $id_pengajuan   = !empty($_POST['id_pengajuan']) ? (int)$_POST['id_pengajuan'] : null;

    if (!empty($username) && $rating > 0) {

        // Tentukan kolom id_pengajuan (nullable)
        $id_peng_value = $id_pengajuan !== null ? "'$id_pengajuan'" : "NULL";

        $query = "INSERT INTO penilaian_layanan (username, rating, ulasan, jenis_layanan, id_pengajuan) 
                  VALUES ('$username', '$rating', '$ulasan', '$jenis_layanan', $id_peng_value)";

        if (mysqli_query($koneksi, $query)) {
            echo json_encode(["status" => "success", "message" => "Penilaian Anda berhasil disimpan. Terima kasih!"]);
        } else {
            echo json_encode(["status" => "error", "message" => "Gagal menyimpan penilaian: " . mysqli_error($koneksi)]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "Parameter tidak lengkap. Username dan rating wajib diisi."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Akses ditolak."]);
}

mysqli_close($koneksi);
?>

