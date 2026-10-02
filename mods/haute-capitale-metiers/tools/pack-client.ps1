# Lance un client « pack complet » isolé : les bibliothèques et versions du .minecraft du joueur,
# mais un gameDir à part (packclient/) dont les mods sont des liens durs vers les jars du banc
# testserver-pack/ — donc exactement les mods du serveur d'essai, notre jar compris. Ne touche
# jamais au .minecraft\mods du joueur. Lancé détaché : un script bash qui se termine ne le tue pas.
#
# Usage : pack-client.ps1 [-Username Joueur1] [-Server 127.0.0.1:25621] [-N 1]
param(
    [string]$Username = "Joueur1",
    [string]$Server = "127.0.0.1:25621",
    [string]$N = "1"
)
$ErrorActionPreference = "Stop"
$mc       = "C:\Users\denne\AppData\Roaming\.minecraft"
$root     = "C:\Users\denne\haute-capitale-metiers\packclient"
$javaExe  = "C:\Users\denne\.jdks\jdk-21.0.12+8\bin\javaw.exe"
$fabricId = "fabric-loader-0.19.5-1.21.11"
$baseId   = "1.21.11"

# Sans focus, le client ouvrirait le menu de pause à chaque image : nos captures ne verraient que lui.
$options = "$root\options.txt"
if (Test-Path $options) {
    (Get-Content $options) -replace "^pauseOnLostFocus:true$", "pauseOnLostFocus:false" | Set-Content $options -Encoding ascii
}

$fabric = Get-Content "$mc\versions\$fabricId\$fabricId.json" -Raw | ConvertFrom-Json
$base   = Get-Content "$mc\versions\$baseId\$baseId.json" -Raw | ConvertFrom-Json

function Test-Rules($lib) {
    if (-not $lib.PSObject.Properties.Name.Contains('rules')) { return $true }
    $allowed = $false
    foreach ($r in $lib.rules) {
        $matchOs = $true
        if ($r.PSObject.Properties.Name.Contains('os') -and $r.os.PSObject.Properties.Name.Contains('name')) {
            $matchOs = ($r.os.name -eq 'windows')
        }
        if ($matchOs) { $allowed = ($r.action -eq 'allow') }
    }
    return $allowed
}
function Get-LibPath($lib) {
    if ($lib.PSObject.Properties.Name.Contains('downloads') -and $lib.downloads.PSObject.Properties.Name.Contains('artifact')) {
        return Join-Path "$mc\libraries" ($lib.downloads.artifact.path -replace '/', '\')
    }
    $parts = $lib.name -split ':'
    $g = $parts[0] -replace '\.', '\'; $a = $parts[1]; $v = $parts[2]
    $file = if ($parts.Count -gt 3) { "$a-$v-$($parts[3]).jar" } else { "$a-$v.jar" }
    return Join-Path "$mc\libraries" "$g\$a\$v\$file"
}
$cp = New-Object System.Collections.Generic.List[string]
foreach ($lib in @($fabric.libraries) + @($base.libraries)) {
    if (-not (Test-Rules $lib)) { continue }
    $p = Get-LibPath $lib
    if ((Test-Path $p) -and -not $cp.Contains($p)) { $cp.Add($p) }
}
$clientJar = "$mc\versions\$baseId\$baseId.jar"
if (-not (Test-Path $clientJar)) { throw "client jar missing: $clientJar" }
$cp.Add($clientJar)

# UUID hors ligne stable par pseudo, comme le ferait un serveur en online-mode=false.
$md5 = [System.Security.Cryptography.MD5]::Create()
$bytes = $md5.ComputeHash([System.Text.Encoding]::UTF8.GetBytes("OfflinePlayer:$Username"))
$bytes[6] = ($bytes[6] -band 0x0f) -bor 0x30
$bytes[8] = ($bytes[8] -band 0x3f) -bor 0x80
$uuid = ($bytes | ForEach-Object { $_.ToString("x2") }) -join ""

$log = "$root\client$N.log"
if (Test-Path $log) { Remove-Item $log -Force }
$args = @("-Xmx4G", "-XX:+UseG1GC", "-Djava.net.preferIPv4Stack=true", "-DFabricMcEmu=net.minecraft.client.main.Main",
    "-cp", ($cp -join ";"), $fabric.mainClass,
    "--username", $Username, "--version", $fabricId, "--gameDir", $root, "--assetsDir", "$mc\assets",
    "--assetIndex", $base.assetIndex.id, "--uuid", $uuid, "--accessToken", "0",
    "--userType", "legacy", "--versionType", "release", "--quickPlayMultiplayer", $Server)
Start-Process -FilePath $javaExe -ArgumentList $args -WorkingDirectory $root -RedirectStandardOutput $log -RedirectStandardError "$root\client$N.err.log"
Write-Output "client pack $N ($Username) lance vers $Server -> $log"
