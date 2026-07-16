$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

Write-Host "Clearing app data..."
& $adb shell pm clear com.desabanggle.ovylia
Start-Sleep -Seconds 4

Write-Host "Starting app..."
& $adb shell am start -n com.desabanggle.ovylia/.LoginActivity
Start-Sleep -Seconds 4

Write-Host "Phase 1: Registration"
& $adb shell input tap 540 1660
Start-Sleep -Seconds 4

& $adb shell input tap 540 600
Start-Sleep -Seconds 2
& $adb shell input text "warga_demo"
Start-Sleep -Seconds 2
& $adb shell input keyevent 61
Start-Sleep -Seconds 2
& $adb shell input text "Demo123\!"
Start-Sleep -Seconds 2
& $adb shell input keyevent 61
Start-Sleep -Seconds 2
& $adb shell input text "Warga%sDemo%sSOW"
Start-Sleep -Seconds 2
& $adb shell input keyevent 61
Start-Sleep -Seconds 2
& $adb shell input text "3505019901010002"
Start-Sleep -Seconds 2
& $adb shell input keyevent 111
Start-Sleep -Seconds 3

& $adb shell input tap 540 1450
Start-Sleep -Seconds 6

& $adb shell input keyevent 4
Start-Sleep -Seconds 4

Write-Host "Phase 1: Login"
& $adb shell input tap 540 1040
Start-Sleep -Seconds 2
& $adb shell input text "warga_demo"
Start-Sleep -Seconds 2
& $adb shell input tap 540 1253
Start-Sleep -Seconds 2
& $adb shell input text "Demo123\!"
Start-Sleep -Seconds 2
& $adb shell input keyevent 111
Start-Sleep -Seconds 2
& $adb shell input tap 540 1480
Start-Sleep -Seconds 5

Write-Host "Phase 2: Document Submission"
& $adb shell input tap 540 817
Start-Sleep -Seconds 4
& $adb shell input tap 980 150
Start-Sleep -Seconds 4
& $adb shell input tap 540 800
Start-Sleep -Seconds 5
& $adb shell input tap 540 600
Start-Sleep -Seconds 5
& $adb shell input tap 540 1200
Start-Sleep -Seconds 5
& $adb shell input tap 540 1000
Start-Sleep -Seconds 5
& $adb shell input keyevent 4
Start-Sleep -Seconds 3
& $adb shell input tap 540 1600
Start-Sleep -Seconds 5

Write-Host "Phase 3: RT/RW Verification"
& $adb shell input tap 540 1040
Start-Sleep -Seconds 2
& $adb shell input text "rtrw001"
Start-Sleep -Seconds 2
& $adb shell input tap 540 1253
Start-Sleep -Seconds 2
& $adb shell input text "admin123"
Start-Sleep -Seconds 2
& $adb shell input keyevent 111
Start-Sleep -Seconds 2
& $adb shell input tap 540 1480
Start-Sleep -Seconds 5

& $adb shell input tap 540 720
Start-Sleep -Seconds 5
& $adb shell input tap 540 450
Start-Sleep -Seconds 5
& $adb shell input tap 750 2100
Start-Sleep -Seconds 5
& $adb shell input keyevent 4
Start-Sleep -Seconds 3
& $adb shell input tap 540 1600
Start-Sleep -Seconds 5

Write-Host "Phase 4: Sekdes Review"
& $adb shell input tap 540 1040
Start-Sleep -Seconds 2
& $adb shell input text "sekdes_banggle"
Start-Sleep -Seconds 2
& $adb shell input tap 540 1253
Start-Sleep -Seconds 2
& $adb shell input text "admin123"
Start-Sleep -Seconds 2
& $adb shell input keyevent 111
Start-Sleep -Seconds 2
& $adb shell input tap 540 1480
Start-Sleep -Seconds 5

& $adb shell input tap 540 850
Start-Sleep -Seconds 5
& $adb shell input tap 540 450
Start-Sleep -Seconds 5
& $adb shell input tap 750 2150
Start-Sleep -Seconds 5
& $adb shell input keyevent 4
Start-Sleep -Seconds 3
& $adb shell input tap 540 1600
Start-Sleep -Seconds 5

Write-Host "Phase 5: Kades Final Signature"
& $adb shell input tap 540 1040
Start-Sleep -Seconds 2
& $adb shell input text "kades_banggle"
Start-Sleep -Seconds 2
& $adb shell input tap 540 1253
Start-Sleep -Seconds 2
& $adb shell input text "admin123"
Start-Sleep -Seconds 2
& $adb shell input keyevent 111
Start-Sleep -Seconds 2
& $adb shell input tap 540 1480
Start-Sleep -Seconds 5

& $adb shell input tap 540 780
Start-Sleep -Seconds 5
& $adb shell input tap 540 450
Start-Sleep -Seconds 5
& $adb shell input text "123456"
Start-Sleep -Seconds 2
& $adb shell input tap 540 1250
Start-Sleep -Seconds 5

Write-Host "Demo Sequence Finished."
