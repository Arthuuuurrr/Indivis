# Indivis NPC Restore 1.0.1 — Prologue resilient

Correctif du datapack 1.0.0 après test réel avec EasyNPC 7.12.1.

## Problème constaté

`restore_story` 1.0.0 lançait plusieurs `easy_npc spawn <uuid>` dans le même appel.
Une exception Java sur la première entrée (Althéon) pouvait interrompre toute la fonction avant
Géraud et les entrées suivantes.

## 1.0.1

Ajoute une restauration ciblée du prologue :
- Althéon Brumeforge — UUID original `efb57def-1e1b-430a-86d6-5df7b8cfcac2`
- Géraud Rivet — UUID original `3e9353d9-e290-4929-80eb-4d940c1c4528`

Chaque tentative est exécutée dans une fonction planifiée distincte. La tentative suivante est
planifiée **avant** l'appel EasyNPC afin qu'une exception sur un NPC ne bloque pas l'autre.

Fonctions :
- `indivis_npc_restore:restore_prologue`
- `indivis_npc_restore:test_altheon`
- `indivis_npc_restore:test_geraud`

La restauration large 1.0.0 n'est plus recommandée tant que les NPC réellement absents n'ont pas
été distingués des NPC simplement déchargés.
