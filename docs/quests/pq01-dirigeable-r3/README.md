# PQ01 — Sous pavillon de la Couronne — R3-HF3

**Version actuelle : journal R3-HF3, 23 octobre 2026.** Corrige le raccord entre les actions natives EasyNPC de HF2 et l’ouverture de la caméra Dialogue b10. [Cause, installation et contrôles HF3](HOTFIX_DIALOGUE_R3_HF3.md).

**Mise à jour depuis HF2 : remplacer uniquement le JAR journal sur le serveur et les clients, puis redémarrer.** Les deux datapacks restent en R3 ; le mod admin reste en alpha.3. Les liaisons réussies restent utilisables. Les corrections NBT/actions HF2 et les annotations Mixin HF1 sont conservées. Aucun Nexus inchangé n’est redistribué.

Minecraft Java Fabric 1.21.11. Cette version conserve les dialogues approuvés, l’aller/retour individuel de Gautier vers Géraud, les occupations des matelots, les placements EasyNPC et la relecture PQ01 par personnage. Les 95 nœuds reprennent les 145 passages/questions approuvés.

Le [guide complet de pose](docs/INSTALLATION_ET_POSE_PNJ.md) contient toutes les commandes de liaison, placement et marche, ainsi que la distinction entre baguette d’édition et clic de dialogue. [Tests de relecture en jeu](docs/TESTS_EN_JEU.md). [Historique HF2](HOTFIX_NBT_R3_HF2.md).

Validation HF3 : 23 assertions sur le pont de production avec contexte de monde/Nexus/caméra doublé ; 3 assertions sur le vrai dispatcher EasyNPC avec initiateur non nul ; 80 assertions NBT/actions ; 109 recherches de membres publics ; 17 cas de marche, 26 assertions ; bytecode et inventaire binaire contrôlés. **Aucun serveur Fabric complet ni monde du joueur lancé.** Le rendu caméra et les paquets de clic restent à vérifier en jeu. Aucun déploiement ni merge effectué.
