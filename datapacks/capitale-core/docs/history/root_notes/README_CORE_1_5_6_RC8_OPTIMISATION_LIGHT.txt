Capitale Core 1.5.6 RC8 — optimisation légère serveur

Base : RC7 access pass warning/crime fix.
Objectif : réduire le coût permanent du datapack sans modifier les mécaniques de jeu.

Changements :
- Anti-spawn hostile : nettoyage automatique toutes les 5 secondes au lieu de chaque seconde.
- Anti-spawn hostile : le nettoyage de marker ne s’exécute que si un joueur est à proximité de la zone.
- Anti-spawn hostile : évite le balayage complet si aucun hostile naturel ciblé n’est présent.
- Protection de zones : le préfiltre joueur autour des markers passe de distance=..5000 à distance=..1000, tout en couvrant les zones R800 existantes.
- Gardes statiques defend/reset : timer phasé toutes les 5 ticks ; le timer baisse de 5 pour conserver un retour autour de 30 secondes.
- Ajout de l’objectif CAP_PERF_TICK pour les horloges légères d’optimisation.

Non changé :
- pas de changement des patrouilles ;
- pas de changement des accès, criminalité, quêtes, monnaie, vendeurs, profils ou CapSkills ;
- pas de NoAI automatique sur les PNJ ;
- pas de suppression de fonctions EasyNPC ou entrypoints manuels.
