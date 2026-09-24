# Retrouve les clients de test de CE projet, et eux seuls.
#
# Ne jamais chercher une fenêtre Minecraft par son titre : la machine de développement fait aussi
# tourner la vraie partie du joueur, et un script qui se trompe de fenêtre lui vole le focus ou,
# pire, lui envoie des touches. On filtre donc sur la ligne de commande du processus, qui contient
# le chemin du projet.
param([string]$Root = "C:\Users\denne\haute-capitale-metiers")

Get-CimInstance Win32_Process -Filter "Name = 'javaw.exe' OR Name = 'java.exe'" |
    Where-Object { $_.CommandLine -and $_.CommandLine -like "*$Root*" -and
        ($_.CommandLine -like "*net.fabricmc.devlaunchinjector*" -or $_.CommandLine -like "*--gameDir*$Root\packclient*") } |
    ForEach-Object {
        $proc = Get-Process -Id $_.ProcessId -ErrorAction SilentlyContinue
        [PSCustomObject]@{
            Id     = $_.ProcessId
            Window = if ($proc) { $proc.MainWindowTitle } else { "" }
            User   = if ($_.CommandLine -match '--username\s+(\S+)') { $Matches[1] } else { "?" }
        }
    }
