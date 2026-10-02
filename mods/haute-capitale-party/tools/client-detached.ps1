# Lance un client de dev détaché du shell appelant : un script bash qui se termine tue un client
# enfant, Start-Process non. Le client rejoint directement le serveur, sans passer par les menus.
#
# --no-daemon volontaire : un démon Gradle partagé est arrêté ou recyclé par n'importe quelle autre
# compilation du dossier, et emporte le client avec lui.
#
# Usage : client-detached.ps1 -N 1 -Username Alice
param(
    [string]$N = "1",
    [string]$Username = "Joueur1",
    [string]$Server = "127.0.0.1:25565"
)

$root = "C:\Users\denne\haute-capitale-party"
$log = "$root\client$N.log"
if (Test-Path $log) { Remove-Item $log -Force }

$gradle = "C:\Users\denne\.gradle\wrapper\dists\gradle-9.7.1-bin\1w1c7tv4s851m17nbqdsro2tv\gradle-9.7.1\bin\gradle.bat"
$env:JAVA_HOME = "C:\Users\denne\.jdks\jdk-21.0.12+8"

# set "VAR=valeur" avec les guillemets : sans eux, cmd garde l'espace qui précède le && dans la
# valeur, et gradle.bat cherche alors « ...\jdk-21.0.12+8 \bin\java.exe ».
$gameArgs = "--username $Username --quickPlayMultiplayer $Server"
$cmd = "cd /d `"$root`" && set `"JAVA_HOME=C:\Users\denne\.jdks\jdk-21.0.12+8`" && `"$gradle`" --no-daemon runClient --args=`"$gameArgs`" --console=plain > `"$log`" 2>&1"
Start-Process -FilePath "cmd.exe" -ArgumentList "/c", $cmd -WindowStyle Hidden
Write-Output "client $N ($Username) lance -> $log"
