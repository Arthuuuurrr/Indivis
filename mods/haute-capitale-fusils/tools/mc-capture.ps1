# Capture la fenêtre d'un client de test en PNG, sans lui envoyer la moindre touche ni le moindre
# clic, et sans voler le focus si PrintWindow suffit.
#
# La règle : ne jamais injecter d'entrée globale sur cette machine. Le joueur y a sa propre partie
# ouverte, et une frappe adressée à la mauvaise fenêtre part dans sa session.
#
# Usage : mc-capture.ps1 -Pid_ 1234 -Out shot.png [-Focus]
param(
    [Parameter(Mandatory = $true)][int]$Pid_,
    [Parameter(Mandatory = $true)][string]$Out,
    [switch]$Focus
)

Add-Type -AssemblyName System.Drawing
Add-Type @"
using System;
using System.Drawing;
using System.Runtime.InteropServices;
public class McCap {
    [DllImport("user32.dll")] public static extern bool PrintWindow(IntPtr h, IntPtr dc, uint flags);
    [DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr h, out RECT r);
    [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr h);
    [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr h, int n);
    [StructLayout(LayoutKind.Sequential)] public struct RECT { public int Left, Top, Right, Bottom; }
}
"@

$proc = Get-Process -Id $Pid_ -ErrorAction SilentlyContinue
if (-not $proc -or $proc.MainWindowHandle -eq 0) { Write-Output "NO_WINDOW pour pid $Pid_"; exit 1 }
$h = $proc.MainWindowHandle

$dir = Split-Path -Parent $Out
if ($dir -and -not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }

$r = New-Object McCap+RECT
[McCap]::GetWindowRect($h, [ref]$r) | Out-Null
$w = $r.Right - $r.Left
$hgt = $r.Bottom - $r.Top

$bmp = New-Object System.Drawing.Bitmap $w, $hgt
$g = [System.Drawing.Graphics]::FromImage($bmp)

if ($Focus) {
    [McCap]::ShowWindow($h, 9) | Out-Null
    [McCap]::SetForegroundWindow($h) | Out-Null
    Start-Sleep -Milliseconds 900
    $g.CopyFromScreen($r.Left, $r.Top, 0, 0, $bmp.Size)
} else {
    # PW_RENDERFULLCONTENT : capture le contenu composé par DWM, y compris OpenGL, sans premier plan.
    $dc = $g.GetHdc()
    [McCap]::PrintWindow($h, $dc, 2) | Out-Null
    $g.ReleaseHdc($dc)
}

$bmp.Save($Out, [System.Drawing.Imaging.ImageFormat]::Png)
$g.Dispose()
$bmp.Dispose()
Write-Output "CAPTURE $Out ($w x $hgt)"
