-- =====================================================
-- JALANKAN SQL INI DI phpMyAdmin UNTUK FIX DATA LAMA
-- yang sudah masuk database tapi status-nya NULL
-- =====================================================

-- 1. Fix semua pengajuan yang status_rtrw-nya NULL
UPDATE pengajuan SET status_rtrw = 'Menunggu' WHERE status_rtrw IS NULL OR status_rtrw = '';

-- 2. Fix semua pengajuan yang status_admin-nya NULL
UPDATE pengajuan SET status_admin = 'Menunggu' WHERE status_admin IS NULL OR status_admin = '';

-- 3. Fix semua pengajuan yang status_akhir-nya NULL
UPDATE pengajuan SET status_akhir = 'Menunggu' WHERE status_akhir IS NULL OR status_akhir = '';

-- 4. Fix tanggal_pengajuan yang NULL
UPDATE pengajuan SET tanggal_pengajuan = NOW() WHERE tanggal_pengajuan IS NULL;

-- 5. Set DEFAULT VALUE agar masalah ini tidak terulang
ALTER TABLE pengajuan MODIFY COLUMN status_rtrw VARCHAR(50) DEFAULT 'Menunggu';
ALTER TABLE pengajuan MODIFY COLUMN status_admin VARCHAR(50) DEFAULT 'Menunggu';
ALTER TABLE pengajuan MODIFY COLUMN status_akhir VARCHAR(50) DEFAULT 'Menunggu';
ALTER TABLE pengajuan MODIFY COLUMN tanggal_pengajuan DATETIME DEFAULT CURRENT_TIMESTAMP;
