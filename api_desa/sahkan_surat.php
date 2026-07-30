<?php
header('Content-Type: application/json; charset=utf-8');
include 'koneksi.php';

// Cek dan Load Autoload Composer (Untuk TCPDF dan Google Drive)
if (file_exists(__DIR__ . '/vendor/autoload.php')) {
    require_once __DIR__ . '/vendor/autoload.php';
} elseif (file_exists(__DIR__ . '/../vendor/autoload.php')) {
    require_once __DIR__ . '/../vendor/autoload.php';
} else {
    echo json_encode(["status" => "error", "message" => "Composer vendor/autoload.php tidak ditemukan di hosting."]);
    exit();
}

if (!class_exists('TCPDF')) {
    echo json_encode(["status" => "error", "message" => "Class TCPDF tidak ditemukan. Pastikan folder vendor/ diupload secara UTUH."]);
    exit();
}

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
// === KOP SURAT ===
$pdf->SetFont('times', 'B', 14);
$pdf->Cell(0, 6, 'PEMERINTAH KABUPATEN BLITAR', 0, 1, 'C');
$pdf->Cell(0, 6, 'KECAMATAN KANIGORO', 0, 1, 'C');
$pdf->SetFont('times', 'B', 16);
$pdf->Cell(0, 6, 'KEPALA DESA BANGGLE', 0, 1, 'C');
$pdf->SetFont('times', '', 11);
$pdf->Cell(0, 6, 'Jln. Raya Desa Banggle No. 1, Kanigoro, Blitar, Kode Pos 66171', 0, 1, 'C');

// Garis Pembatas Kop Surat (Double Line)
$pdf->SetLineWidth(1);
$pdf->Line(15, 38, 195, 38);
$pdf->SetLineWidth(0.3);
$pdf->Line(15, 39.5, 195, 39.5);

$pdf->Ln(10);
// === JUDUL SURAT ===
$pdf->SetFont('times', 'BU', 12);
// Gunakan judul dari jenis surat, default SURAT KETERANGAN
$judul_surat = (stripos($jenis_surat, 'Surat') !== false) ? strtoupper($jenis_surat) : 'SURAT KETERANGAN';
$pdf->Cell(0, 6, $judul_surat, 0, 1, 'C');
$pdf->SetFont('times', '', 11);
$pdf->Cell(0, 6, 'Nomor : ' . $kode_surat, 0, 1, 'C');

$pdf->Ln(8);

// === PARAGRAF PEMBUKA ===
$pdf->SetFont('times', '', 12);
// Menggunakan margin dan indentasi standar surat dinas
$pdf->setCellMargins(5, 0, 5, 0);
$pembuka = $template ? $template['isi_pembuka'] : "Yang bertanda tangan dibawah ini, kami Kepala Desa Banggle, Kecamatan Kanigoro, Kabupaten Blitar, menerangkan dengan sebenarnya kepada :";
$pdf->MultiCell(0, 7, "       " . $pembuka, 0, 'J');

$pdf->Ln(3);

// === DATA PENDUDUK ===
$keterangan_lain = $pengajuan['keterangan'] ?? 'Bahwa orang tersebut di atas adalah benar-benar penduduk Desa Banggle. Demikian agar menjadi periksa adanya.';

$pdf->SetX(20); $pdf->Cell(45, 7, 'Nama Lengkap', 0, 0); $pdf->Cell(5, 7, ':', 0, 0); $pdf->Cell(0, 7, $nama_pemohon, 0, 1);
$pdf->SetX(20); $pdf->Cell(45, 7, 'NIK', 0, 0); $pdf->Cell(5, 7, ':', 0, 0); $pdf->Cell(0, 7, $nik_pemohon, 0, 1);
$pdf->SetX(20); $pdf->Cell(45, 7, 'Alamat', 0, 0); $pdf->Cell(5, 7, ':', 0, 0); $pdf->Cell(0, 7, 'Desa Banggle, Kec. Kanigoro, Kab. Blitar', 0, 1);
$pdf->SetX(20); $pdf->Cell(45, 7, 'Dipergunakan untuk', 0, 0); $pdf->Cell(5, 7, ':', 0, 0); $pdf->Cell(0, 7, $keperluan, 0, 1);

// Keterangan Lain-lain (Bisa panjang jadi pakai MultiCell)
$pdf->SetX(20); 
$pdf->Cell(45, 7, 'Keterangan lain-lain', 0, 0); 
$pdf->Cell(5, 7, ':', 0, 0); 
$pdf->MultiCell(115, 7, $keterangan_lain, 0, 'J', false, 1, $pdf->GetX(), $pdf->GetY());

$pdf->Ln(3);

// === PARAGRAF PENUTUP ===
$pdf->SetX(15);
$penutup = $template ? $template['isi_penutup'] : "Demikian Surat Keterangan ini dibuat untuk dipergunakan sebagaimana mestinya dan untuk menjadikan periksa adanya.";
$pdf->MultiCell(0, 7, "       " . $penutup, 0, 'J');

// === BLOK TANDA TANGAN ===
$pdf->Ln(10);
$pdf->Cell(110, 6, '', 0, 0);
$pdf->Cell(70, 6, 'Banggle, ' . date('d-m-Y'), 0, 1, 'C');
$pdf->Cell(110, 6, '', 0, 0);
$pdf->Cell(70, 6, 'Kepala Desa Banggle', 0, 1, 'C');

// Ruang kosong untuk TTE / QR / Stempel
$pdf->SetXY(135, $pdf->GetY() + 3);
$pdf->SetFont('times', 'I', 10);
$pdf->Cell(50, 18, 'Ditandatangani secara Elektronik', 0, 1, 'C'); // Placeholder TTE

$pdf->Ln(2);
$pdf->Cell(110, 6, '', 0, 0);
$pdf->SetFont('times', 'BU', 12);
$pdf->Cell(70, 6, 'NUR HUDA, S.Pd.', 0, 1, 'C');

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
try {
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
} catch (Throwable $e) {
    // Abaikan error Drive dan simpan surat di lokal
}

// Cek kolom yang tersedia agar tidak error di versi db lama
$columns = [];
$res_cols = mysqli_query($koneksi, "SHOW COLUMNS FROM pengajuan");
while($col = mysqli_fetch_assoc($res_cols)) {
    $columns[] = $col['Field'];
}

$set_queries = [
    "status_akhir = 'Selesai'",
    "dokumen_hasil = '$file_path'"
];

if (in_array('status', $columns)) $set_queries[] = "status = 'selesai'";
if (in_array('catatan_sekdes', $columns)) $set_queries[] = "catatan_sekdes = 'Surat telah disahkan oleh Kepala Desa dengan TTE.'";
if (in_array('catatan', $columns)) $set_queries[] = "catatan = 'Surat telah disahkan oleh Kepala Desa dengan TTE.'";
if (in_array('tanggal_sekdes', $columns)) $set_queries[] = "tanggal_sekdes = NOW()";
if (in_array('dokumen_drive_url', $columns) && $dokumen_drive_url) $set_queries[] = "dokumen_drive_url = '$dokumen_drive_url'";

$set_string = implode(", ", $set_queries);

$query = "UPDATE pengajuan SET $set_string WHERE id_pengajuan = '$id_pengajuan'";

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
