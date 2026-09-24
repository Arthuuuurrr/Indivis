# Client de dev détaché (Start-Process), qui rejoint directement le serveur de test.
# Un seul client, petite fenêtre, jamais de focus volé ni d'entrée injectée.
param([string]$N = "1", [string]$Username = "Chasseur", [string]$Server = "127.0.0.1:25834")
$root = "C:\Users\denne\haute-capitale-fusils"
$log = "$root\client$N.log"
$gameDir = "$root\run-client-$N"
if (Test-Path $log) { Remove-Item $log -Force }
if (-not (Test-Path $gameDir)) { New-Item -ItemType Directory -Path $gameDir | Out-Null }
$gradle = "C:\Users\denne\.gradle\wrapper\dists\gradle-9.5.1-bin\iq79hdu3mqx29lgffhp8bfmx\gradle-9.5.1\bin\gradle.bat"
$gameArgs = "--username $Username --gameDir $gameDir --quickPlayMultiplayer $Server --width 1024 --height 600"
$cmd = "cd /d `"$root`" && set `"JAVA_HOME=C:\Users\denne\.jdks\jdk-21.0.12+8`" && `"$gradle`" --no-daemon runClient --args=`"$gameArgs`" --console=plain > `"$log`" 2>&1"
Start-Process -FilePath "cmd.exe" -ArgumentList "/c", $cmd -WindowStyle Hidden
Write-Output "client $N ($Username) lance -> $log (gameDir $gameDir)"
