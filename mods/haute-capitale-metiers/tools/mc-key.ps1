# Envoie une touche à UNE fenêtre de client de test, désignée par son PID — jamais au clavier global.
#
# PostMessage(WM_KEYDOWN/WM_KEYUP) vise le handle de la fenêtre : rien n'atteint une autre application,
# même si elle a le focus. GLFW lit le scancode dans lParam (bits 16-23), il faut donc le fournir.
#
# Usage : mc-key.ps1 -Pid_ 12345 -Key ESC   (ESC, F5, E, F3, ENTER, T, 1-9)
param(
    [Parameter(Mandatory = $true)][int]$Pid_,
    [Parameter(Mandatory = $true)][string]$Key,
    [string]$Root = "C:\Users\denne\haute-capitale-metiers",
    [int]$X = -1,
    [int]$Y = -1
)

$proc = Get-CimInstance Win32_Process -Filter "ProcessId = $Pid_"
if (-not $proc -or -not ($proc.CommandLine -like "*$Root*") -or
    -not ($proc.CommandLine -like "*net.fabricmc.devlaunchinjector*" -or $proc.CommandLine -like "*--gameDir*$Root\packclient*")) {
    Write-Error "PID $Pid_ n'est pas un client de test de ce projet : rien envoyé."
    exit 1
}
if (-not ($proc.CommandLine -match '--username\s+(\S+)')) {
    Write-Error "PID $Pid_ n'a pas de pseudo (serveur dev ?) : rien envoyé."
    exit 1
}
$hwnd = (Get-Process -Id $Pid_).MainWindowHandle
if ($hwnd -eq 0) { Write-Error "Pas de fenêtre pour $Pid_"; exit 1 }

$table = @{
    "ESC"   = @(0x1B, 0x01)
    "F5"    = @(0x74, 0x3F)
    "F3"    = @(0x72, 0x3D)
    "E"     = @(0x45, 0x12)
    "T"     = @(0x54, 0x14)
    "ENTER" = @(0x0D, 0x1C)
    "TAB"   = @(0x09, 0x0F)
    "SPACE" = @(0x20, 0x39)
    "1" = @(0x31, 0x02); "2" = @(0x32, 0x03); "3" = @(0x33, 0x04); "4" = @(0x34, 0x05); "5" = @(0x35, 0x06)
    "6" = @(0x36, 0x07); "7" = @(0x37, 0x08); "8" = @(0x38, 0x09); "9" = @(0x39, 0x0A)
}
# Boutons de souris : RDOWN/RUP/LDOWN/LUP, postés au centre de la zone client. GLFW lit
# WM_xBUTTONDOWN/UP dans sa procédure de fenêtre, sans exiger le focus.
$mouse = @{ "RDOWN" = @(0x0204, 0x0002); "RUP" = @(0x0205, 0x0000); "LDOWN" = @(0x0201, 0x0001); "LUP" = @(0x0202, 0x0000); "MOVE" = @(0x0200, 0x0000) }
if (-not $table.ContainsKey($Key.ToUpper()) -and -not $mouse.ContainsKey($Key.ToUpper()) -and $Key.ToUpper() -ne "F3T") { Write-Error "Touche inconnue : $Key"; exit 1 }

Add-Type @"
using System;
using System.Runtime.InteropServices;
public static class McKey {
    [DllImport("user32.dll", SetLastError = true)]
    public static extern bool PostMessage(IntPtr hWnd, uint Msg, IntPtr wParam, IntPtr lParam);
    [StructLayout(LayoutKind.Sequential)] public struct RECT { public int Left, Top, Right, Bottom; }
    [DllImport("user32.dll")] public static extern bool GetClientRect(IntPtr hWnd, out RECT rect);
}
"@
if ($mouse.ContainsKey($Key.ToUpper())) {
    $rect = New-Object McKey+RECT
    [void][McKey]::GetClientRect([IntPtr]$hwnd, [ref]$rect)
    $cx = [int]($rect.Right / 2); $cy = [int]($rect.Bottom / 2)
    if ($X -ge 0) { $cx = $X }; if ($Y -ge 0) { $cy = $Y }
    $lp = [IntPtr](($cy -shl 16) -bor $cx)
    [void][McKey]::PostMessage([IntPtr]$hwnd, $mouse[$Key.ToUpper()][0], [IntPtr]$mouse[$Key.ToUpper()][1], $lp)
    Write-Output "MOUSE $Key -> PID $Pid_ ($($Matches[1])) en $cx,$cy"
    exit 0
}
$WM_KEYDOWN = 0x0100
$WM_KEYUP = 0x0101
# Combinaison F3+T (rechargement des ressources) : F3 enfoncée, T frappée, F3 relâchée.
if ($Key.ToUpper() -eq "F3T") {
    $f3 = $table["F3"]; $t = $table["T"]
    [void][McKey]::PostMessage([IntPtr]$hwnd, $WM_KEYDOWN, [IntPtr]$f3[0], [IntPtr](1 -bor ($f3[1] -shl 16)))
    Start-Sleep -Milliseconds 80
    [void][McKey]::PostMessage([IntPtr]$hwnd, $WM_KEYDOWN, [IntPtr]$t[0], [IntPtr](1 -bor ($t[1] -shl 16)))
    Start-Sleep -Milliseconds 60
    [void][McKey]::PostMessage([IntPtr]$hwnd, $WM_KEYUP, [IntPtr]$t[0], [IntPtr](1 -bor ($t[1] -shl 16) -bor (1 -shl 30) -bor (1 -shl 31)))
    Start-Sleep -Milliseconds 60
    [void][McKey]::PostMessage([IntPtr]$hwnd, $WM_KEYUP, [IntPtr]$f3[0], [IntPtr](1 -bor ($f3[1] -shl 16) -bor (1 -shl 30) -bor (1 -shl 31)))
    Write-Output "KEY F3+T -> PID $Pid_ ($($Matches[1]))"
    exit 0
}
$vk = $table[$Key.ToUpper()][0]
$sc = $table[$Key.ToUpper()][1]
$down = [IntPtr](1 -bor ($sc -shl 16))
$up = [IntPtr](1 -bor ($sc -shl 16) -bor (1 -shl 30) -bor (1 -shl 31))
[void][McKey]::PostMessage([IntPtr]$hwnd, $WM_KEYDOWN, [IntPtr]$vk, $down)
Start-Sleep -Milliseconds 60
[void][McKey]::PostMessage([IntPtr]$hwnd, $WM_KEYUP, [IntPtr]$vk, $up)
Write-Output "KEY $Key -> PID $Pid_ ($($Matches[1]))"
