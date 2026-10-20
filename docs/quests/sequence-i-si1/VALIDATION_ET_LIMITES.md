# Vérifications effectuées et limites

Statut : **candidat intégré, à recetter en jeu**. Aucun monde ni serveur de production n'a été modifié.

| Catégorie | Résultat | Portée |
|---|---|---|
| Statique — norme v2 | 70 fiches, 0 erreur | Validateur fourni avec la norme |
| Statique — intégrité et références | 7006 contrôles réussis | JSON, fonctions, prédicats, scores, items, livres, JAR |
| Statique — équivalence des états | 6973 cas réussis | Tous les états historiques effectivement écrits dans les sources |
| Banc — fonctions natives | 13 tests réussis | Droits, dialogues, sessions périmées, inventaire plein, indépendance de deux joueurs |
| Banc — installateur | 4 tests réussis | Plan sans écriture, sauvegarde, intégrité et annulation en cas de copie interrompue |
| Banc — Java livré | BRIDGE_BENCH assertions=8193 legacy_visibility_truth_cases=8160 errors=0; endpoints Minecraft/Nexus doublés | Doubles des points d'API Minecraft/Nexus ; classes d'adaptation réelles |
| Statique — bytecode | ASM_BASIC_VERIFIER classes=11 methods=153 errors=0 (sans résolution des API Minecraft) | Pas de résolution des API Minecraft |
| Banc — NBT livré | NBT_GUARD_BENCH assertions=22 errors=0; API NBT/entités doublées, chargement Minecraft non testé | Contrats Yarn vérifiés ; doubles Minecraft/EasyNPC |
| Reproduction du défaut précédent | Échec attendu confirmé sur le JAR SI1 | Lecture non destructive ; trace jointe |
| Serveur Fabric du correctif | **Non effectué** | Chargement, NBT, PNJ, jeu multijoueur, Xaero, sauvegardes réelles et performances |

Les tests natifs interprètent le sous-ensemble des commandes utilisé dans les accès et les transitions des
dialogues ; les effets du monde, du rendu, du Nexus et de l'inventaire y sont des doubles explicites.
Ils exécutent les véritables lignes livrées, sans représenter un serveur Minecraft.
Le banc Java exécute les classes ajoutées aux JAR avec de petits doubles d'API. Il vérifie notamment
le changement A→B→A, la visibilité privée, la persistance des nouveaux préfixes, le remappage du journal
et les anciennes règles de visibilité. Il ne teste pas une sauvegarde Nexus réelle.

L'équivalence des prédicats porte sur le domaine des valeurs présentes dans les sources ; une valeur
historique forcée hors de ce domaine doit être examinée sur la copie du serveur avant import.
Les identifiants de 64 objets sont migrés. Les livres sont comparés
au niveau de leur composant `written_book_content`, y compris son texte exact.

Les traces brutes, diffs, entrées SHA-256 et sources sont joints. La liste de recette restante figure dans
`RECETTE_ROLLBACK.md`. Une validation en production n'est pas affirmée.
