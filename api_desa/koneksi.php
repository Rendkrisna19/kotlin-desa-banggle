<?php
error_reporting(0);
ini_set('display_errors', 0);

$host = "localhost";
$user = "root";
$pass = "";
$db   = "desa_kotlin";

$kon = mysqli_connect($host, $user, $pass, $db);
$koneksi = $kon;

if (mysqli_connect_errno()) {
    header('Content-Type: application/json; charset=utf-8');
    die(json_encode([
        "status"  => "error",
        "message" => "Koneksi database gagal: " . mysqli_connect_error()
    ]));
}
