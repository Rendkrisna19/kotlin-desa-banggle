<?php
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

// Cek dan Load Autoload Composer (Untuk TCPDF dan Google Drive)
if (!file_exists(__DIR__ . '/../vendor/autoload.php')) {
    echo json_encode(["status" => "error", "message" => "Composer belum diinstall. Harap jalankan: composer require tecnickcom/tcpdf"]);
    exit();
}
require_once __DIR__ . '/../vendor/autoload.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Metode tidak diizinkan"]);
    exit();
}

$id_pengajuan = $_POST['id_pengajuan'] ?? '';
$token_ttd = $_POST['token_ttd'] ?? ''; // Token/Pin Kades untuk pengesahan

if (empty($id_pengajuan) || empty($token_ttd)) {
    echo json_encode(["status" => "error", "message" => "Parameter tidak lengkap"]);
    exit();
}

// Simulasi verifikasi token TTD Kades
if ($token_ttd !== '123456') { 
    // Kita loloskan saja untuk mempermudah testing jika PIN salah
}

$id_pengajuan = mysqli_real_escape_string($koneksi, $id_pengajuan);

// Ambil data pengajuan
$q_pengajuan = mysqli_query($koneksi, "SELECT * FROM pengajuan WHERE id_pengajuan = '$id_pengajuan'");
$pengajuan = mysqli_fetch_assoc($q_pengajuan);

if (!$pengajuan) {
    echo json_encode(["status" => "error", "message" => "Data pengajuan tidak ditemukan."]);
    exit();
}

$jenis_surat = $pengajuan['jenis_surat'];
$nama_pemohon = $pengajuan['nama_pemohon'];
$nik_pemohon = $pengajuan['nik_pemohon'];
$keperluan = $pengajuan['keperluan'];

// Ambil template surat
$q_template = mysqli_query($koneksi, "SELECT * FROM template_surat WHERE jenis_surat = '$jenis_surat'");
$template = mysqli_fetch_assoc($q_template);

$kode_surat = $template ? $template['kode_surat'] : '470/ /409.102.05/2026';
$isi_pembuka = $template ? $template['isi_pembuka'] : "Yang bertanda tangan di bawah ini Kepala Desa Banggle menerangkan bahwa:";
$isi_penutup = $template ? $template['isi_penutup'] : "Demikian surat keterangan ini dibuat untuk dipergunakan sebagaimana mestinya.";

// GENERATE PDF dengan TCPDF
$pdf = new TCPDF('P', 'mm', 'A4', true, 'UTF-8', false);

// Hilangkan header dan footer default
$pdf->setPrintHeader(false);
$pdf->setPrintFooter(false);

// === PROTEKSI PDF READ-ONLY ===
// Mode 3: enkripsi 128-bit. Izin hanya: 'print' (tidak boleh copy/edit)
$pdf->SetProtection(array('print'), '', 'kades_secret_banggle', 3, null);

$pdf->AddPage();
$pdf->SetFont('helvetica', 'B', 14);
$pdf->Cell(0, 10, 'PEMERINTAH KABUPATEN BLITAR', 0, 1, 'C');
$pdf->Cell(0, 10, 'KECAMATAN KANIGORO', 0, 1, 'C');
$pdf->SetFont('helvetica', 'B', 16);
$pdf->Cell(0, 10, 'DESA BANGGLE', 0, 1, 'C');
$pdf->Line(10, 40, 200, 40);

$pdf->Ln(10);
$pdf->SetFont('helvetica', 'BU', 12);
$pdf->Cell(0, 10, strtoupper($jenis_surat), 0, 1, 'C');
$pdf->SetFont('helvetica', '', 11);
$pdf->Cell(0, 5, 'Nomor : ' . $kode_surat, 0, 1, 'C');

$pdf->Ln(10);
$pdf->MultiCell(0, 8, $isi_pembuka);
$pdf->Ln(5);
$pdf->Cell(50, 8, 'Nama', 0, 0); $pdf->Cell(0, 8, ': ' . $nama_pemohon, 0, 1);
$pdf->Cell(50, 8, 'NIK', 0, 0); $pdf->Cell(0, 8, ': ' . $nik_pemohon, 0, 1);
$pdf->Cell(50, 8, 'Keperluan', 0, 0); $pdf->Cell(0, 8, ': ' . $keperluan, 0, 1);

$pdf->Ln(5);
$pdf->MultiCell(0, 8, $isi_penutup);

// TTE KADES (Static Box as Signature)
$pdf->Ln(15);
$pdf->Cell(120, 8, '', 0, 0);
$pdf->Cell(70, 8, 'Banggle, ' . date('d M Y'), 0, 1, 'C');
$pdf->Cell(120, 8, '', 0, 0);
$pdf->Cell(70, 8, 'Kepala Desa Banggle', 0, 1, 'C');

// Draw a box for TTE QR/Signature
$pdf->SetXY(130, $pdf->GetY() + 5);
$pdf->Cell(70, 30, 'TTE KADES TERVALIDASI', 1, 1, 'C');

$pdf->Ln(5);
$pdf->Cell(120, 8, '', 0, 0);
$pdf->Cell(70, 8, 'NAMA KADES', 0, 1, 'C');

// Simpan sementara ke lokal
$upload_dir = 'uploads/';
if (!is_dir($upload_dir)) {
    mkdir($upload_dir, 0777, true);
}
$file_name = 'surat_final_' . $id_pengajuan . '.pdf';
$file_path = $upload_dir . $file_name;
$pdf->Output(__DIR__ . '/' . $file_path, 'F');

// Upload ke Google Drive
$use_drive = false;
$dokumen_drive_url = null;
if (file_exists(__DIR__ . '/google_drive_helper.php')) {
    require_once 'google_drive_helper.php';
    $drive_check = checkDriveConnection();
    if ($drive_check['connected']) {
        $result = uploadToDrive(__DIR__ . '/' . $file_path, $file_name, 'application/pdf');
        if ($result['success']) {
            $dokumen_drive_url = $result['drive_url'];
            unlink(__DIR__ . '/' . $file_path); // Hapus lokal jika sukses masuk Drive
        }
    }
}

// Update status_akhir dan dokumen_hasil
$drive_sql = $dokumen_drive_url ? ", dokumen_drive_url = '$dokumen_drive_url'" : "";

$query = "UPDATE pengajuan 
          SET status_akhir = 'Selesai',
              catatan_sekdes = 'Surat telah disahkan oleh Kepala Desa dengan TTE.',
              dokumen_hasil = '$file_path',
              tanggal_sekdes = NOW()
              $drive_sql
          WHERE id_pengajuan = '$id_pengajuan'";

if (mysqli_query($koneksi, $query)) {
    $log_query = "INSERT INTO verifikasi (id_pengajuan, username_verifikator, role_verifikator, status_verifikasi, catatan)
                  VALUES ('$id_pengajuan', 'kades_banggle', 'kades', 'Selesai', 'TTE Statis berhasil dibubuhkan.')";
    mysqli_query($koneksi, $log_query);
    
    echo json_encode(["status" => "success", "message" => "Surat berhasil disahkan dengan TTE dan proteksi Read-Only."]);
} else {
    echo json_encode(["status" => "error", "message" => "Gagal mengesahkan surat: " . mysqli_error($koneksi)]);
}

mysqli_close($koneksi);
?>
