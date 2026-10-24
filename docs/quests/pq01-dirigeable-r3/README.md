# PQ01 — Blocs de lecture, orientation et doublage — R3-HF9

24 octobre 2026 — Minecraft Fabric 1.21.11, EasyNPC 7.16.0 et Dialogue b10.

Les répliques consécutives d’un même PNJ sont regroupées. Aucun texte ne disparaît à la fin de sa frappe : le bloc et ses pages restent affichés jusqu’à Espace, Entrée ou un clic du joueur. Les véritables réponses restent des choix, sans ligne fictive « Continuer » dans la bouche du protagoniste.

Géraud regarde le joueur au début. Il ne se tourne vers Gautier que pendant l’interruption. « Matelot ! » reste avec la remontrance, au même endroit et sans temporisation qui l’efface.

Perrin conserve sa ronde et ses points. Le correctif récupère aussi une vitesse de base nulle héritée d’un PNJ décoratif, seulement pour l’acteur temporaire. Son état propre sur la carte reste à établir : une commande de diagnostic ciblée est incluse.

**Remplacer le core sur le serveur et le journal sur le serveur et chaque client. Conserver `hc_quetes` HF4. Aucun PNJ, point ou liaison à replacer.**

- [Installation et commandes complètes](docs/INSTALLATION_ET_POSE_PNJ.md)
- [Changements, vérifications et limites](HOTFIX_BLOCS_ORIENTATION_R3_HF9.md)
- [Recette en jeu](docs/TESTS_EN_JEU.md)
- [Intégration future des doublages](docs/DOUBLAGE_BLOCS.md)
- [Correspondance blocs et prises](config/BLOCS_ET_PRISES.json)

Les 92 prises et leurs identifiants ne changent pas. Une file serveur joue les prises d’un bloc dans l’ordre, attend leurs durées et annule le reste à la fermeture. Aucun audio nouveau ni nouveau JAR Indivis Voices n’est livré.

Le banc valide fonctions, contrôleurs et navigation réels ; le rendu et la géométrie de la carte utilisateur restent hors du banc. [GitHub nº177](https://github.com/Arthuuuurrr/Indivis/pull/177).
