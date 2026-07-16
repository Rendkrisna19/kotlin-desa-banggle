<?php
header('Content-Type: application/json');
include 'koneksi.php';

$action = isset($_GET['action']) ? $_GET['action'] : '';
$response = array();

if ($action == 'read') {
    // Ambil semua daftar template
    $query = mysqli_query($koneksi, "SELECT * FROM template_surat ORDER BY id_template DESC");
    $data = array();
    while ($row = mysqli_fetch_assoc($query)) {
        $data[] = $row;
    }
    echo json_encode($data);

} else if ($action == 'update' || $action == 'create') {
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $jenis = mysqli_real_escape_string($koneksi, $_POST['jenis_surat']);
        $kode = mysqli_real_escape_string($koneksi, $_POST['kode_surat']);
        $pembuka = mysqli_real_escape_string($koneksi, $_POST['isi_pembuka']);
        $penutup = mysqli_real_escape_string($koneksi, $_POST['isi_penutup']);

        if ($action == 'update') {
            $id = intval($_POST['id_template']);
            $sql = "UPDATE template_surat SET jenis_surat='$jenis', kode_surat='$kode', isi_pembuka='$pembuka', isi_penutup='$penutup' WHERE id_template=$id";
        } else {
            $sql = "INSERT INTO template_surat (jenis_surat, kode_surat, isi_pembuka, isi_penutup) VALUES ('$jenis', '$kode', '$pembuka', '$penutup')";
        }

        if (mysqli_query($koneksi, $sql)) {
            $response['status'] = 'success';
            $response['message'] = 'Template berhasil disimpan';
        } else {
            $response['status'] = 'error';
            $response['message'] = mysqli_error($koneksi);
        }
        echo json_encode($response);
    }
}
mysqli_close($koneksi);
?>
