# Prologue et quêtes du core — HF12 / QMARK2

26 octobre 2026 — Minecraft Fabric 1.21.11, EasyNPC 7.16.0, Dialogue b10.

HF12 reconnaît les anciennes actions du capitaine au quai et des quatre matelots, sans perdre leurs autres actions. QMARK2 rend l'actualisation des « ! » indépendante du tick de la Séquence I et reconnaît les donneurs par leurs actions, tags et noms normalisés. Les 24 règles d'offre du core sont conservées.

Installer les trois composants fournis : journal HF12 sur serveur et clients, core HF9_QMARK2 et hc_quetes HF4_QMARK2 côté serveur. Les liaisons et points existants sont réutilisés ; relancer uniquement les liaisons refusées.

- [Installation et fichiers exacts](HF12/README.md)
- [Commandes des six PNJ au quai](HF12/COMMANDES_QUAI.md)
- [Guide complet de pose et de déplacement](HF12/INSTALLATION_ET_POSE_PNJ.md)
- [Marqueurs et diagnostic des conditions réelles](HF12/GUIDE_REPERES.md)
- [Vérifications et limites](HF12/VALIDATION.md)

Validation : 233 contrôles de marqueurs, 176 de liaison après démarrage/reload, 1161 d'enchaînement, 25 de scène couplée, 15 d'ambiance, 26 de navigation, 48 multijoueurs et 12 de régression sur les sources lentes. API, NBT et capture b10 également vérifiés. Les 802 fonctions PQ01 du core, gardes, 25 biomes, fonctions MQ/SI1, mixins, visibilité et classes de dialogue client sont conservés octet par octet par rapport aux bases documentées. Le rendu graphique et les FPS du modpack utilisateur ne sont pas mesurés.

Les blocs manuels, questions réouvrables, conclusion verte, préparation des doublages, marche et filtres privés des correctifs antérieurs sont conservés. [Historique de marche HF11](HOTFIX_MARCHE_R3_HF11.md) · [PR nº177](https://github.com/Arthuuuurrr/Indivis/pull/177).
