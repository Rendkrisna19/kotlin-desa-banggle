adb shell am force-stop com.desabanggle.ovylia
adb shell am start -n com.desabanggle.ovylia/.SplashActivity
Start-Sleep -Seconds 4

# Clear username field by long press and delete or just backspaces
adb shell input tap 400 900
adb shell input keyevent --longpress 67
adb shell input keyevent --longpress 67
Start-Sleep -Seconds 1
adb shell input text "kades"

# Clear password field
adb shell input tap 400 1100
Start-Sleep -Seconds 1
adb shell input text "kades123"

# Login
adb shell input tap 400 1400
Start-Sleep -Seconds 2

# Tap Monitor Surat (assuming KadesDashboard is open)
adb shell input tap 500 1200
Start-Sleep -Seconds 3

# Dump UI
adb shell uiautomator dump
adb shell cat /sdcard/window_dump.xml
