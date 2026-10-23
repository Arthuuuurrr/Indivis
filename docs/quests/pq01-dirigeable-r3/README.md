# PQ01 — Sous pavillon de la Couronne — R3-HF2

**Version actuelle : journal R3-HF2, 23 octobre 2026.** Corrige les copies de listes/noms NBT, la migration des liaisons multiples avec un hook connu et les déclencheurs EasyNPC. [Installation, preuves et limites HF2](HOTFIX_NBT_R3_HF2.md). Les datapacks restent en R3 et le mod admin en alpha.3. Le correctif des annotations Mixin HF1 est conservé.

Minecraft Java Fabric 1.21.11 — 22 octobre 2026.

Cette livraison intègre les dialogues approuvés, l’aller/retour individuel de Gautier vers Géraud, les occupations des matelots, les liaisons et placements individuels EasyNPC et la relecture de PQ01 par personnage.

**Trois composants à remplacer : core, datapack de quêtes et mod journal.** Le journal R3-HF2 s’installe sur le serveur **et sur les clients**. Les fichiers sont dans `installation/`. Nexus, Dialogue et EasyNPC fournis restent utilisés ; aucun Nexus inchangé n’est redistribué.

Lire **[INSTALLATION_ET_POSE_PNJ.md](docs/INSTALLATION_ET_POSE_PNJ.md)** : liaison, déplacement physique et points de marche sont trois opérations distinctes, avec toutes les commandes exactes. Puis **[TESTS_EN_JEU.md](docs/TESTS_EN_JEU.md)** pour rejouer sans récompense ni lancement de PQ02.

Les 95 nœuds reprennent les 145 passages/questions approuvés. Les choix et sous-questions sont répétables et fermables ; l’amnésie précède la conclusion d’Althéon. L’amarrage historique et les récompenses normales sont conservés. Visiteur reste attribué chez le magistrat.

Validation exécutée : 12 contrôles des datapacks, 17 cas du contrôleur de marche compilé, 44 assertions sur la politique du Nexus fourni, vérification du bytecode. **Aucun serveur Minecraft/Fabric ni monde n’a été lancé ici.** La navigation dans le décor, le rendu des PNJ, la caméra et les scénarios à deux joueurs restent à recetter sur une copie du monde.

Inventaire exact : `audit/DIFFS_DEPUIS_SI1_EXHAUSTIFS.csv`. Preuves et limites : `docs/VALIDATION_ET_LIMITES.md`. Les sources et le prompt sont inclus dans `sources/`. Aucun déploiement ni merge effectué.
