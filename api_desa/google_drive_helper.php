<?php
// ============================================================
// google_drive_helper.php
// Helper: Upload, ambil link publik, dan hapus file di Google Drive
// Menggunakan OAuth 2.0 (token.json)
// ============================================================

// ID Folder Google Drive tujuan upload
// Isi dengan ID folder dari URL: drive.google.com/drive/folders/[ID_INI]
define('GDRIVE_FOLDER_ID', '1tZDPDV0PA1Id0cMxxzyBKRoXw_C0M8sQ');

/**
 * Mendapatkan Google Drive Client menggunakan OAuth 2.0
 */
function getDriveClient() {
    if (file_exists(__DIR__ . '/vendor/autoload.php')) {
        require_once __DIR__ . '/vendor/autoload.php';
    } elseif (file_exists(__DIR__ . '/../vendor/autoload.php')) {
        require_once __DIR__ . '/../vendor/autoload.php';
    }
    
    $client = new Google\Client();
    $client->setApplicationName('Aplikasi Desa Banggle');
    $client->setScopes([Google\Service\Drive::DRIVE_FILE]);
    
    // Gunakan OAuth Client ID
    $client->setAuthConfig(__DIR__ . '/credentials_oauth.json');
    $client->setAccessType('offline');

    $tokenPath = __DIR__ . '/token.json';
    
    if (file_exists($tokenPath)) {
        $accessToken = json_decode(file_get_contents($tokenPath), true);
        $client->setAccessToken($accessToken);
    } else {
        throw new Exception("File token.json tidak ditemukan! Jalankan generate_token.php terlebih dahulu.");
    }

    // Jika token expired, perbarui secara otomatis
    if ($client->isAccessTokenExpired()) {
        if ($client->getRefreshToken()) {
            $client->fetchAccessTokenWithRefreshToken($client->getRefreshToken());
            // Simpan token yang sudah diperbarui
            file_put_contents($tokenPath, json_encode($client->getAccessToken()));
        } else {
            throw new Exception("Token expired dan tidak ada Refresh Token. Hapus token.json dan login ulang!");
        }
    }

    return new Google\Service\Drive($client);
}

/**
 * Upload file ke Google Drive menggunakan OAuth 2.0
 * @param string $filePath  Path lokal file yang akan diupload
 * @param string $fileName  Nama file di Google Drive
 * @param string $mimeType  MIME type file (contoh: 'application/pdf')
 * @return array ['success'=>bool, 'file_id'=>string, 'drive_url'=>string, 'error'=>string]
 */
function uploadToDrive(string $filePath, string $fileName, string $mimeType): array {
    // Validasi file ada
    if (!file_exists($filePath)) {
        return ['success' => false, 'error' => 'File tidak ditemukan: ' . $filePath];
    }

    try {
        $driveService = getDriveClient();

        // Metadata file
        $fileMetadata = new Google\Service\Drive\DriveFile([
            'name'    => $fileName,
            'parents' => [GDRIVE_FOLDER_ID]
        ]);

        // Upload file
        $fileContent = file_get_contents($filePath);
        $uploadedFile = $driveService->files->create(
            $fileMetadata,
            [
                'data'       => $fileContent,
                'mimeType'   => $mimeType,
                'uploadType' => 'multipart',
                'fields'     => 'id, name, webViewLink, webContentLink'
            ]
        );

        $fileId = $uploadedFile->getId();

        // Set permission publik (anyone with link can view)
        $permission = new Google\Service\Drive\Permission([
            'type' => 'anyone',
            'role' => 'reader'
        ]);
        $driveService->permissions->create($fileId, $permission);

        // URL untuk view/download
        $webViewLink    = "https://drive.google.com/file/d/{$fileId}/view";
        $directViewLink = "https://drive.google.com/uc?export=view&id={$fileId}";

        return [
            'success'      => true,
            'file_id'      => $fileId,
            'drive_url'    => $webViewLink,
            'direct_url'   => $directViewLink,
            'error'        => null
        ];

    } catch (Exception $e) {
        return [
            'success' => false,
            'file_id' => null,
            'drive_url' => null,
            'error'   => 'Google Drive Error: ' . $e->getMessage()
        ];
    }
}

/**
 * Hapus file dari Google Drive berdasarkan File ID
 * @param string $fileId  ID file Google Drive
 * @return bool
 */
function deleteDriveFile(string $fileId): bool {
    if (empty($fileId)) return false;
    
    try {
        $driveService = getDriveClient();
        $driveService->files->delete($fileId);
        return true;
    } catch (Exception $e) {
        error_log('Gagal hapus file Drive: ' . $e->getMessage());
        return false;
    }
}

/**
 * Cek apakah Google Drive sudah terkonfigurasi dengan benar
 * @return array ['connected'=>bool, 'message'=>string]
 */
function checkDriveConnection(): array {
    if (!file_exists(__DIR__ . '/credentials_oauth.json')) {
        return ['connected' => false, 'message' => 'credentials_oauth.json tidak ditemukan'];
    }
    if (!file_exists(__DIR__ . '/token.json')) {
        return ['connected' => false, 'message' => 'token.json tidak ditemukan'];
    }
    if (GDRIVE_FOLDER_ID === 'ISI_DENGAN_ID_FOLDER_GOOGLE_DRIVE_ANDA') {
        return ['connected' => false, 'message' => 'GDRIVE_FOLDER_ID belum diisi di google_drive_helper.php'];
    }
    $vendorExists = file_exists(__DIR__ . '/vendor/autoload.php') || file_exists(__DIR__ . '/../vendor/autoload.php');
    if (!$vendorExists) {
        return ['connected' => false, 'message' => 'Composer vendor/autoload.php tidak ditemukan'];
    }
    
    // Coba load koneksi
    try {
        getDriveClient();
        return ['connected' => true, 'message' => 'Konfigurasi Google Drive dengan OAuth siap digunakan.'];
    } catch (Exception $e) {
        return ['connected' => false, 'message' => 'Error OAuth: ' . $e->getMessage()];
    }
}
?>
