# PQ01 — scènes personnelles et charge multijoueur — R3-HF10

**Framerate : régression signalée le 24 octobre 2026, cause encore non identifiée.** Le profil reçu contient uniquement le thread serveur (environ 20 TPS, MSPT médian 9,62 ms), sans rendu client. Les tests HF10 ne valident donc pas les FPS. [Analyse et capture client nécessaire](audit/SPARK_GiYiuD2gYw_2026-10-24.md).

24 octobre 2026 — Minecraft Fabric 1.21.11.

**Remplacer uniquement le JAR de quêtes/journal sur le serveur et chaque client. Conserver le core HF9 et le datapack `hc_quetes` HF4 déjà installés. Aucun PNJ, point, liaison ou progression à réinitialiser.**

Le filtre refuse les copies PQ01 des autres joueurs avant les contrôles historiques. Les rafraîchissements de visibilité sont regroupés par joueur et par tick. Gautier n’a plus de copie d’ambiance inactive : sa copie est créée pour les scènes qui l’utilisent. Les blocs de dialogue et les hooks de doublage HF9 sont conservés.

La livraison contient le seul JAR changé, le guide complet de pose/configuration, la recette et les résultats des vérifications. Aucun Nexus inchangé n’est redistribué.

- [Installation et commandes complètes](docs/INSTALLATION_ET_POSE_PNJ.md)
- [Changements, constats dans les logs et limites](HOTFIX_MULTIJOUEUR_R3_HF10.md)
- [Recette en jeu](docs/TESTS_EN_JEU.md)
- [Préparation du doublage conservée](https://github.com/Arthuuuurrr/Indivis/blob/feat/pq01-dirigeable-r2-20261022/docs/quests/pq01-dirigeable-r3/docs/DOUBLAGE_BLOCS.md)

Le test utilise deux propriétaires simultanés, un observateur et un arrivant tardif. Le suivi réseau Minecraft et les paquets sont réels ; les connexions et profils Nexus sont synthétiques. Aucun FPS de client graphique n’est mesuré. Perrin marche dans ce test ; ses points et obstacles propres à votre carte restent hors du banc.

[Suivi GitHub nº177](https://github.com/Arthuuuurrr/Indivis/pull/177).
