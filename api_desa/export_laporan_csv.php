<?php
header('Content-Type: text/csv; charset=utf-8');
header('Content-Disposition: attachment; filename="Rekap_Pelayanan_Desa_Banggle.csv"');
header('Pragma: no-cache');
header('Expires: 0');

include 'koneksi.php';

$output = fopen('php://output', 'w');
fprintf($output, chr(0xEF).chr(0xBB).chr(0xBF)); // UTF-8 BOM

// Header CSV
fputcsv($output, ['No', 'ID Pengajuan', 'Username', 'Nama Pemohon', 'Jenis Surat', 'Tanggal Pengajuan', 'Status RT/RW', 'Status Admin', 'Status Akhir']);

$sql = "SELECT 
    id_pengajuan,
    username,
    nama_pemohon,
    jenis_surat,
    tanggal_pengajuan,
    status_rtrw,
    status_admin,
    status_akhir
FROM pengajuan
ORDER BY id_pengajuan DESC";

$result = mysqli_query($koneksi, $sql);
$no = 1;
while ($row = mysqli_fetch_assoc($result)) {
    fputcsv($output, [
        $no++,
        $row['id_pengajuan'],
        $row['username'],
        $row['nama_pemohon'] ?? '-',
        $row['jenis_surat'],
        $row['tanggal_pengajuan'],
        $row['status_rtrw'],
        $row['status_admin'],
        $row['status_akhir']
    ]);
}

fclose($output);
mysqli_close($koneksi);
?>
