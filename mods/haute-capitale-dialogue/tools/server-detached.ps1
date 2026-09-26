# Lance le serveur de dev détaché (dossier run/, RCON 25575 mot de passe hcdtest, port 25565).
# Journal : server-dev.log à la racine, puis run/logs/latest.log pour le vrai journal serveur.
#
# Usage : server-detached.ps1
$root = "C:\Users\denne\haute-capitale-dialogue"
$log = "$root\server-dev.log"
if (Test-Path $log) { Remove-Item $log -Force }

$gradle = "C:\Users\denne\.gradle\wrapper\dists\gradle-9.5.1-bin\iq79hdu3mqx29lgffhp8bfmx\gradle-9.5.1\bin\gradle.bat"
$cmd = "cd /d `"$root`" && set `"JAVA_HOME=C:\Users\denne\.jdks\jdk-21.0.12+8`" && `"$gradle`" --no-daemon runServer -PdialogueDevTools --args=`"nogui`" --console=plain > `"$log`" 2>&1"
Start-Process -FilePath "cmd.exe" -ArgumentList "/c", $cmd -WindowStyle Hidden
Write-Output "serveur lance -> $log"
