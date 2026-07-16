-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jul 16, 2026 at 06:57 AM
-- Server version: 8.4.3
-- PHP Version: 8.3.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `desa_kotlin`
--

-- --------------------------------------------------------

--
-- Table structure for table `berita_desa`
--

CREATE TABLE `berita_desa` (
  `id_berita` int NOT NULL,
  `judul` varchar(300) NOT NULL,
  `isi_berita` text NOT NULL,
  `tanggal_post` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `berita_desa`
--

INSERT INTO `berita_desa` (`id_berita`, `judul`, `isi_berita`, `tanggal_post`) VALUES
(1, 'tes', 'p', '2026-07-14 15:49:02');

-- --------------------------------------------------------

--
-- Table structure for table `berkas_pengajuan`
--

CREATE TABLE `berkas_pengajuan` (
  `id_berkas` int NOT NULL,
  `id_pengajuan` int NOT NULL,
  `nama_syarat` varchar(200) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `url_file` varchar(500) DEFAULT NULL,
  `drive_file_id` varchar(255) DEFAULT NULL,
  `drive_url` text,
  `tanggal_upload` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `berkas_pengajuan`
--

INSERT INTO `berkas_pengajuan` (`id_berkas`, `id_pengajuan`, `nama_syarat`, `file_path`, `url_file`, `drive_file_id`, `drive_url`, `tanggal_upload`) VALUES
(1, 1, 'Foto KTP', 'uploads/1784047782_file_ktp_GoldenGym_anna_1770644341431.jpg', '1784047782_file_ktp_GoldenGym_anna_1770644341431.jpg', NULL, NULL, '2026-07-14 16:49:43'),
(2, 1, 'Kartu Keluarga (KK)', 'uploads/1784047783_file_kk_GoldenGym_anna_1770644341431.jpg', '1784047783_file_kk_GoldenGym_anna_1770644341431.jpg', NULL, NULL, '2026-07-14 16:49:43'),
(3, 2, 'Foto KTP', 'uploads/1784047954_file_ktp_GoldenGym_anna_1770644341431.jpg', '1784047954_file_ktp_GoldenGym_anna_1770644341431.jpg', NULL, NULL, '2026-07-14 16:52:34'),
(4, 2, 'Kartu Keluarga (KK)', 'uploads/1784047954_file_kk_GoldenGym_anna_1770644341431.jpg', '1784047954_file_kk_GoldenGym_anna_1770644341431.jpg', NULL, NULL, '2026-07-14 16:52:34'),
(5, 3, 'Foto KTP', 'uploads/1784051878_file_ktp_GoldenGym_anna_1770644341431.jpg', '1784051878_file_ktp_GoldenGym_anna_1770644341431.jpg', NULL, NULL, '2026-07-14 17:57:58'),
(6, 3, 'Kartu Keluarga (KK)', 'uploads/1784051882_file_kk_GoldenGym_anna_1770644341431.jpg', '1784051882_file_kk_GoldenGym_anna_1770644341431.jpg', NULL, NULL, '2026-07-14 17:58:02'),
(7, 4, 'Foto KTP', 'drive://1mommiA0c9tVe8MkqOv-jndwnYtQq4jlT', 'https://drive.google.com/file/d/1mommiA0c9tVe8MkqOv-jndwnYtQq4jlT/view', '1mommiA0c9tVe8MkqOv-jndwnYtQq4jlT', 'https://drive.google.com/file/d/1mommiA0c9tVe8MkqOv-jndwnYtQq4jlT/view', '2026-07-15 17:15:49'),
(8, 4, 'Kartu Keluarga (KK)', 'drive://10pERv7kJvKc3XuHy7oyBVmfVpuEJhvB0', 'https://drive.google.com/file/d/10pERv7kJvKc3XuHy7oyBVmfVpuEJhvB0/view', '10pERv7kJvKc3XuHy7oyBVmfVpuEJhvB0', 'https://drive.google.com/file/d/10pERv7kJvKc3XuHy7oyBVmfVpuEJhvB0/view', '2026-07-15 17:15:52');

-- --------------------------------------------------------

--
-- Table structure for table `laporan_users`
--

CREATE TABLE `laporan_users` (
  `id_laporan` int NOT NULL,
  `username` varchar(100) NOT NULL,
  `kategori` varchar(100) NOT NULL,
  `subjek` varchar(200) NOT NULL,
  `isi_laporan` text NOT NULL,
  `tanggal_kirim` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status_tindak_lanjut` varchar(50) NOT NULL DEFAULT 'Belum Ditindaklanjuti',
  `tanggapan_admin` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `laporan_users`
--

INSERT INTO `laporan_users` (`id_laporan`, `username`, `kategori`, `subjek`, `isi_laporan`, `tanggal_kirim`, `status_tindak_lanjut`, `tanggapan_admin`) VALUES
(1, 'rahma', 'Keluhan', 'dwdd', 'dwdwd', '2026-07-14 16:26:12', 'Belum Ditindaklanjuti', NULL),
(2, 'rahma', 'Aspirasi', 'swdd', 'dwd', '2026-07-14 18:00:12', 'Belum Ditindaklanjuti', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `layanan`
--

CREATE TABLE `layanan` (
  `id` int NOT NULL,
  `nama_surat` varchar(200) NOT NULL,
  `deskripsi` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `layanan`
--

INSERT INTO `layanan` (`id`, `nama_surat`, `deskripsi`) VALUES
(1, 'Surat Keterangan Domisili', 'Surat keterangan tempat tinggal users'),
(2, 'Surat Keterangan Tidak Mampu', 'Surat keterangan ekonomi kurang mampu'),
(3, 'Surat Keterangan Usaha', 'Surat keterangan usaha kecil menengah'),
(4, 'Surat Pengantar KTP', 'Surat pengantar pembuatan KTP baru'),
(5, 'Surat Keterangan Kelahiran', 'Surat keterangan kelahiran anak'),
(6, 'Surat Keterangan Kematian', 'Surat keterangan kematian users'),
(7, 'Surat Keterangan Pindah', 'Surat keterangan pindah domisili'),
(8, 'Pembuatan KIA', 'Kartu Identitas Anak (0-17 th)'),
(9, 'Permohonan KTP', 'Pembuatan KTP Baru/Hilang'),
(10, 'Permohonan KK', 'Pembuatan KK Baru/Perubahan');

-- --------------------------------------------------------

--
-- Table structure for table `notifikasi`
--

CREATE TABLE `notifikasi` (
  `id_notifikasi` int NOT NULL,
  `username_tujuan` varchar(100) NOT NULL,
  `judul` varchar(200) NOT NULL,
  `pesan` text NOT NULL,
  `id_pengajuan` int DEFAULT NULL,
  `sudah_dibaca` tinyint(1) NOT NULL DEFAULT '0',
  `tanggal` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pengajuan`
--

CREATE TABLE `pengajuan` (
  `id_pengajuan` int NOT NULL,
  `username` varchar(100) NOT NULL,
  `nama_pemohon` varchar(150) DEFAULT NULL,
  `nama_pengaju` varchar(150) DEFAULT NULL,
  `no_ktp` varchar(20) DEFAULT NULL,
  `nik_pemohon` varchar(16) DEFAULT NULL,
  `jenis_surat` varchar(200) NOT NULL,
  `keterangan` text,
  `keperluan` text,
  `tanggal_pengajuan` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status_rtrw` varchar(50) NOT NULL DEFAULT 'Menunggu' COMMENT 'Menunggu, Disetujui, Ditolak',
  `catatan_rtrw` text,
  `tanggal_rtrw` datetime DEFAULT NULL,
  `status_admin` varchar(50) NOT NULL DEFAULT 'Menunggu' COMMENT 'Menunggu, Diproses, Selesai',
  `catatan_admin` text,
  `tanggal_admin` datetime DEFAULT NULL,
  `status_akhir` varchar(50) NOT NULL DEFAULT 'Menunggu' COMMENT 'Menunggu, Diproses, Selesai, Ditolak Sekdes',
  `catatan_sekdes` text,
  `tanggal_sekdes` datetime DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'pending' COMMENT 'pending, diproses, disetujui, ditolak, selesai',
  `catatan` text,
  `dokumen_hasil` varchar(500) DEFAULT NULL,
  `dokumen_drive_url` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pengajuan`
--

INSERT INTO `pengajuan` (`id_pengajuan`, `username`, `nama_pemohon`, `nama_pengaju`, `no_ktp`, `nik_pemohon`, `jenis_surat`, `keterangan`, `keperluan`, `tanggal_pengajuan`, `status_rtrw`, `catatan_rtrw`, `tanggal_rtrw`, `status_admin`, `catatan_admin`, `tanggal_admin`, `status_akhir`, `catatan_sekdes`, `tanggal_sekdes`, `status`, `catatan`, `dokumen_hasil`, `dokumen_drive_url`) VALUES
(1, 'rahma', 'rahma', NULL, NULL, '1232321412412424', 'Pembuatan KIA', 'Pengajuan dari Aplikasi - Pembuatan KIA', 'Pembuatan KIA', '2026-07-14 16:49:42', 'Menunggu', NULL, NULL, 'Terverifikasi', 'oke', '2026-07-15 00:52:39', 'Menunggu Sekdes', NULL, NULL, 'pending', NULL, NULL, NULL),
(2, 'rahma', 'rahma', NULL, NULL, '1312312323454554', 'Permohonan KTP', 'Pengajuan dari Aplikasi - Permohonan KTP', 'Permohonan KTP', '2026-07-14 16:52:33', 'Menunggu', NULL, NULL, 'Terverifikasi', 'oke', '2026-07-15 00:52:31', 'Menunggu Sekdes', NULL, NULL, 'pending', NULL, NULL, NULL),
(3, 'rahma', 'ama', NULL, NULL, '4234354545323', 'Permohonan KTP', 'Pengajuan dari Aplikasi - Permohonan KTP', 'Permohonan KTP', '2026-07-14 17:57:51', 'Menunggu', NULL, NULL, 'Ditolak', 'e', '2026-07-15 00:59:32', 'Ditolak Admin', NULL, NULL, 'pending', NULL, NULL, NULL),
(4, 'rahma', 'pepek', NULL, NULL, '3214134214124412', 'Permohonan KTP', 'Pengajuan dari Aplikasi - Permohonan KTP', 'Permohonan KTP', '2026-07-15 17:15:43', 'Menunggu', NULL, NULL, 'Menunggu', NULL, NULL, 'Menunggu', NULL, NULL, 'pending', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `penilaian_layanan`
--

CREATE TABLE `penilaian_layanan` (
  `id_penilaian` int NOT NULL,
  `username` varchar(100) NOT NULL,
  `rating` tinyint(1) NOT NULL,
  `ulasan` text,
  `tanggal` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ;

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id_role` int NOT NULL,
  `nama_role` varchar(50) NOT NULL COMMENT 'masyarakat, rtrw, sekdes, kades, admin'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id_role`, `nama_role`) VALUES
(1, 'masyarakat'),
(2, 'rtrw'),
(3, 'sekdes'),
(4, 'kades'),
(5, 'admin');

-- --------------------------------------------------------

--
-- Table structure for table `syarat_surat`
--

CREATE TABLE `syarat_surat` (
  `id` int NOT NULL,
  `id_jenis_surat` int NOT NULL,
  `nama_syarat` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `syarat_surat`
--

INSERT INTO `syarat_surat` (`id`, `id_jenis_surat`, `nama_syarat`) VALUES
(1, 8, 'Foto KTP'),
(2, 8, 'Kartu Keluarga (KK)'),
(3, 9, 'Foto KTP'),
(4, 9, 'Kartu Keluarga (KK)'),
(5, 10, 'Foto KTP'),
(6, 10, 'Kartu Keluarga (KK)'),
(7, 8, 'Foto KTP'),
(8, 8, 'Kartu Keluarga (KK)'),
(9, 9, 'Foto KTP'),
(10, 9, 'Kartu Keluarga (KK)'),
(11, 10, 'Foto KTP'),
(12, 10, 'Kartu Keluarga (KK)');

-- --------------------------------------------------------

--
-- Table structure for table `template_surat`
--

CREATE TABLE `template_surat` (
  `id_template` int NOT NULL,
  `jenis_surat` varchar(200) NOT NULL,
  `kode_surat` varchar(50) DEFAULT NULL,
  `isi_pembuka` text,
  `isi_penutup` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `template_surat`
--

INSERT INTO `template_surat` (`id_template`, `jenis_surat`, `kode_surat`, `isi_pembuka`, `isi_penutup`) VALUES
(1, 'Pembuatan KIA', 'KIA/01/2026', 'Menerangkan bahwa anak dari pemohon berikut mengajukan pembuatan Kartu Identitas Anak (KIA).', 'Demikian surat pengantar ini dibuat untuk dipergunakan sebagaimana mestinya.'),
(2, 'Permohonan KTP', 'KTP/02/2026', 'Menerangkan bahwa warga tersebut di atas mengajukan permohonan pembuatan KTP/E-KTP.', 'Demikian surat pengantar ini dibuat untuk dipergunakan sebagaimana mestinya.'),
(3, 'Permohonan KK', 'KK/03/2026', 'Menerangkan bahwa warga tersebut di atas mengajukan permohonan Kartu Keluarga (KK) baru/perubahan.', 'Demikian surat pengantar ini dibuat untuk dipergunakan sebagaimana mestinya.');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id_user` int NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama_lengkap` varchar(150) NOT NULL,
  `nik` varchar(16) DEFAULT NULL,
  `role` varchar(50) NOT NULL DEFAULT 'masyarakat' COMMENT 'masyarakat, rtrw, sekdes, kades, admin',
  `status_akun` enum('aktif','nonaktif','menunggu') NOT NULL DEFAULT 'aktif',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id_user`, `username`, `password`, `nama_lengkap`, `nik`, `role`, `status_akun`, `created_at`) VALUES
(1, 'kades_banggle', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Kepala Desa Banggle', NULL, 'kades', 'aktif', '2026-07-14 14:18:29'),
(2, 'sekdes_banggle', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Sekretaris Desa Banggle', NULL, 'sekdes', 'aktif', '2026-07-14 14:18:29'),
(3, 'rtrw001', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Ketua RT/RW 001', NULL, 'rtrw', 'aktif', '2026-07-14 14:18:29'),
(4, 'admin', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Administrator Desa Banggle', NULL, 'admin', 'aktif', '2026-07-14 14:18:29'),
(5, 'rendy', '$2y$10$WvZuA7Vz.sqTxAgDzxzmQ.kL/zEXT66r9V./YY4m6QiLddhc6tHMK', 'rendy', '1234567891234567', 'masyarakat', 'aktif', '2026-07-14 16:23:54'),
(6, 'rahma', '$2y$10$EoQF/p5eTyhvk2fHPuUpAu8b6NASKXJFDzeuVDD7VWDp9t4vkXB4u', 'rahma', '1234567897643354', 'masyarakat', 'aktif', '2026-07-14 16:24:24');

-- --------------------------------------------------------

--
-- Table structure for table `verifikasi`
--

CREATE TABLE `verifikasi` (
  `id_verifikasi` int NOT NULL,
  `id_pengajuan` int NOT NULL,
  `username_verifikator` varchar(100) NOT NULL,
  `role_verifikator` varchar(50) NOT NULL,
  `status_verifikasi` varchar(50) NOT NULL,
  `catatan` text,
  `tanggal_verifikasi` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `berita_desa`
--
ALTER TABLE `berita_desa`
  ADD PRIMARY KEY (`id_berita`);

--
-- Indexes for table `berkas_pengajuan`
--
ALTER TABLE `berkas_pengajuan`
  ADD PRIMARY KEY (`id_berkas`),
  ADD KEY `id_pengajuan` (`id_pengajuan`);

--
-- Indexes for table `laporan_users`
--
ALTER TABLE `laporan_users`
  ADD PRIMARY KEY (`id_laporan`);

--
-- Indexes for table `layanan`
--
ALTER TABLE `layanan`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `notifikasi`
--
ALTER TABLE `notifikasi`
  ADD PRIMARY KEY (`id_notifikasi`);

--
-- Indexes for table `pengajuan`
--
ALTER TABLE `pengajuan`
  ADD PRIMARY KEY (`id_pengajuan`);

--
-- Indexes for table `penilaian_layanan`
--
ALTER TABLE `penilaian_layanan`
  ADD PRIMARY KEY (`id_penilaian`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id_role`);

--
-- Indexes for table `syarat_surat`
--
ALTER TABLE `syarat_surat`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_jenis_surat` (`id_jenis_surat`);

--
-- Indexes for table `template_surat`
--
ALTER TABLE `template_surat`
  ADD PRIMARY KEY (`id_template`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `nik` (`nik`);

--
-- Indexes for table `verifikasi`
--
ALTER TABLE `verifikasi`
  ADD PRIMARY KEY (`id_verifikasi`),
  ADD KEY `id_pengajuan` (`id_pengajuan`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `berita_desa`
--
ALTER TABLE `berita_desa`
  MODIFY `id_berita` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `berkas_pengajuan`
--
ALTER TABLE `berkas_pengajuan`
  MODIFY `id_berkas` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `laporan_users`
--
ALTER TABLE `laporan_users`
  MODIFY `id_laporan` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `layanan`
--
ALTER TABLE `layanan`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `notifikasi`
--
ALTER TABLE `notifikasi`
  MODIFY `id_notifikasi` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pengajuan`
--
ALTER TABLE `pengajuan`
  MODIFY `id_pengajuan` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `penilaian_layanan`
--
ALTER TABLE `penilaian_layanan`
  MODIFY `id_penilaian` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id_role` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `syarat_surat`
--
ALTER TABLE `syarat_surat`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `template_surat`
--
ALTER TABLE `template_surat`
  MODIFY `id_template` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id_user` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `verifikasi`
--
ALTER TABLE `verifikasi`
  MODIFY `id_verifikasi` int NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `berkas_pengajuan`
--
ALTER TABLE `berkas_pengajuan`
  ADD CONSTRAINT `berkas_pengajuan_ibfk_1` FOREIGN KEY (`id_pengajuan`) REFERENCES `pengajuan` (`id_pengajuan`) ON DELETE CASCADE;

--
-- Constraints for table `syarat_surat`
--
ALTER TABLE `syarat_surat`
  ADD CONSTRAINT `syarat_surat_ibfk_1` FOREIGN KEY (`id_jenis_surat`) REFERENCES `layanan` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `verifikasi`
--
ALTER TABLE `verifikasi`
  ADD CONSTRAINT `verifikasi_ibfk_1` FOREIGN KEY (`id_pengajuan`) REFERENCES `pengajuan` (`id_pengajuan`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
