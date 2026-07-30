<?php
include 'koneksi.php';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';

    if (!empty($username)) {
        $username = mysqli_real_escape_string($koneksi, $username);
        
        // Cek apakah kolom dokumen_drive_url sudah ada
        $has_drive_col = false;
        $chk = mysqli_query($koneksi, "SHOW COLUMNS FROM pengajuan LIKE 'dokumen_drive_url'");
        if ($chk && mysqli_num_rows($chk) > 0) $has_drive_col = true;

        $drive_col = $has_drive_col ? ", dokumen_drive_url" : "";
        $sql = "SELECT id_pengajuan, jenis_surat, keterangan, status_rtrw, status_admin, status_akhir,
                       catatan_rtrw, catatan_admin, catatan_sekdes, dokumen_hasil $drive_col
                FROM pengajuan WHERE username = '$username' ORDER BY id_pengajuan DESC";
        $result = mysqli_query($koneksi, $sql);
        
        $list_surat = array();
        if ($result) {
            while ($row = mysqli_fetch_assoc($result)) {
                // Logika status general mapping ke UI
                $status = 'pending';
                if (strtolower(trim($row['status_akhir'])) === 'selesai') {
                    $status = 'selesai';
                } elseif (stripos($row['status_akhir'], 'tolak') !== false
                       || stripos($row['status_admin'], 'tolak') !== false
                       || stripos($row['status_rtrw'], 'tolak') !== false) {
                    $status = 'ditolak';
                } elseif ($row['status_admin'] === 'Terverifikasi'
                       || $row['status_rtrw'] === 'Disetujui'
                       || $row['status_rtrw'] === 'Disetujui RT/RW'
                       || $row['status_akhir'] === 'Menunggu Sekdes'
                       || $row['status_akhir'] === 'Siap Disahkan') {
                    $status = 'diproses';
                }
                
                $catatan = $row['catatan_sekdes'] ?: ($row['catatan_admin'] ?: $row['catatan_rtrw']);

                // Prioritaskan URL Google Drive jika ada, fallback ke path lokal
                $dokumen_drive = isset($row['dokumen_drive_url']) ? $row['dokumen_drive_url'] : null;
                $dokumen_url   = !empty($dokumen_drive) ? $dokumen_drive : $row['dokumen_hasil'];

                $list_surat[] = array(
                    "id_pengajuan"  => (int)$row['id_pengajuan'],
                    "jenis_surat"   => $row['jenis_surat'],
                    "keterangan"    => $row['keterangan'],
                    "status"        => $status,
                    "catatan"       => $catatan,
                    "dokumen_hasil" => $dokumen_url,  // Drive URL atau path lokal
                    "is_drive_url"  => !empty($dokumen_drive) ? true : false
                );
            }
            echo json_encode([
                "status" => "success",
                "data" => $list_surat
            ]);
        } else {
            echo json_encode(["status" => "error", "message" => "Gagal mengambil data: " . mysqli_error($koneksi)]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "Parameter username tidak boleh kosong."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Metode akses tidak sah."]);
}

mysqli_close($koneksi);
?>

