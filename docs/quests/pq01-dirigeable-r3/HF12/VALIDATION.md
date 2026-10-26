# Vérifications HF12 / QMARK2 — 26 octobre 2026

- PASS 233 QMARK2 checks
- PASS 176 quay migration checks
- PASS 26 checks
- PASS 48 multiplayer checks
- PASS 12 movement regression checks

- Le rejet d'Althéon au quai est reproduit sur le JAR HF11 exact, puis corrigé sur HF12 avec les mêmes anciennes actions.
- Fonctions de liaison/pose/libération exécutées avec l'initialisation de production, `function-permission-level=2`, les vrais objets EasyNPC et leurs actions. Contrôles avant/après `/reload`, conservation des autres actions et restauration exacte ; un autre rôle et plusieurs déclencheurs restent refusés sans écriture.
- Marqueurs : appels du tick de production sans contexte Séquence I ; 24 disponibilités, 20 donneurs, villageois Greffier reconnu par action puis nom coloré, délais, escorte et rangs ; propriétaires/observateur avec vrais trackers et paquets d'apparition/destruction ; UUID stable, déplacement, changement de personnage, déconnexion et nettoyage.
- Graphe des dialogues et scène couplée, marche réelle de Gautier et Perrin, obstacle, joueurs simultanés et sources très lentes contrôlés par les bancs de régression existants.
- Vérifications NBT, API natives, clics, capture Dialogue b10, couleurs et paramètres de navigation ; détails dans `audit/`.
- Comparaison octet par octet : scènes et fonctions du prologue identiques au core HF9 ; fonctions MQ/Séquence I, gardes et 25 biomes conservés. Dans `hc_quetes`, seule la description change par rapport à HF4 : l'ajout de tick QMARK1 est retiré.
- Le JAR HF12 change seulement `Pq01Actions`, `Pq01Bridge`, le nouveau module `CoreQuestMarkers`, son catalogue et les métadonnées. Toutes les classes de déplacement, de dialogue client, de visibilité, tous les mixins et les helpers historiques restent identiques à HF11.

## Limites

- Identités Nexus, connexions et réception de chunks synthétiques dans les bancs ; vrais objets et fonctions Minecraft, vraies entités EasyNPC et trackers réseau.
- Aucun rendu client graphique, doublage écouté ou mesure de FPS ; carte complète du serveur utilisateur non disponible.
- La cause initiale exacte du marqueur absent dans la capture ne peut pas être déduite du log seul ; le nouveau diagnostic expose les conditions réelles.
- Le parcours SI1 complet sur la carte utilisateur n’a pas été effectué ; fonctions, gardes et biomes préservés octet par octet.

Les logs de banc inclus sont synthétiques et ne contiennent pas les journaux privés transmis par l'utilisateur. Les résultats ne prouvent pas un rendu graphique ou un gain de FPS sur son modpack.

## Reconstruction

```bash
python3 work/pq01_full/build.py
CORE_QUEST_MARKERS=1 python3 work/pq01_full/run_world_test.py
PQ01_QUAI_COMMANDS=1 python3 work/pq01_full/run_world_test.py
python3 work/pq01_full/run_world_test.py
PQ01_MULTIPLAYER=1 python3 work/pq01_full/run_world_test.py
PQ01_WALK_REGRESSION=1 python3 work/pq01_full/run_world_test.py
python3 work/pq01_full/verify_hf12.py
python3 work/pq01_full/release_hf12.py
```

Le contrôle avant correctif utilise le JAR HF11 exact avec `PQ01_JAR` et `PQ01_TEST_LABEL=quai-hf11-regression`. Les bancs de serveur s'exécutent successivement car ils emploient le même port. Les scripts de construction historiques sont rangés dans `tools/pq01_full/` sur GitHub ; le répertoire de travail local reste `work/pq01_full/`.
