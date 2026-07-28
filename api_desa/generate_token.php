<?php
if (file_exists(__DIR__ . '/vendor/autoload.php')) {
    require_once __DIR__ . '/vendor/autoload.php';
} else {
    require_once __DIR__ . '/../vendor/autoload.php';
}

// Pastikan dijalankan dari Terminal (CLI)
if (php_sapi_name() != 'cli') {
    die("Script ini HANYA BOLEH dijalankan melalui Terminal/CMD.");
}

$client = new Google\Client();
$client->setApplicationName('Aplikasi Desa Banggle');
$client->setScopes([Google\Service\Drive::DRIVE_FILE]);
$client->setAuthConfig('credentials_oauth.json'); // File dari Langkah 1
$client->setAccessType('offline');
$client->setPrompt('select_account consent');

$tokenPath = 'token.json';

// Cek apakah token sudah ada
if (file_exists($tokenPath)) {
    echo "File token.json sudah ada! Hapus file lama jika ingin login ulang.\n";
    exit;
}

// Minta URL Otorisasi
$authUrl = $client->createAuthUrl();
printf("1. Silakan Buka link berikut di Browser Kamu:\n%s\n\n", $authUrl);
print("2. Login pakai akun Google Desa, lalu setujui perizinannya.\n");
print("3. Setelah setuju, Google akan memberikan KODE (atau gagal load localhost, COPY CODE dari URL).\n");
print("4. Paste KODE tersebut di sini: ");

$authCode = trim(fgets(STDIN));

// Tukar kode dengan Token
$accessToken = $client->fetchAccessTokenWithAuthCode($authCode);

if (array_key_exists('error', $accessToken)) {
    throw new Exception(join(', ', $accessToken));
}

// Simpan token ke file
if (!file_exists(dirname($tokenPath))) {
    mkdir(dirname($tokenPath), 0700, true);
}
file_put_contents($tokenPath, json_encode($accessToken));
echo "Bagus! File token.json berhasil dibuat dan disimpan.\n";
