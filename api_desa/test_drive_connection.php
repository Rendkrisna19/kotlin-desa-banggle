<?php
// ============================================================
// test_drive_connection.php
// Endpoint untuk cek apakah Google Drive API sudah terhubung
// Akses via browser: http://localhost/api_desa/test_drive_connection.php
// ============================================================
header('Content-Type: application/json; charset=utf-8');
require_once 'google_drive_helper.php';

$cek = checkDriveConnection();

if (!$cek['connected']) {
    echo json_encode([
        'status'  => 'error',
        'message' => $cek['message'],
        'step'    => 'Ikuti panduan setup di README_GDRIVE.txt'
    ]);
    exit;
}

// Coba upload file test kecil
$testFile = tempnam(sys_get_temp_dir(), 'test_drive_');
file_put_contents($testFile, 'Test koneksi Google Drive API dari Desa Banggle - ' . date('Y-m-d H:i:s'));

$result = uploadToDrive($testFile, 'test_koneksi_' . date('His') . '.txt', 'text/plain');
unlink($testFile); // Hapus file temp lokal

if ($result['success']) {
    // Hapus file test dari Drive setelah berhasil
    deleteDriveFile($result['file_id']);
    
    echo json_encode([
        'status'   => 'success',
        'message'  => '✅ Google Drive API berhasil terhubung!',
        'file_id'  => $result['file_id'],
        'drive_url'=> $result['drive_url']
    ]);
} else {
    echo json_encode([
        'status'  => 'error',
        'message' => '❌ Gagal upload ke Google Drive: ' . $result['error']
    ]);
}
