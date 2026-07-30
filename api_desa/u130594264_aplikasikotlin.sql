-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Waktu pembuatan: 30 Jul 2026 pada 02.15
-- Versi server: 11.8.8-MariaDB-log
-- Versi PHP: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `u130594264_aplikasikotlin`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `berita_desa`
--

CREATE TABLE `berita_desa` (
  `id_berita` int(11) NOT NULL,
  `judul` varchar(300) NOT NULL,
  `isi_berita` text NOT NULL,
  `tanggal_post` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `berita_desa`
--

INSERT INTO `berita_desa` (`id_berita`, `judul`, `isi_berita`, `tanggal_post`) VALUES
(1, 'tes', 'p', '2026-07-14 15:49:02');

-- --------------------------------------------------------

--
-- Struktur dari tabel `berkas_pengajuan`
--

CREATE TABLE `berkas_pengajuan` (
  `id_berkas` int(11) NOT NULL,
  `id_pengajuan` int(11) NOT NULL,
  `nama_syarat` varchar(200) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `url_file` varchar(500) DEFAULT NULL,
  `drive_file_id` varchar(255) DEFAULT NULL,
  `drive_url` text DEFAULT NULL,
  `tanggal_upload` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `berkas_pengajuan`
--

INSERT INTO `berkas_pengajuan` (`id_berkas`, `id_pengajuan`, `nama_syarat`, `file_path`, `url_file`, `drive_file_id`, `drive_url`, `tanggal_upload`) VALUES
(25, 22, 'Foto KTP', 'uploads/1785377284_file_ktp_IMG-20260728-WA0029.jpg', '1785377284_file_ktp_IMG-20260728-WA0029.jpg', NULL, NULL, '2026-07-30 02:08:04'),
(26, 22, 'Kartu Keluarga (KK)', 'uploads/1785377284_file_kk_IMG-20260728-WA0029.jpg', '1785377284_file_kk_IMG-20260728-WA0029.jpg', NULL, NULL, '2026-07-30 02:08:04'),
(27, 23, 'Foto KTP', 'uploads/1785377546_file_ktp_IMG-20260728-WA0029.jpg', '1785377546_file_ktp_IMG-20260728-WA0029.jpg', NULL, NULL, '2026-07-30 02:12:26'),
(28, 23, 'Kartu Keluarga (KK)', 'uploads/1785377546_file_kk_IMG-20260728-WA0029.jpg', '1785377546_file_kk_IMG-20260728-WA0029.jpg', NULL, NULL, '2026-07-30 02:12:26');

-- --------------------------------------------------------

--
-- Struktur dari tabel `laporan_users`
--

CREATE TABLE `laporan_users` (
  `id_laporan` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `kategori` varchar(100) NOT NULL,
  `subjek` varchar(200) NOT NULL,
  `isi_laporan` text NOT NULL,
  `tanggal_kirim` timestamp NULL DEFAULT current_timestamp(),
  `status_tindak_lanjut` varchar(50) NOT NULL DEFAULT 'Belum Ditindaklanjuti',
  `tanggapan_admin` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `laporan_users`
--

INSERT INTO `laporan_users` (`id_laporan`, `username`, `kategori`, `subjek`, `isi_laporan`, `tanggal_kirim`, `status_tindak_lanjut`, `tanggapan_admin`) VALUES
(1, 'rahma', 'Keluhan', 'dwdd', 'dwdwd', '2026-07-14 16:26:12', 'Belum Ditindaklanjuti', NULL),
(2, 'rahma', 'Aspirasi', 'swdd', 'dwd', '2026-07-14 18:00:12', 'Belum Ditindaklanjuti', NULL),
(3, 'lulu', 'Keluhan', 'fg', 'fu', '2026-07-28 16:28:31', 'Selesai', 'selesai');

-- --------------------------------------------------------

--
-- Struktur dari tabel `layanan`
--

CREATE TABLE `layanan` (
  `id` int(11) NOT NULL,
  `nama_surat` varchar(200) NOT NULL,
  `deskripsi` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `layanan`
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
-- Struktur dari tabel `notifikasi`
--

CREATE TABLE `notifikasi` (
  `id_notifikasi` int(11) NOT NULL,
  `username_tujuan` varchar(100) NOT NULL,
  `judul` varchar(200) NOT NULL,
  `pesan` text NOT NULL,
  `id_pengajuan` int(11) DEFAULT NULL,
  `sudah_dibaca` tinyint(1) NOT NULL DEFAULT 0,
  `tanggal` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `pengajuan`
--

CREATE TABLE `pengajuan` (
  `id_pengajuan` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `nama_pemohon` varchar(150) DEFAULT NULL,
  `nama_pengaju` varchar(150) DEFAULT NULL,
  `no_ktp` varchar(20) DEFAULT NULL,
  `nik_pemohon` varchar(16) DEFAULT NULL,
  `jenis_surat` varchar(200) NOT NULL,
  `keterangan` text DEFAULT NULL,
  `keperluan` text DEFAULT NULL,
  `tanggal_pengajuan` timestamp NULL DEFAULT current_timestamp(),
  `status_rtrw` varchar(50) NOT NULL DEFAULT 'Menunggu' COMMENT 'Menunggu, Disetujui, Ditolak',
  `catatan_rtrw` text DEFAULT NULL,
  `tanggal_rtrw` datetime DEFAULT NULL,
  `status_admin` varchar(50) NOT NULL DEFAULT 'Menunggu' COMMENT 'Menunggu, Diproses, Selesai',
  `catatan_admin` text DEFAULT NULL,
  `tanggal_admin` datetime DEFAULT NULL,
  `status_akhir` varchar(50) NOT NULL DEFAULT 'Menunggu' COMMENT 'Menunggu, Diproses, Selesai, Ditolak Sekdes',
  `catatan_sekdes` text DEFAULT NULL,
  `tanggal_sekdes` datetime DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'pending' COMMENT 'pending, diproses, disetujui, ditolak, selesai',
  `catatan` text DEFAULT NULL,
  `dokumen_hasil` varchar(500) DEFAULT NULL,
  `dokumen_drive_url` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `pengajuan`
--

INSERT INTO `pengajuan` (`id_pengajuan`, `username`, `nama_pemohon`, `nama_pengaju`, `no_ktp`, `nik_pemohon`, `jenis_surat`, `keterangan`, `keperluan`, `tanggal_pengajuan`, `status_rtrw`, `catatan_rtrw`, `tanggal_rtrw`, `status_admin`, `catatan_admin`, `tanggal_admin`, `status_akhir`, `catatan_sekdes`, `tanggal_sekdes`, `status`, `catatan`, `dokumen_hasil`, `dokumen_drive_url`) VALUES
(22, 'Lisa', 'Nana', NULL, NULL, '1646467994949499', 'Permohonan KK', 'Pengajuan dari Aplikasi - Permohonan KK', 'Permohonan KK', '2026-07-30 02:08:04', 'Menunggu', NULL, NULL, 'Menunggu', NULL, NULL, 'Menunggu', NULL, NULL, 'pending', NULL, NULL, NULL),
(23, 'lisa', 'Sjjsjssj', NULL, NULL, '59564694949494', 'Pembuatan KIA', 'Pengajuan dari Aplikasi - Pembuatan KIA', 'Pembuatan KIA', '2026-07-30 02:12:26', 'Menunggu', NULL, NULL, 'Menunggu', NULL, NULL, 'Menunggu', NULL, NULL, 'pending', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `penilaian_layanan`
--

CREATE TABLE `penilaian_layanan` (
  `id_penilaian` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `rating` tinyint(1) NOT NULL,
  `ulasan` text DEFAULT NULL,
  `tanggal` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `roles`
--

CREATE TABLE `roles` (
  `id_role` int(11) NOT NULL,
  `nama_role` varchar(50) NOT NULL COMMENT 'masyarakat, rtrw, sekdes, kades, admin'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `roles`
--

INSERT INTO `roles` (`id_role`, `nama_role`) VALUES
(1, 'masyarakat'),
(2, 'rtrw'),
(3, 'sekdes'),
(4, 'kades'),
(5, 'admin');

-- --------------------------------------------------------

--
-- Struktur dari tabel `syarat_surat`
--

CREATE TABLE `syarat_surat` (
  `id` int(11) NOT NULL,
  `id_jenis_surat` int(11) NOT NULL,
  `nama_syarat` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `syarat_surat`
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
-- Struktur dari tabel `template_surat`
--

CREATE TABLE `template_surat` (
  `id_template` int(11) NOT NULL,
  `jenis_surat` varchar(200) NOT NULL,
  `kode_surat` varchar(50) DEFAULT NULL,
  `isi_pembuka` text DEFAULT NULL,
  `isi_penutup` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `template_surat`
--

INSERT INTO `template_surat` (`id_template`, `jenis_surat`, `kode_surat`, `isi_pembuka`, `isi_penutup`) VALUES
(1, 'Pembuatan KIA', 'KIA/01/2026', 'Menerangkan bahwa anak dari pemohon berikut mengajukan pembuatan Kartu Identitas Anak (KIA).', 'Demikian surat pengantar ini dibuat untuk dipergunakan sebagaimana mestinya.'),
(2, 'Permohonan KTP', 'KTP/02/2026', 'Menerangkan bahwa warga tersebut di atas mengajukan permohonan pembuatan KTP/E-KTP.', 'Demikian surat pengantar ini dibuat untuk dipergunakan sebagaimana mestinya.'),
(3, 'Permohonan KK', 'KK/03/2026', 'Menerangkan bahwa warga tersebut di atas mengajukan permohonan Kartu Keluarga (KK) baru/perubahan.', 'Demikian surat pengantar ini dibuat untuk dipergunakan sebagaimana mestinya.');

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id_user` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama_lengkap` varchar(150) NOT NULL,
  `nik` varchar(16) DEFAULT NULL,
  `role` varchar(50) NOT NULL DEFAULT 'masyarakat' COMMENT 'masyarakat, rtrw, sekdes, kades, admin',
  `status_akun` enum('aktif','nonaktif','menunggu') NOT NULL DEFAULT 'aktif',
  `created_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id_user`, `username`, `password`, `nama_lengkap`, `nik`, `role`, `status_akun`, `created_at`) VALUES
(1, 'kades_banggle', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Kepala Desa Banggle', NULL, 'kades', 'aktif', '2026-07-14 14:18:29'),
(2, 'sekdes_banggle', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Sekretaris Desa Banggle', NULL, 'sekdes', 'aktif', '2026-07-14 14:18:29'),
(3, 'rtrw001', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Ketua RT/RW 001', NULL, 'rtrw', 'aktif', '2026-07-14 14:18:29'),
(4, 'admin', '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq', 'Administrator Desa Banggle', NULL, 'admin', 'aktif', '2026-07-14 14:18:29'),
(5, 'rendy', '$2y$10$WvZuA7Vz.sqTxAgDzxzmQ.kL/zEXT66r9V./YY4m6QiLddhc6tHMK', 'rendy', '1234567891234567', 'masyarakat', 'aktif', '2026-07-14 16:23:54'),
(6, 'rahma', '$2y$10$EoQF/p5eTyhvk2fHPuUpAu8b6NASKXJFDzeuVDD7VWDp9t4vkXB4u', 'rahma', '1234567897643354', 'masyarakat', 'aktif', '2026-07-14 16:24:24'),
(7, 'Rendy2006', '$2y$10$vuXhmUUMQ3f3Mt6D5OKDWO3yTTVjj6hlgLLkX.Delq5WggVSkXbYG', 'Rendy Krisna', '2432444434324234', 'masyarakat', '', '2026-07-26 10:00:15'),
(8, 'pukimak123', '$2y$10$1AuDxBff2c6eefUzsycWDOcJKezQdaDUBxzyh0RoGQ6FWdtXPPsSq', 'puki', '1234345325435355', 'masyarakat', '', '2026-07-26 10:06:14'),
(9, 'Lisa', '$2y$10$ubyN5dQq3AtwMTkhkcMceO3UQ2M.1BKxR2JfAvOG8XaJwdGgx4lr6', 'Lisa', '2321321331232133', 'masyarakat', 'aktif', '2026-07-28 12:50:08'),
(10, 'Pukimak1234', '$2y$10$s3eN2bjlgYpuBDBtwZ8nYO48ZrC4cRbDaiL7TC2OUKVP3EOn29zMq', 'Pukimak1234', '5324543255453453', 'masyarakat', '', '2026-07-28 12:54:13'),
(11, 'lili', '$2y$10$m.hgBIIWyRLWNd13Rh6w7.s8x4uUb15G1B64pUCjyYnpDC5qpnAIK', 'lili', '5181316494043434', 'masyarakat', 'aktif', '2026-07-28 13:31:05'),
(12, 'lala', '$2y$10$1fQNF7ec7eETag2qeBZfgOxUeT0ZW0lkfgOBO4k8hXl8bvRhlZDyG', 'Lala', '1234546787931248', 'masyarakat', 'aktif', '2026-07-28 15:32:43'),
(13, 'lulu', '$2y$10$A18QwEBj87w1DOKNs79QiOHwe4JbIR1QqV3mGEeZPOcOucuCwnuw6', 'Lulu', '1213546784343498', 'masyarakat', 'aktif', '2026-07-28 16:02:57'),
(14, 'nini', '$2y$10$aq8OpPGxq757GrSF7fJ50eTPq5P2b0cS00P/pqOf7sHyNHSxFU67m', 'nini', '1234567898765432', 'masyarakat', 'aktif', '2026-07-29 11:44:36');

-- --------------------------------------------------------

--
-- Struktur dari tabel `verifikasi`
--

CREATE TABLE `verifikasi` (
  `id_verifikasi` int(11) NOT NULL,
  `id_pengajuan` int(11) NOT NULL,
  `username_verifikator` varchar(100) NOT NULL,
  `role_verifikator` varchar(50) NOT NULL,
  `status_verifikasi` varchar(50) NOT NULL,
  `catatan` text DEFAULT NULL,
  `tanggal_verifikasi` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `berita_desa`
--
ALTER TABLE `berita_desa`
  ADD PRIMARY KEY (`id_berita`);

--
-- Indeks untuk tabel `berkas_pengajuan`
--
ALTER TABLE `berkas_pengajuan`
  ADD PRIMARY KEY (`id_berkas`),
  ADD KEY `id_pengajuan` (`id_pengajuan`);

--
-- Indeks untuk tabel `laporan_users`
--
ALTER TABLE `laporan_users`
  ADD PRIMARY KEY (`id_laporan`);

--
-- Indeks untuk tabel `layanan`
--
ALTER TABLE `layanan`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `notifikasi`
--
ALTER TABLE `notifikasi`
  ADD PRIMARY KEY (`id_notifikasi`);

--
-- Indeks untuk tabel `pengajuan`
--
ALTER TABLE `pengajuan`
  ADD PRIMARY KEY (`id_pengajuan`);

--
-- Indeks untuk tabel `penilaian_layanan`
--
ALTER TABLE `penilaian_layanan`
  ADD PRIMARY KEY (`id_penilaian`);

--
-- Indeks untuk tabel `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id_role`);

--
-- Indeks untuk tabel `syarat_surat`
--
ALTER TABLE `syarat_surat`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_jenis_surat` (`id_jenis_surat`);

--
-- Indeks untuk tabel `template_surat`
--
ALTER TABLE `template_surat`
  ADD PRIMARY KEY (`id_template`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `nik` (`nik`);

--
-- Indeks untuk tabel `verifikasi`
--
ALTER TABLE `verifikasi`
  ADD PRIMARY KEY (`id_verifikasi`),
  ADD KEY `id_pengajuan` (`id_pengajuan`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `berita_desa`
--
ALTER TABLE `berita_desa`
  MODIFY `id_berita` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `berkas_pengajuan`
--
ALTER TABLE `berkas_pengajuan`
  MODIFY `id_berkas` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT untuk tabel `laporan_users`
--
ALTER TABLE `laporan_users`
  MODIFY `id_laporan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `layanan`
--
ALTER TABLE `layanan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT untuk tabel `notifikasi`
--
ALTER TABLE `notifikasi`
  MODIFY `id_notifikasi` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `pengajuan`
--
ALTER TABLE `pengajuan`
  MODIFY `id_pengajuan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT untuk tabel `penilaian_layanan`
--
ALTER TABLE `penilaian_layanan`
  MODIFY `id_penilaian` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `roles`
--
ALTER TABLE `roles`
  MODIFY `id_role` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `syarat_surat`
--
ALTER TABLE `syarat_surat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT untuk tabel `template_surat`
--
ALTER TABLE `template_surat`
  MODIFY `id_template` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT untuk tabel `verifikasi`
--
ALTER TABLE `verifikasi`
  MODIFY `id_verifikasi` int(11) NOT NULL AUTO_INCREMENT;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `berkas_pengajuan`
--
ALTER TABLE `berkas_pengajuan`
  ADD CONSTRAINT `berkas_pengajuan_ibfk_1` FOREIGN KEY (`id_pengajuan`) REFERENCES `pengajuan` (`id_pengajuan`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `syarat_surat`
--
ALTER TABLE `syarat_surat`
  ADD CONSTRAINT `syarat_surat_ibfk_1` FOREIGN KEY (`id_jenis_surat`) REFERENCES `layanan` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `verifikasi`
--
ALTER TABLE `verifikasi`
  ADD CONSTRAINT `verifikasi_ibfk_1` FOREIGN KEY (`id_pengajuan`) REFERENCES `pengajuan` (`id_pengajuan`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
