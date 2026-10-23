# PQ01 — Sous pavillon de la Couronne — R3-HF4

Version du 23 octobre 2026 pour Minecraft Java Fabric 1.21.11.

HF4 corrige les acteurs mobiles de Gautier/Perrin, l’identité de l’interlocuteur pendant les interruptions, le conflit du matelot Anselme avec l’archiviste Kerrec et les pages vides. Les répliques successives avancent après lecture et fin de voix ; les réponses du protagoniste et les questions restent manuelles. Le Capitaine commence au clic droit et sa réponse qui lance l’amarrage est verte et explicitement nommée. Les diagnostics techniques ne sont plus des répliques de PNJ.

**Depuis HF3 : remplacer les deux datapacks et le journal.** Le journal HF4 va sur le serveur et les clients. Les liaisons, apparences, points et progressions existants sont conservés ; aucune réinitialisation n’est exécutée par l’installation. Les commandes de pose restent valables. Les autres composants restent installés.

Le [guide complet](docs/INSTALLATION_ET_POSE_PNJ.md) contient les commandes de liaison, placement et déplacement. Le [rapport HF4](HOTFIX_IMMERSION_R3_HF4.md) détaille les changements et preuves. Les dialogues approuvés, les 95 identifiants de nœud, les sauvegardes de relecture, les récompenses et la priorité MQ03 restent conservés.

Validation : monde Fabric 1.21.11 réel avec EasyNPC 7.16.0, quatre trajets parcourus, copie du skin/modèle/équipement et interaction d’acteur vérifiées ; tous les scripts PQ01 chargés par Minecraft. Bancs complémentaires : 80 assertions NBT, 23 sur les interactions, 34 sur la cadence client, 18 cas de marche / 27 assertions, 118 contrôles de membres publics, vrai dispatcher EasyNPC et vraie classification verte de Dialogue b10. Les mixins historiques sont identiques à HF3.

**Le rendu graphique, la caméra et le son dans le modpack complet ne sont pas validés ici.** Le test de monde utilise un sol plat et double la sélection Nexus et l’émission des paquets de visibilité. Aucun monde utilisateur n’a été ouvert. PR [#177](https://github.com/Arthuuuurrr/Indivis/pull/177) conservée en brouillon, sans merge ni déploiement.
