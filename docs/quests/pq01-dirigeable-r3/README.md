# PQ01 — marche de Gautier — R3-HF11

25 octobre 2026 — Minecraft Fabric 1.21.11, EasyNPC 7.16.0, Dialogue b10.

**Remplacer uniquement le JAR de quêtes/journal sur le serveur et chaque client. Conserver le core HF9 et `hc_quetes` HF4. Aucun PNJ, point, liaison ou progression à réinitialiser.**

HF11 corrige le cas d’une vitesse de décor faible mais positive, qui n’était pas traité par la correction des vitesses nulles. Le plancher s’applique uniquement aux copies mobiles de Gautier/Perrin. Gautier utilise une allure de navigation normale et conserve le même acteur pendant son approche et son retour. Les originaux, dialogues manuels, hooks de doublage et filtres personnels HF10 sont conservés.

- [Installation et commandes complètes](docs/INSTALLATION_ET_POSE_PNJ.md)
- [Défaut reproduit, changements et limites](HOTFIX_MARCHE_R3_HF11.md)
- [Recette en jeu](docs/TESTS_EN_JEU.md)

La livraison contient le seul JAR modifié et les guides/preuves. Aucun datapack ni Nexus inchangé redistribué. Le test vérifie marche, scène et suivi réseau ; la géométrie et la vitesse de la carte utilisateur ne sont pas présentes dans les fichiers reçus.

[Suivi GitHub nº177](https://github.com/Arthuuuurrr/Indivis/pull/177).
