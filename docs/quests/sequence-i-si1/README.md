# Indivis — Séquence I — SI1a — 20 octobre 2026

La Séquence I est intégrée aux deux datapacks fournis : MQ01 La Convocation, MQ02 Les Registres,
MQ03 Le Journal de bord, puis l'ouverture de MQ04 Havre-Fort et son premier objectif.
Les 66 quêtes historiques sont migrées vers les états dédiés de la norme v2. La base du core est
la 1.5.7 fournie, avec restauration de dépendances manquantes depuis la référence historique.
Ce lot constitue donc une version modifiée de la 1.5.7, avec journal et Nexus adaptés.

**Statut : candidat intégré pour recette sur une copie du serveur.** Les validations statiques et
les bancs sont exécutés ; aucun serveur Minecraft/Fabric ni monde de jeu n'était disponible ici.
La jouabilité, le chargement réel des commandes/NBT et les performances restent à valider en jeu.

## Installation

Pour une installation SI1 existante, lire d’abord `docs/CORRECTIF_SI1a.md` : seuls le journal et le pack de quêtes changent. Le core et Nexus restent identiques.

`installation/datapacks/` contient les deux ZIP à installer ensemble dans le monde de test.
`installation/mods/` contient les deux JAR à remplacer ensemble, côté serveur et clients.
Le ZIP de livraison lui-même ne doit pas être placé comme datapack.

Lire `docs/INSTALLATION_ET_PLACEMENT.md` : fonctions individuelles pour le Scribe, l'Archivaire,
les deux Sentinelles, liaison d'Althéon, volume des Archives et cible du port. Aucune coordonnée
de carte n'est inventée. Les apparences restent configurables dans EasyNPC.

`tools/install_candidate.py` affiche d'abord un plan sans écriture ; `--apply` installe et sauvegarde
les anciens fichiers, après arrêt du serveur/jeu et sauvegarde du monde et des profils Nexus.

## Contenu technique

- Dialogues complets, choix répétables, convocation nominative unique et progression par personnage.
- Messager privé en villes impériales admissibles ; `!/?`, journal et cible Xaero raccordés.
- Droits des Archives par source ; retrait des seules permissions temporaires de la quête.
- Sentinelles créées depuis un garde du Cœur opérationnel, avec données EasyNPC et fonctions de contrôle.
- Import unique des anciens états ; conservation des 33 contenus de livres du datapack de quêtes.

Les journaux de tests sont dans `audit/`. Les registres sont dans `docs/quests/`.
`docs/CHANGEMENTS.md`, les listes de chemins, les diffs et les SHA-256 détaillent les modifications.
Les scripts et sources de reconstruction sont dans `sources/` ; leurs anciens fichiers « Lot01 »
servent uniquement de données d'entrée du générateur et ne sont pas à installer.
