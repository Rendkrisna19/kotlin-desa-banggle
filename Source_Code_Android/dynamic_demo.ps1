$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

function Tap-Dynamic {
    param([string]$ResourceId, [string]$Text, [string]$Class, [string]$InputString, [int]$Delay = 4, [switch]$Backspace)
    
    Write-Host "Dumping UI for '$ResourceId' '$Text' '$Class'..."
    & $adb shell uiautomator dump /sdcard/window_dump.xml | Out-Null
    & $adb pull /sdcard/window_dump.xml dump.xml | Out-Null
    
    [xml]$xml = Get-Content -Path dump.xml -Raw
    
    $node = $null
    if ($ResourceId) {
        $node = $xml.SelectNodes("//node[@resource-id='$ResourceId']") | Select-Object -First 1
    } elseif ($Text) {
        $node = $xml.SelectNodes("//node[@text='$Text' or contains(@content-desc, '$Text')]") | Select-Object -First 1
        if (-not $node) {
             $node = $xml.SelectNodes("//node[contains(@text, '$Text')]") | Select-Object -First 1
        }
    } elseif ($Class) {
        $node = $xml.SelectNodes("//node[@class='$Class']") | Select-Object -First 1
    }
    
    if ($node) {
        $bounds = $node.bounds -replace '\[|\]', ' ' -split ' ' | Where-Object { $_ -ne '' }
        $x = [math]::Floor(([int]$bounds[0] + [int]$bounds[2]) / 2)
        $y = [math]::Floor(([int]$bounds[1] + [int]$bounds[3]) / 2)
        
        Write-Host "Tapping ($x, $y)..."
        & $adb shell input tap $x $y
        Start-Sleep -Seconds 1
        
        if ($Backspace) {
            # Clear input
            for($i=0; $i -lt 15; $i++) { & $adb shell input keyevent 67 }
        }
        
        if ($InputString) {
            $escaped = $InputString -replace ' ', '%s'
            Write-Host "Inputting text: $InputString"
            & $adb shell input text `"$escaped`"
            Start-Sleep -Seconds 1
            
            Write-Host "Hiding keyboard..."
            & $adb shell input keyevent 111
            Start-Sleep -Seconds 2
        } else {
            Start-Sleep -Seconds $Delay
        }
    } else {
        Write-Host "Node not found!"
    }
}

Write-Host "Phase 1: Registration"
& $adb shell pm clear com.desabanggle.ovylia
Start-Sleep -Seconds 4
& $adb shell am start -n com.desabanggle.ovylia/.LoginActivity
Start-Sleep -Seconds 4

Tap-Dynamic -Text "Daftar di sini"
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/regUsername" -InputString "warga_demo" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/regPassword" -InputString "Demo123\!" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/regNama" -InputString "Warga Demo SOW" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/regNik" -InputString "3505019901010002" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnRegister" -Delay 6
Tap-Dynamic -Text "Kembali ke Login" -Delay 4

Write-Host "Phase 1: Login"
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/etUsername" -InputString "warga_demo" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/etPassword" -InputString "Demo123\!" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnLogin" -Delay 6

Write-Host "Phase 2: Document Submission"
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/menuKelolaPengajuan" -Delay 4
Tap-Dynamic -Text "Tambah Baru" -Delay 4
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnUpload" -Delay 5
Tap-Dynamic -Text "dummy.pdf" -Delay 5
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnKirim" -Delay 6
& $adb shell input keyevent 4
Start-Sleep -Seconds 4
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/menuCekStatus" -Delay 4
& $adb shell input keyevent 4
Start-Sleep -Seconds 4
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnLogout" -Delay 5

Write-Host "Phase 3: RT/RW Verification"
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/etUsername" -InputString "rtrw001" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/etPassword" -InputString "admin123" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnLogin" -Delay 6
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/menuValidasiSurat" -Delay 5
Tap-Dynamic -Text "warga_demo" -Delay 5
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnSetujuiSurat" -Delay 6
& $adb shell input keyevent 4
Start-Sleep -Seconds 4
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnLogoutRt" -Delay 5

Write-Host "Phase 4: Sekdes Review"
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/etUsername" -InputString "sekdes_banggle" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/etPassword" -InputString "admin123" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnLogin" -Delay 6
Tap-Dynamic -Text "Surat Terverifikasi" -Delay 5
Tap-Dynamic -Text "warga_demo" -Delay 5
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnSetujuiSekdes" -Delay 6
& $adb shell input keyevent 4
Start-Sleep -Seconds 4
Tap-Dynamic -Text "Keluar Dari Sistem" -Delay 5

Write-Host "Phase 5: Kades Final Signature"
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/etUsername" -InputString "kades_banggle" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/etPassword" -InputString "admin123" -Backspace
Tap-Dynamic -ResourceId "com.desabanggle.ovylia:id/btnLogin" -Delay 6
Tap-Dynamic -Text "Monitor Surat" -Delay 5
Tap-Dynamic -Text "warga_demo" -Delay 5
Tap-Dynamic -Text "Sahkan Dokumen Digital" -Delay 5
Tap-Dynamic -Class "android.widget.EditText" -InputString "123456" -Backspace
Tap-Dynamic -Text "Konfirmasi" -Delay 5
Tap-Dynamic -Text "OK" -Delay 5

Write-Host "Demo Sequence Finished."
