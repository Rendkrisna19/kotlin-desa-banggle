<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
try {
$_SERVER['REQUEST_METHOD'] = 'POST';
$_POST['id_pengajuan'] = '1';
$_POST['token_ttd'] = '123456';
require 'sahkan_surat.php';
} catch (Throwable $e) {
    echo "ERROR: " . $e->getMessage() . " at " . $e->getFile() . ":" . $e->getLine();
}
?>
