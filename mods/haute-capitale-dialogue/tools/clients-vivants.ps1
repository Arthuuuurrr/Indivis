# Liste les clients/serveurs de dev de CE projet encore en vie (par ligne de commande, jamais par titre de fenetre).
Get-CimInstance Win32_Process -Filter "Name = 'java.exe' OR Name = 'javaw.exe'" |
    Where-Object { $_.CommandLine -and $_.CommandLine -like "*haute-capitale-dialogue*" -and $_.CommandLine -like "*devlaunchinjector*" } |
    ForEach-Object { "pid $($_.ProcessId) : " + $_.CommandLine.Substring(0, [Math]::Min(160, $_.CommandLine.Length)) }
