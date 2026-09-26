# Lance un client de dev détaché du shell appelant : un script bash qui se termine tue un client
# enfant, Start-Process non. Le client rejoint directement le serveur, sans passer par les menus.
#
# Chaque client reçoit son propre dossier de jeu (--gameDir) : deux clients qui partagent le même
# dossier se disputent le cache des jars remappés (.fabric/remappedJars) et le second s'arrête net.
#
# --no-daemon volontaire : un démon Gradle partagé est arrêté ou recyclé par n'importe quelle autre
# compilation du dossier, et emporte le client avec lui.
#
# Usage : client-detached.ps1 -N 1 -Username Alice [-Server 127.0.0.1:25565]
param(
    [string]$N = "1",
    [string]$Username = "Joueur1",
    [string]$Server = "127.0.0.1:25565"
)

$root = "C:\Users\denne\haute-capitale-dialogue"
$log = "$root\client$N.log"
$gameDir = "$root\run-client-$N"
if (Test-Path $log) { Remove-Item $log -Force }
if (-not (Test-Path $gameDir)) { New-Item -ItemType Directory -Path $gameDir | Out-Null }

$gradle = "C:\Users\denne\.gradle\wrapper\dists\gradle-9.5.1-bin\iq79hdu3mqx29lgffhp8bfmx\gradle-9.5.1\bin\gradle.bat"
$env:JAVA_HOME = "C:\Users\denne\.jdks\jdk-21.0.12+8"

$gameArgs = "--username $Username --gameDir $gameDir --quickPlayMultiplayer $Server"
$cmd = "cd /d `"$root`" && set `"JAVA_HOME=C:\Users\denne\.jdks\jdk-21.0.12+8`" && `"$gradle`" --no-daemon runClient -PdialogueDevTools --args=`"$gameArgs`" --console=plain > `"$log`" 2>&1"
Start-Process -FilePath "cmd.exe" -ArgumentList "/c", $cmd -WindowStyle Hidden
Write-Output "client $N ($Username) lance -> $log (gameDir $gameDir)"
