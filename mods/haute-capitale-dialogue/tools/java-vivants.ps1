# Liste les processus Java de la machine avec un extrait de leur ligne de commande (lecture seule).
Get-CimInstance Win32_Process -Filter "Name = 'java.exe' OR Name = 'javaw.exe'" |
    ForEach-Object {
        $cl = $_.CommandLine
        if (-not $cl) { $cl = "(ligne de commande inaccessible)" }
        $debut = [Math]::Max(0, $cl.Length - 200)
        "pid $($_.ProcessId) depuis $($_.CreationDate.ToString('HH:mm:ss')) : ..." + $cl.Substring($debut)
    }
