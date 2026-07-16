-- ============================================================
-- DATABASE: db_desa
-- Sistem Informasi Pelayanan Masyarakat Desa Banggle
-- Generated from PHP query analysis
-- ============================================================

CREATE DATABASE IF NOT EXISTS `db_desa` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `db_desa`;

-- ============================================================
-- 1. TABEL: roles
-- ============================================================
CREATE TABLE IF NOT EXISTS `roles` (
    `id_role` INT(11) NOT NULL AUTO_INCREMENT,
    `nama_role` VARCHAR(50) NOT NULL COMMENT 'masyarakat, rtrw, sekdes, kades, admin',
    PRIMARY KEY (`id_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO `roles` (`id_role`, `nama_role`) VALUES
(1, 'masyarakat'),
(2, 'rtrw'),
(3, 'sekdes'),
(4, 'kades'),
(5, 'admin');

-- ============================================================
-- 2. TABEL: users (Menyatukan Masyarakat, Perangkat Desa, Admin)
-- ============================================================
CREATE TABLE IF NOT EXISTS `users` (
    `id_user` INT(11) NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `nama_lengkap` VARCHAR(150) NOT NULL,
    `nik` VARCHAR(16) UNIQUE DEFAULT NULL,
    `role` VARCHAR(50) NOT NULL DEFAULT 'masyarakat' COMMENT 'masyarakat, rtrw, sekdes, kades, admin',
    `status_akun` ENUM('aktif','nonaktif','menunggu') NOT NULL DEFAULT 'aktif',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data contoh perangkat desa dan admin (password: admin123 di-hash PHP password_hash)
INSERT IGNORE INTO `users` (`username`, `password`, `nama_lengkap`, `nik`, `role`) VALUES
('kades_banggle', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Kepala Desa Banggle', NULL, 'kades'),
('sekdes_banggle', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Sekretaris Desa Banggle', NULL, 'sekdes'),
('rtrw001', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Ketua RT/RW 001', NULL, 'rtrw'),
('admin', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Administrator Desa Banggle', NULL, 'admin');

-- ============================================================
-- 5. TABEL: layanan (Jenis Surat / Layanan yang tersedia)
-- ============================================================
CREATE TABLE IF NOT EXISTS `layanan` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `nama_surat` VARCHAR(200) NOT NULL,
    `deskripsi` TEXT DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO `layanan` (`nama_surat`, `deskripsi`) VALUES
('Surat Keterangan Domisili', 'Surat keterangan tempat tinggal users'),
('Surat Keterangan Tidak Mampu', 'Surat keterangan ekonomi kurang mampu'),
('Surat Keterangan Usaha', 'Surat keterangan usaha kecil menengah'),
('Surat Pengantar KTP', 'Surat pengantar pembuatan KTP baru'),
('Surat Keterangan Kelahiran', 'Surat keterangan kelahiran anak'),
('Surat Keterangan Kematian', 'Surat keterangan kematian users'),
('Surat Keterangan Pindah', 'Surat keterangan pindah domisili');

-- ============================================================
-- 6. TABEL: syarat_surat (Syarat Berkas per Jenis Surat)
-- ============================================================
CREATE TABLE IF NOT EXISTS `syarat_surat` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `id_jenis_surat` INT(11) NOT NULL,
    `nama_syarat` VARCHAR(200) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`id_jenis_surat`) REFERENCES `layanan`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. TABEL: pengajuan (Pengajuan Layanan dari users)
-- ============================================================
CREATE TABLE IF NOT EXISTS `pengajuan` (
    `id_pengajuan` INT(11) NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(100) NOT NULL,
    `nama_pemohon` VARCHAR(150) DEFAULT NULL,
    `nik_pemohon` VARCHAR(16) DEFAULT NULL,
    `jenis_surat` VARCHAR(200) NOT NULL,
    `keterangan` TEXT DEFAULT NULL,
    `keperluan` TEXT DEFAULT NULL,
    `tanggal_pengajuan` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Status verifikasi RT/RW
    `status_rtrw` VARCHAR(50) NOT NULL DEFAULT 'Menunggu' COMMENT 'Menunggu, Disetujui, Ditolak',
    `catatan_rtrw` TEXT DEFAULT NULL,
    `tanggal_rtrw` DATETIME DEFAULT NULL,
    -- Status verifikasi Admin
    `status_admin` VARCHAR(50) NOT NULL DEFAULT 'Menunggu' COMMENT 'Menunggu, Diproses, Selesai',
    `catatan_admin` TEXT DEFAULT NULL,
    `tanggal_admin` DATETIME DEFAULT NULL,
    -- Status akhir dari Sekdes/Kades
    `status_akhir` VARCHAR(50) NOT NULL DEFAULT 'Menunggu' COMMENT 'Menunggu, Diproses, Selesai, Ditolak Sekdes',
    `catatan_sekdes` TEXT DEFAULT NULL,
    `tanggal_sekdes` DATETIME DEFAULT NULL,
    -- Status keseluruhan untuk cek status users
    `status` VARCHAR(50) NOT NULL DEFAULT 'pending' COMMENT 'pending, diproses, disetujui, ditolak, selesai',
    `catatan` TEXT DEFAULT NULL,
    `dokumen_hasil` VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (`id_pengajuan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 8. TABEL: berkas_pengajuan (File Upload Persyaratan)
-- ============================================================
CREATE TABLE IF NOT EXISTS `berkas_pengajuan` (
    `id_berkas` INT(11) NOT NULL AUTO_INCREMENT,
    `id_pengajuan` INT(11) NOT NULL,
    `nama_syarat` VARCHAR(200) NOT NULL,
    `file_path` VARCHAR(500) NOT NULL,
    `url_file` VARCHAR(500) DEFAULT NULL,
    `tanggal_upload` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_berkas`),
    FOREIGN KEY (`id_pengajuan`) REFERENCES `pengajuan`(`id_pengajuan`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 9. TABEL: verifikasi (Log Riwayat Verifikasi)
-- ============================================================
CREATE TABLE IF NOT EXISTS `verifikasi` (
    `id_verifikasi` INT(11) NOT NULL AUTO_INCREMENT,
    `id_pengajuan` INT(11) NOT NULL,
    `username_verifikator` VARCHAR(100) NOT NULL,
    `role_verifikator` VARCHAR(50) NOT NULL,
    `status_verifikasi` VARCHAR(50) NOT NULL,
    `catatan` TEXT DEFAULT NULL,
    `tanggal_verifikasi` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_verifikasi`),
    FOREIGN KEY (`id_pengajuan`) REFERENCES `pengajuan`(`id_pengajuan`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 10. TABEL: notifikasi
-- ============================================================
CREATE TABLE IF NOT EXISTS `notifikasi` (
    `id_notifikasi` INT(11) NOT NULL AUTO_INCREMENT,
    `username_tujuan` VARCHAR(100) NOT NULL,
    `judul` VARCHAR(200) NOT NULL,
    `pesan` TEXT NOT NULL,
    `id_pengajuan` INT(11) DEFAULT NULL,
    `sudah_dibaca` TINYINT(1) NOT NULL DEFAULT 0,
    `tanggal` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_notifikasi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 11. TABEL: laporan_users (Keluhan & Aspirasi)
-- ============================================================
CREATE TABLE IF NOT EXISTS `laporan_users` (
    `id_laporan` INT(11) NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(100) NOT NULL,
    `kategori` VARCHAR(100) NOT NULL,
    `subjek` VARCHAR(200) NOT NULL,
    `isi_laporan` TEXT NOT NULL,
    `tanggal_kirim` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `status_tindak_lanjut` VARCHAR(50) NOT NULL DEFAULT 'Belum Ditindaklanjuti',
    `tanggapan_admin` TEXT DEFAULT NULL,
    PRIMARY KEY (`id_laporan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 12. TABEL: berita_desa
-- ============================================================
CREATE TABLE IF NOT EXISTS `berita_desa` (
    `id_berita` INT(11) NOT NULL AUTO_INCREMENT,
    `judul` VARCHAR(300) NOT NULL,
    `isi_berita` TEXT NOT NULL,
    `tanggal_post` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_berita`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 13. TABEL: penilaian_layanan
-- ============================================================
CREATE TABLE IF NOT EXISTS `penilaian_layanan` (
    `id_penilaian` INT(11) NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(100) NOT NULL,
    `rating` TINYINT(1) NOT NULL CHECK (`rating` BETWEEN 1 AND 5),
    `ulasan` TEXT DEFAULT NULL,
    `tanggal` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_penilaian`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 14. TABEL: template_surat
-- ============================================================
CREATE TABLE IF NOT EXISTS `template_surat` (
    `id_template` INT(11) NOT NULL AUTO_INCREMENT,
    `jenis_surat` VARCHAR(200) NOT NULL,
    `kode_surat` VARCHAR(50) DEFAULT NULL,
    `isi_pembuka` TEXT DEFAULT NULL,
    `isi_penutup` TEXT DEFAULT NULL,
    PRIMARY KEY (`id_template`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
