<?php
header("Content-Type: application/json; charset=UTF-8");
include_once "koneksi.php";

$query = "SELECT 
            ps.id_pengajuan, 
            ps.username, 
            u.nama_lengkap AS nama_warga, 
            ps.jenis_surat, 
            ps.keterangan, 
            ps.keperluan,
            ps.tanggal_pengajuan,
            ps.status_rtrw,
            ps.status_admin,
            ps.status_akhir,
            ps.catatan_rtrw,
            ps.catatan_admin,
            ps.catatan_sekdes,
            (SELECT url_file FROM berkas_pengajuan WHERE id_pengajuan = ps.id_pengajuan AND nama_syarat = 'Foto KTP' LIMIT 1) AS file_ktp,
            (SELECT url_file FROM berkas_pengajuan WHERE id_pengajuan = ps.id_pengajuan AND nama_syarat = 'Kartu Keluarga (KK)' LIMIT 1) AS file_kk,
            (SELECT url_file FROM berkas_pengajuan WHERE id_pengajuan = ps.id_pengajuan AND nama_syarat = 'Berkas Utama' LIMIT 1) AS dokumen
          FROM pengajuan ps
          LEFT JOIN users u ON ps.username = u.username
          ORDER BY ps.id_pengajuan DESC";

$result = mysqli_query($koneksi, $query);

if ($result) {
    $array_data = array();
    
    while ($row = mysqli_fetch_assoc($result)) {
        // Logika status general mapping ke UI (AdminLihatPengajuanActivity expect: "Pending", "Disetujui RT/RW", "Ditolak RT/RW")
        $status = 'Pending';
        if ($row['status_akhir'] === 'Selesai' || $row['status_akhir'] === 'Siap Disahkan' || $row['status_akhir'] === 'Menunggu Sekdes') {
            $status = 'Selesai';
        } elseif (stripos($row['status_akhir'], 'tolak') !== false || stripos($row['status_admin'], 'tolak') !== false || stripos($row['status_rtrw'], 'tolak') !== false) {
            $status = 'Ditolak RT/RW'; // Untuk memuaskan dropdown filter admin yang menganggap semua penolakan itu 'Ditolak RT/RW'
        } elseif ($row['status_admin'] === 'Terverifikasi' || $row['status_rtrw'] === 'Disetujui' || $row['status_rtrw'] === 'Disetujui RT/RW') {
            $status = 'Disetujui RT/RW';
        }

        $catatan = $row['catatan_sekdes'] ?: ($row['catatan_admin'] ?: $row['catatan_rtrw']);

        $berkas = array();
        if (!empty($row['dokumen'])) {
            $berkas[] = array("nama_syarat" => "Berkas Lampiran", "url_file" => $row['dokumen']);
        }
        if (!empty($row['file_ktp'])) {
            $berkas[] = array("nama_syarat" => "Foto KTP", "url_file" => $row['file_ktp']);
        }
        if (!empty($row['file_kk'])) {
            $berkas[] = array("nama_syarat" => "Kartu Keluarga (KK)", "url_file" => $row['file_kk']);
        }

        $array_data[] = array(
            // Parameter untuk PengajuanSuratModel (Admin Proses)
            "id_pengajuan" => (int)$row['id_pengajuan'],
            "nama_warga" => $row['nama_warga'] ?: $row['username'],
            "tanggal_pengajuan" => $row['tanggal_pengajuan'],
            "catatan" => $catatan,
            "berkas_persyaratan" => $berkas,
            
            // Parameter untuk PengajuanSurat (Admin Lihat)
            "id" => (int)$row['id_pengajuan'],
            "username" => $row['username'],
            "jenis_surat" => $row['jenis_surat'],
            "keperluan" => $row['keperluan'],
            "dokumen" => $row['dokumen'],
            "file_ktp" => $row['file_ktp'],
            "file_kk" => $row['file_kk'],
            
            // Shared Parameter
            "status" => $status,
            "keterangan" => $row['keterangan']
        );
    }

    echo json_encode(array(
        "status" => "success",
        "message" => "Data pengajuan berhasil dimuat",
        "data" => $array_data
    ));
} else {
    echo json_encode(array(
        "status" => "error",
        "message" => "Gagal mengeksekusi data ke database: " . mysqli_error($koneksi),
        "data" => []
    ));
}

mysqli_close($koneksi);
?>
