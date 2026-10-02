#!/bin/bash
# Banc d'essai multijoueur : serveur de dev + deux clients détachés (Alice, Bob), pilotés par RCON.
# Aucune touche n'est injectée : les clients reçoivent leurs ordres du serveur (DevTools).
#
# Prérequis : jar compilé (./gradlew build), run/server.properties (RCON hcdtest:25575).
# Usage : bash tools/banc-multi.sh   → résultat dans banc-multi.log
set -u
R=/c/Users/denne/haute-capitale-dialogue
LOG=$R/banc-multi.log
SLOG=$R/run/logs/latest.log
: > "$LOG"

log() { echo "[$(date +%H:%M:%S)] $*" | tee -a "$LOG"; }
rcon() { powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$R/tools/rcon.ps1" -File "$R/tools/$1" 2>&1 | tee -a "$LOG"; }
attendre() { # attendre <regex> <secondes> <fichier>
  local n=0
  until grep -qE "$1" "$3" 2>/dev/null; do sleep 2; n=$((n+2)); if [ "$n" -ge "$2" ]; then log "DELAI dépassé en attendant « $1 »"; return 1; fi; done
  return 0
}

log "=== serveur"
rm -f "$SLOG"
# Le banc bascule l'exclusivite a mi-parcours : on repart toujours de la valeur par defaut.
[ -f "$R/run/config/haute_capitale_dialogue.json" ] && perl -pi -e 's/"exclusif_par_defaut": true/"exclusif_par_defaut": false/' "$R/run/config/haute_capitale_dialogue.json"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$R/tools/server-detached.ps1" | tee -a "$LOG"
attendre 'Done \(' 300 "$SLOG" || exit 1
log "serveur prêt"

log "=== client 1 (Alice)"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$R/tools/client-detached.ps1" -N 1 -Username Alice | tee -a "$LOG"
attendre 'Alice joined the game' 300 "$SLOG" || exit 1
log "=== client 2 (Bob)"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$R/tools/client-detached.ps1" -N 2 -Username Bob | tee -a "$LOG"
attendre 'Bob joined the game' 300 "$SLOG" || exit 1
sleep 8

log "=== 01 mise en place"; rcon banc/01-setup.txt; sleep 4
log "=== 02 ouverture des deux dialogues"; rcon banc/02-ouvrir.txt; sleep 4
log "=== 03 vérification, Alice choisit"; rcon banc/03-verifier.txt; sleep 4
log "=== 04 nœud suivant, dégâts bloqués, fermeture"; rcon banc/04-noeud.txt; sleep 4
log "=== 05 après fermeture, dégâts autorisés"; rcon banc/05-apres.txt; sleep 2

log "=== exclusivité : activation dans la config puis rechargement"
perl -pi -e 's/"exclusif_par_defaut": false/"exclusif_par_defaut": true/' "$R/run/config/haute_capitale_dialogue.json"
printf 'dialogue recharger\n' > "$R/tools/banc/recharger.txt"; rcon banc/recharger.txt; sleep 2
log "=== 06 Alice ouvre"; rcon banc/06-exclusif.txt; sleep 4
log "=== 07 Bob tente"; rcon banc/07-exclusif2.txt; sleep 4
log "=== 08 sessions (attendu : 1), kick Alice"; rcon banc/08-exclusif3.txt; sleep 4
log "=== 09 fin"; rcon banc/09-fin.txt; sleep 2

log "=== capture des dialogues de chat (serveur dédié, vrai réseau)"
log "=== 10 Roch de test (tellraw + [Choix] /trigger)"; rcon banc/10-capture.txt; sleep 3
log "=== 11 Bob clique"; rcon banc/11-capture2.txt; sleep 4
log "=== 12 état attendu : mode=CAPTURE repliques=1 choix=2 ; Bob choisit"; rcon banc/12-capture3.txt; sleep 3
log "=== 13 score attendu 1, choix=0 ; Bob ferme"; rcon banc/13-capture4.txt; sleep 3
log "=== 14 sessions attendues : 0"; rcon banc/14-capture5.txt; sleep 2

log "=== journal serveur : réponses des clients et sessions"
grep -E "hcd-banc|Session ouverte|Session fermée|déjà en conversation|left the game|Blocked|\[capture\]" "$SLOG" | sed 's/^.*\] //' | tee -a "$LOG"
log "=== journal client Bob : message d'exclusivité (attendu), et aucun écran d'Easy NPC"
grep -E "déjà en conversation|already talking|DialogScreenWrapper" "$R/client2.log" | sed 's/^.*\] //' | tail -5 | tee -a "$LOG"

log "=== arrêt"
printf 'stop\n' > "$R/tools/banc/stop.txt"; rcon banc/stop.txt
sleep 5
log "fin du banc"

# Les clients de test sont arretes par leur ligne de commande (chemin du projet + devlaunchinjector),
# jamais par le titre de leur fenetre : la vraie partie du joueur tourne sur la meme machine.
powershell.exe -NoProfile -Command 'Get-CimInstance Win32_Process -Filter "Name = ''java.exe'' OR Name = ''javaw.exe''" | Where-Object { $_.CommandLine -and $_.CommandLine -like "*haute-capitale-dialogue*" -and $_.CommandLine -like "*devlaunchinjector*" } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force; "client arrete pid $($_.ProcessId)" }' | tee -a "$LOG"
