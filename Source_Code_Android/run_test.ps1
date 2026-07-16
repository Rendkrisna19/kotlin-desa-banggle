$rand = Get-Random -Minimum 100000 -Maximum 999999
$nik = "1234567890$rand"
$username = "warga_$rand"

adb shell input keyevent KEYCODE_BACK
adb reverse tcp:8080 tcp:8080

Write-Host "Resetting App Data..."
adb shell pm clear com.desabanggle.ovylia

Write-Host "Launching App..."
adb shell am start -n com.desabanggle.ovylia/.SplashActivity
Start-Sleep -Seconds 4

Write-Host "1. Register Warga"
adb shell input tap 400 1350
Start-Sleep -Seconds 1
adb shell input tap 400 650
adb shell input text "$username"
adb shell input keyevent KEYCODE_TAB
adb shell input text "$username"
adb shell input keyevent KEYCODE_TAB
adb shell input text "$nik"
adb shell input keyevent KEYCODE_TAB
adb shell input text "warga123"
Start-Sleep -Seconds 1
adb shell input tap 400 1450
Start-Sleep -Seconds 2

Write-Host "2. Admin Verifikasi Akun"
adb shell input tap 400 900
adb shell input text "admin"
adb shell input tap 400 1100
adb shell input text "admin123"
adb shell input tap 400 1400
Start-Sleep -Seconds 2
adb shell input tap 500 1150
Start-Sleep -Seconds 1
adb shell uiautomator dump
adb shell cat /sdcard/window_dump.xml | Select-String "Setujui"
adb shell input tap 850 500
Start-Sleep -Seconds 1
adb shell input keyevent KEYCODE_BACK
adb shell input tap 850 150
Start-Sleep -Seconds 1
adb shell input tap 850 1300
Start-Sleep -Seconds 2

Write-Host "3. Login Warga Baru"
adb shell input tap 400 900
adb shell input text "$username"
adb shell input tap 400 1100
adb shell input text "warga123"
adb shell input tap 400 1400
Start-Sleep -Seconds 2
adb shell input tap 500 1250
Start-Sleep -Seconds 1
adb shell input tap 500 350
Start-Sleep -Seconds 1
adb shell input tap 500 350
adb shell input text "Test Name"
adb shell input keyevent KEYCODE_TAB
adb shell input text "1234567890123456"
Start-Sleep -Seconds 1

Write-Host "Creating dummy PDF"
adb shell "echo 'Dummy KTP' > /sdcard/Download/dummy_ktp.pdf"
adb shell "echo 'Dummy KK' > /sdcard/Download/dummy_kk.pdf"
Start-Sleep -Seconds 1

Write-Host "Upload KTP"
adb shell input tap 500 900
Start-Sleep -Seconds 2
adb shell input keyevent 19
adb shell input keyevent 19
adb shell input keyevent 66
Start-Sleep -Seconds 1

Write-Host "Upload KK"
adb shell input tap 500 1100
Start-Sleep -Seconds 2
adb shell input keyevent 20
adb shell input keyevent 20
adb shell input keyevent 66
Start-Sleep -Seconds 1

adb shell input tap 500 1350
Start-Sleep -Seconds 3

Write-Host "Logout Warga"
adb shell input tap 900 150
Start-Sleep -Seconds 1
adb shell input tap 850 1300
Start-Sleep -Seconds 2

Write-Host "4. Admin Review Dokumen"
adb shell input tap 400 900
adb shell input text "admin"
adb shell input tap 400 1100
adb shell input text "admin123"
adb shell input tap 400 1400
Start-Sleep -Seconds 2
adb shell input tap 500 900
Start-Sleep -Seconds 1
adb shell input tap 500 500
Start-Sleep -Seconds 1
adb shell input tap 850 1350
Start-Sleep -Seconds 2

Write-Host "Logout Admin"
adb shell input keyevent KEYCODE_BACK
adb shell input tap 850 150
Start-Sleep -Seconds 1
adb shell input tap 850 1300
Start-Sleep -Seconds 2

Write-Host "5. Sekdes Tindak Lanjut"
adb shell input tap 400 900
adb shell input text "sekdes"
adb shell input tap 400 1100
adb shell input text "sekdes123"
adb shell input tap 400 1400
Start-Sleep -Seconds 2
adb shell input tap 500 1200
Start-Sleep -Seconds 1
adb shell input tap 500 500
Start-Sleep -Seconds 1
adb shell input tap 850 1000
Start-Sleep -Seconds 1
adb shell input tap 500 1200
Start-Sleep -Seconds 1
adb shell input tap 500 1350
Start-Sleep -Seconds 2

Write-Host "Logout Sekdes"
adb shell input keyevent KEYCODE_BACK
adb shell input tap 850 150
Start-Sleep -Seconds 1
adb shell input tap 850 1300
Start-Sleep -Seconds 2

Write-Host "6. Kades Pengesahan TTD (Blank screen check)"
adb shell input tap 400 900
adb shell input text "kades"
adb shell input tap 400 1100
adb shell input text "kades123"
adb shell input tap 400 1400
Start-Sleep -Seconds 2
adb shell input tap 500 1200
Start-Sleep -Seconds 3
adb shell uiautomator dump
adb shell cat /sdcard/window_dump.xml | Select-String "Pengesahan TTD Digital"
adb shell cat /sdcard/window_dump.xml | Select-String "warga_"
