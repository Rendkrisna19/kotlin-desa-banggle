<?php
$hash = '$2y$10$QJOuI9ZBwrxB5nHN/lpJcOhoO/cMTc/pf4zxFjP9HfMZLSXQFDcSq';
echo 'admin: ' . (password_verify('admin', $hash) ? 'YES' : 'NO') . "\n";
echo 'admin123: ' . (password_verify('admin123', $hash) ? 'YES' : 'NO') . "\n";
echo '123456: ' . (password_verify('123456', $hash) ? 'YES' : 'NO') . "\n";
?>
