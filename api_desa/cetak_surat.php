<?php
header('Content-Type: application/pdf');
include 'koneksi.php';
require('fpdf.php');

$id_pengajuan = $_GET['id'] ?? '';

if (empty($id_pengajuan)) {
    die("Parameter ID tidak ditemukan.");
}

$id_pengajuan = mysqli_real_escape_string($koneksi, $id_pengajuan);

$q_pengajuan = mysqli_query($koneksi, "SELECT * FROM pengajuan WHERE id_pengajuan = '$id_pengajuan'");
$pengajuan = mysqli_fetch_assoc($q_pengajuan);

if (!$pengajuan) {
    die("Data pengajuan tidak ditemukan.");
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

// GENERATE DRAF PDF dengan FPDF
$pdf = new FPDF();
$pdf->AddPage();
$pdf->SetFont('Arial', 'B', 14);
$pdf->Cell(0, 10, 'PEMERINTAH KABUPATEN BLITAR', 0, 1, 'C');
$pdf->Cell(0, 10, 'KECAMATAN KANIGORO', 0, 1, 'C');
$pdf->SetFont('Arial', 'B', 16);
$pdf->Cell(0, 10, 'DESA BANGGLE', 0, 1, 'C');
$pdf->Line(10, 40, 200, 40);

$pdf->Ln(10);
$pdf->SetFont('Arial', 'BU', 12);
$pdf->Cell(0, 10, strtoupper($jenis_surat), 0, 1, 'C');
$pdf->SetFont('Arial', '', 11);
$pdf->Cell(0, 5, 'Nomor : ' . $kode_surat, 0, 1, 'C');

$pdf->Ln(10);
$pdf->MultiCell(0, 8, $isi_pembuka);
$pdf->Ln(5);
$pdf->Cell(50, 8, 'Nama', 0, 0); $pdf->Cell(0, 8, ': ' . $nama_pemohon, 0, 1);
$pdf->Cell(50, 8, 'NIK', 0, 0); $pdf->Cell(0, 8, ': ' . $nik_pemohon, 0, 1);
$pdf->Cell(50, 8, 'Keperluan', 0, 0); $pdf->Cell(0, 8, ': ' . $keperluan, 0, 1);

$pdf->Ln(5);
$pdf->MultiCell(0, 8, $isi_penutup);

// === BLOK TANDA TANGAN ===
$pdf->Ln(15);
$pdf->Cell(120, 6, '', 0, 0);
$pdf->Cell(70, 6, 'Banggle, ' . date('d-m-Y'), 0, 1, 'C');
$pdf->Cell(120, 6, '', 0, 0);
$pdf->Cell(70, 6, 'Kepala Desa Banggle', 0, 1, 'C');

$pdf->Ln(5);
$pdf->SetFont('Arial', 'I', 10);
$pdf->Cell(120, 12, '', 0, 0);
$pdf->Cell(70, 12, 'Ditandatangani secara Elektronik', 0, 1, 'C');

$pdf->Ln(5);
$pdf->SetFont('Arial', 'BU', 12);
$pdf->Cell(120, 6, '', 0, 0);
$pdf->Cell(70, 6, 'NUR HUDA, S.Pd.', 0, 1, 'C');

// Output to browser directly
$pdf->Output('I', 'Draf_Surat_' . $id_pengajuan . '.pdf');

mysqli_close($koneksi);
?>
