<?php
error_reporting(E_ALL); ini_set('display_errors',1);
include 'koneksi.php';
$log = [];
$tables = [
"roles" => "CREATE TABLE IF NOT EXISTS `roles` (`id_role` INT NOT NULL AUTO_INCREMENT, `nama_role` VARCHAR(50) NOT NULL, PRIMARY KEY (`id_role`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"users" => "CREATE TABLE IF NOT EXISTS `users` (`id_user` INT NOT NULL AUTO_INCREMENT, `username` VARCHAR(100) NOT NULL UNIQUE, `password` VARCHAR(255) NOT NULL, `nama_lengkap` VARCHAR(150) NOT NULL, `nik` VARCHAR(16) UNIQUE DEFAULT NULL, `role` VARCHAR(50) NOT NULL DEFAULT 'masyarakat', `status_akun` ENUM('aktif','nonaktif','menunggu') NOT NULL DEFAULT 'aktif', `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id_user`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"layanan" => "CREATE TABLE IF NOT EXISTS `layanan` (`id` INT NOT NULL AUTO_INCREMENT, `nama_surat` VARCHAR(200) NOT NULL, `deskripsi` TEXT DEFAULT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"syarat_surat" => "CREATE TABLE IF NOT EXISTS `syarat_surat` (`id` INT NOT NULL AUTO_INCREMENT, `id_jenis_surat` INT NOT NULL, `nama_syarat` VARCHAR(200) NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"pengajuan" => "CREATE TABLE IF NOT EXISTS `pengajuan` (`id_pengajuan` INT NOT NULL AUTO_INCREMENT, `username` VARCHAR(100) NOT NULL, `nama_pemohon` VARCHAR(150) DEFAULT NULL, `nik_pemohon` VARCHAR(16) DEFAULT NULL, `jenis_surat` VARCHAR(200) NOT NULL, `keterangan` TEXT DEFAULT NULL, `keperluan` TEXT DEFAULT NULL, `tanggal_pengajuan` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `status_rtrw` VARCHAR(50) NOT NULL DEFAULT 'Menunggu', `catatan_rtrw` TEXT DEFAULT NULL, `tanggal_rtrw` DATETIME DEFAULT NULL, `status_admin` VARCHAR(50) NOT NULL DEFAULT 'Menunggu', `catatan_admin` TEXT DEFAULT NULL, `tanggal_admin` DATETIME DEFAULT NULL, `status_akhir` VARCHAR(50) NOT NULL DEFAULT 'Menunggu', `catatan_sekdes` TEXT DEFAULT NULL, `tanggal_sekdes` DATETIME DEFAULT NULL, `status` VARCHAR(50) NOT NULL DEFAULT 'pending', `catatan` TEXT DEFAULT NULL, `dokumen_hasil` VARCHAR(500) DEFAULT NULL, `file_ktp` VARCHAR(500) DEFAULT NULL, `file_kk` VARCHAR(500) DEFAULT NULL, PRIMARY KEY (`id_pengajuan`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"berkas_pengajuan" => "CREATE TABLE IF NOT EXISTS `berkas_pengajuan` (`id_berkas` INT NOT NULL AUTO_INCREMENT, `id_pengajuan` INT NOT NULL, `nama_syarat` VARCHAR(200) NOT NULL, `file_path` VARCHAR(500) NOT NULL, `url_file` VARCHAR(500) DEFAULT NULL, `tanggal_upload` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id_berkas`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"verifikasi" => "CREATE TABLE IF NOT EXISTS `verifikasi` (`id_verifikasi` INT NOT NULL AUTO_INCREMENT, `id_pengajuan` INT NOT NULL, `username_verifikator` VARCHAR(100) NOT NULL, `role_verifikator` VARCHAR(50) NOT NULL, `status_verifikasi` VARCHAR(50) NOT NULL, `catatan` TEXT DEFAULT NULL, `tanggal_verifikasi` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id_verifikasi`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"laporan_users" => "CREATE TABLE IF NOT EXISTS `laporan_users` (`id_laporan` INT NOT NULL AUTO_INCREMENT, `username` VARCHAR(100) NOT NULL, `kategori` VARCHAR(100) NOT NULL, `subjek` VARCHAR(200) NOT NULL, `isi_laporan` TEXT NOT NULL, `tanggal_kirim` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `status_tindak_lanjut` VARCHAR(50) NOT NULL DEFAULT 'Pending', `tanggapan_admin` TEXT DEFAULT NULL, PRIMARY KEY (`id_laporan`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"berita_desa" => "CREATE TABLE IF NOT EXISTS `berita_desa` (`id_berita` INT NOT NULL AUTO_INCREMENT, `judul` VARCHAR(300) NOT NULL, `isi_berita` TEXT NOT NULL, `tanggal_post` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id_berita`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"penilaian_layanan" => "CREATE TABLE IF NOT EXISTS `penilaian_layanan` (`id_penilaian` INT NOT NULL AUTO_INCREMENT, `username` VARCHAR(100) NOT NULL, `rating` TINYINT(1) NOT NULL, `ulasan` TEXT DEFAULT NULL, `tanggal` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id_penilaian`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"notifikasi" => "CREATE TABLE IF NOT EXISTS `notifikasi` (`id_notifikasi` INT NOT NULL AUTO_INCREMENT, `username_tujuan` VARCHAR(100) NOT NULL, `judul` VARCHAR(200) NOT NULL, `pesan` TEXT NOT NULL, `id_pengajuan` INT DEFAULT NULL, `sudah_dibaca` TINYINT(1) NOT NULL DEFAULT 0, `tanggal` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id_notifikasi`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
"template_surat" => "CREATE TABLE IF NOT EXISTS `template_surat` (`id_template` INT NOT NULL AUTO_INCREMENT, `jenis_surat` VARCHAR(200) NOT NULL, `kode_surat` VARCHAR(50) DEFAULT NULL, `isi_pembuka` TEXT DEFAULT NULL, `isi_penutup` TEXT DEFAULT NULL, PRIMARY KEY (`id_template`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
];
foreach($tables as $name => $sql) {
  if(mysqli_query($koneksi, $sql)) $log[] = "OK: $name";
  else $log[] = "FAIL $name: ".mysqli_error($koneksi);
}
// Seed data
$seeds = [
"INSERT IGNORE INTO `roles` VALUES (1,'masyarakat'),(2,'rtrw'),(3,'sekdes'),(4,'kades'),(5,'admin')",
"INSERT IGNORE INTO `users` (username,password,nama_lengkap,nik,role,status_akun) VALUES ('admin','\$2y\$10\$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq','Administrator Desa Banggle',NULL,'admin','aktif'),('kades_banggle','\$2y\$10\$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq','Kepala Desa Banggle',NULL,'kades','aktif'),('sekdes_banggle','\$2y\$10\$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq','Sekretaris Desa Banggle',NULL,'sekdes','aktif'),('rtrw001','\$2y\$10\$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq','Ketua RT/RW 001',NULL,'rtrw','aktif')",
"INSERT IGNORE INTO `layanan` (nama_surat,deskripsi) VALUES ('Surat Keterangan Domisili','Surat keterangan tempat tinggal'),('Surat Keterangan Tidak Mampu','Surat keterangan ekonomi'),('Surat Keterangan Usaha','Surat keterangan usaha'),('Surat Pengantar KTP','Surat pengantar KTP'),('Surat Keterangan Kelahiran','Surat keterangan kelahiran'),('Surat Keterangan Kematian','Surat keterangan kematian'),('Surat Keterangan Pindah','Surat keterangan pindah')"
];
foreach($seeds as $sql) {
  if(mysqli_query($koneksi,$sql)) $log[] = "SEED OK";
  else $log[] = "SEED FAIL: ".mysqli_error($koneksi);
}
echo json_encode(["status"=>"success","log"=>$log],JSON_PRETTY_PRINT|JSON_UNESCAPED_UNICODE);
?>
