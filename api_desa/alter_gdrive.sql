-- ============================================================
-- alter_gdrive.sql
-- Migrasi database: tambah kolom Google Drive
-- Jalankan sekali saja di phpMyAdmin atau via CLI:
--   mysql -u root db_desa < alter_gdrive.sql
-- ============================================================

USE db_desa;

-- Tambah kolom Drive ke tabel berkas_pengajuan
ALTER TABLE `berkas_pengajuan`
  ADD COLUMN IF NOT EXISTS `drive_file_id` VARCHAR(200) DEFAULT NULL
    COMMENT 'ID file di Google Drive',
  ADD COLUMN IF NOT EXISTS `drive_url` VARCHAR(500) DEFAULT NULL
    COMMENT 'URL view file di Google Drive (https://drive.google.com/file/d/...)';

-- Tambah kolom Drive URL ke tabel pengajuan (untuk dokumen hasil surat)
ALTER TABLE `pengajuan`
  ADD COLUMN IF NOT EXISTS `dokumen_drive_url` VARCHAR(500) DEFAULT NULL
    COMMENT 'URL PDF hasil surat di Google Drive';

-- Verifikasi
SELECT 'Migrasi Google Drive berhasil!' AS pesan;
DESCRIBE berkas_pengajuan;
