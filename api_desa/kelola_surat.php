<?php
// ============================================================
// kelola_surat.php — Kelola pengajuan surat warga
// UPDATE: Terintegrasi Google Drive API (dengan fallback ke local)
// ============================================================
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

$action = $_POST['action'] ?? '';

if ($action === 'tambah') {
    $username     = mysqli_real_escape_string($koneksi, $_POST['username'] ?? 'users');
    $jenis_surat  = mysqli_real_escape_string($koneksi, $_POST['jenis_surat'] ?? 'Surat Pengantar Umum');
    $keterangan   = mysqli_real_escape_string($koneksi, $_POST['keterangan'] ?? 'Pengajuan dari Aplikasi');
    $keperluan    = mysqli_real_escape_string($koneksi, $_POST['keperluan'] ?? 'Keperluan Administrasi');
    $nama_pengaju = mysqli_real_escape_string($koneksi, $_POST['nama_pengaju'] ?? '');
    $no_ktp       = mysqli_real_escape_string($koneksi, $_POST['no_ktp'] ?? '');

    // Ambil data user dari DB
    $query_users  = mysqli_query($koneksi, "SELECT nama_lengkap, nik FROM users WHERE username = '$username'");
    $users        = mysqli_fetch_assoc($query_users);
    $nama_pemohon = $users ? $users['nama_lengkap'] : ($nama_pengaju ?: 'Pemohon');
    $nik_pemohon  = $users ? $users['nik'] : ($no_ktp ?: '0000000000000000');

    if (!empty($nama_pengaju)) $nama_pemohon = $nama_pengaju;
    if (!empty($no_ktp))       $nik_pemohon  = $no_ktp;

    // INSERT pengajuan utama
    $query_pengajuan = "INSERT INTO pengajuan
        (username, nama_pemohon, nik_pemohon, jenis_surat, keterangan, keperluan,
         status_rtrw, status_admin, status_akhir, tanggal_pengajuan)
        VALUES
        ('$username', '$nama_pemohon', '$nik_pemohon', '$jenis_surat', '$keterangan', '$keperluan',
         'Menunggu', 'Menunggu', 'Menunggu', NOW())";

    if (!mysqli_query($koneksi, $query_pengajuan)) {
        echo json_encode(["status" => "error", "message" => "Gagal menyimpan: " . mysqli_error($koneksi)]);
        exit;
    }

    $id_pengajuan = mysqli_insert_id($koneksi);

    // Direktori upload lokal (sebagai fallback)
    $upload_dir = __DIR__ . '/uploads/';
    if (!is_dir($upload_dir)) mkdir($upload_dir, 0777, true);

    // Cek apakah Google Drive sudah dikonfigurasi
    $use_drive = false;
    try {
        if (file_exists(__DIR__ . '/google_drive_helper.php')) {
            require_once 'google_drive_helper.php';
            $drive_check = checkDriveConnection();
            $use_drive   = $drive_check['connected'];
        }
    } catch (Throwable $e) {
        // Jika terjadi Fatal Error (misal: class Google_Client tidak ada), otomatis abaikan Drive dan pakai lokal
        $use_drive = false;
    }

    $files_map = [
        'file_ktp'    => 'Foto KTP',
        'file_kk'     => 'Kartu Keluarga (KK)',
        'file_syarat' => 'Berkas Utama'
    ];

    foreach ($files_map as $field => $label) {
        if (!isset($_FILES[$field]) || $_FILES[$field]['error'] !== UPLOAD_ERR_OK) continue;

        $orig_name = basename($_FILES[$field]['name']);
        $mime_type = $_FILES[$field]['type'] ?: 'application/octet-stream';
        $tmp_path  = $_FILES[$field]['tmp_name'];

        $drive_file_id = null;
        $drive_url     = null;
        $file_path     = null;
        $url_file      = null;

        if ($use_drive) {
            // ===== Upload ke Google Drive =====
            $drive_filename = $id_pengajuan . '_' . $field . '_' . time() . '_' . $orig_name;
            $result = uploadToDrive($tmp_path, $drive_filename, $mime_type);

            if ($result['success']) {
                $drive_file_id = $result['file_id'];
                $drive_url     = $result['drive_url'];
                $file_path     = 'drive://' . $result['file_id'];
                $url_file      = $result['drive_url'];
            } else {
                // Fallback ke lokal jika Drive gagal
                $file_name = time() . '_' . $field . '_' . $orig_name;
                $file_path = 'uploads/' . $file_name;
                if (move_uploaded_file($tmp_path, $upload_dir . $file_name)) {
                    $url_file = $file_name;
                }
            }
        } else {
            // ===== Upload ke Lokal (mode sebelum Drive dikonfigurasi) =====
            $file_name = time() . '_' . $field . '_' . $orig_name;
            $file_path = 'uploads/' . $file_name;
            if (move_uploaded_file($tmp_path, $upload_dir . $file_name)) {
                $url_file = $file_name;
            }
        }

        if ($file_path) {
            $sp  = mysqli_real_escape_string($koneksi, $file_path);
            $sf  = mysqli_real_escape_string($koneksi, $url_file ?? '');
            $sl  = mysqli_real_escape_string($koneksi, $label);

            // Cek apakah kolom drive sudah ada
            $check_col   = mysqli_query($koneksi, "SHOW COLUMNS FROM berkas_pengajuan LIKE 'drive_file_id'");
            $has_drv_col = mysqli_num_rows($check_col) > 0;

            if ($has_drv_col && $drive_file_id && $drive_url) {
                $sid = mysqli_real_escape_string($koneksi, $drive_file_id);
                $sdu = mysqli_real_escape_string($koneksi, $drive_url);
                mysqli_query($koneksi,
                    "INSERT INTO berkas_pengajuan (id_pengajuan, nama_syarat, file_path, url_file, drive_file_id, drive_url)
                     VALUES ('$id_pengajuan', '$sl', '$sp', '$sf', '$sid', '$sdu')"
                );
            } else {
                mysqli_query($koneksi,
                    "INSERT INTO berkas_pengajuan (id_pengajuan, nama_syarat, file_path, url_file)
                     VALUES ('$id_pengajuan', '$sl', '$sp', '$sf')"
                );
            }
        }
    }

    echo json_encode(["status" => "success", "message" => "Pengajuan berhasil dikirim."]);

} elseif ($action === 'edit') {
    $id_surat = $_POST['id_surat'] ?? '';
    if (empty($id_surat)) { echo json_encode(["status"=>"error","message"=>"ID tidak ada"]); exit(); }

    $cek  = mysqli_query($koneksi, "SELECT status_rtrw FROM pengajuan WHERE id_pengajuan='$id_surat'");
    $data = mysqli_fetch_assoc($cek);
    if (!$data || $data['status_rtrw'] !== 'Menunggu') {
        echo json_encode(["status"=>"error","message"=>"Data tidak valid atau sudah diproses."]); exit();
    }

    $ket = mysqli_real_escape_string($koneksi, $_POST['keterangan'] ?? '');
    if (mysqli_query($koneksi, "UPDATE pengajuan SET keterangan='$ket' WHERE id_pengajuan='$id_surat'")) {
        echo json_encode(["status"=>"success","message"=>"Pengajuan diperbarui."]);
    } else {
        echo json_encode(["status"=>"error","message"=>"Gagal update."]);
    }

} elseif ($action === 'hapus') {
    $id_surat = $_POST['id_surat'] ?? '';
    if (empty($id_surat)) { echo json_encode(["status"=>"error","message"=>"ID tidak ada"]); exit(); }

    $cek  = mysqli_query($koneksi, "SELECT status_rtrw FROM pengajuan WHERE id_pengajuan='$id_surat'");
    $data = mysqli_fetch_assoc($cek);
    if (!$data || $data['status_rtrw'] !== 'Menunggu') {
        echo json_encode(["status"=>"error","message"=>"Data tidak valid atau sudah diproses."]); exit();
    }

    // Hapus file dari Drive juga jika ada
    try {
        if (file_exists(__DIR__ . '/google_drive_helper.php')) {
            require_once 'google_drive_helper.php';
            $drive_check = checkDriveConnection();
            if ($drive_check['connected']) {
                $berkas_query = mysqli_query($koneksi,
                    "SELECT drive_file_id FROM berkas_pengajuan WHERE id_pengajuan='$id_surat'"
                );
                while ($b = mysqli_fetch_assoc($berkas_query)) {
                    if (!empty($b['drive_file_id'])) {
                        deleteDriveFile($b['drive_file_id']);
                    }
                }
            }
        }
    } catch (Throwable $e) {
        // Abaikan error Drive dan lanjutkan ke query DELETE database
    }

    if (mysqli_query($koneksi, "DELETE FROM pengajuan WHERE id_pengajuan='$id_surat'")) {
        echo json_encode(["status"=>"success","message"=>"Pengajuan dihapus."]);
    } else {
        echo json_encode(["status"=>"error","message"=>"Gagal hapus."]);
    }

} else {
    echo json_encode(["status"=>"error","message"=>"Aksi tidak valid."]);
}

mysqli_close($koneksi);
?>
