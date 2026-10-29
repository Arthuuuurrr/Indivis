# PQ02 R1 HF4 — commandes et repères

Minecraft/Fabric **1.21.11**, Java **21**. Installer le core **PQ02 R1 HF1** et le mod de quêtes fusionné **HF12-PQ02-R1-HF4** avant d’utiliser les nouvelles commandes. Aucun nouveau mod compagnon n’est ajouté. Si `hc_quetes` PQ02 R1 est déjà installé, le conserver.

**Passage de HF1/HF2/HF3 à HF4 : remplacer seulement le JAR de quêtes et redémarrer. Les commandes et liaisons déjà enregistrées restent valables.**

## Préparation initiale

| Action | Commande | Où l’exécuter |
| --- | --- | --- |
| Associer le Léovic permanent | `/function capitale:pq02/liaison/leovic` | À moins de 4 blocs de Léovic, à son poste près de A |
| Associer le magistrat permanent | `/function capitale:pq02/liaison/magistrat` | À moins de 4 blocs du magistrat, près de M |
| Lire et mémoriser le parcours existant | `/function capitale:pq02/admin/preparer_parcours` | Depuis n’importe quel point de la même dimension ; aucun armor stand à viser |
| Voir les coordonnées et l’état de chaque repère | `/function capitale:pq02/admin/diagnostic_points` | Dans la dimension du parcours ; lecture seule |
| Voir l’état de son personnage et de sa scène | `/function capitale:pq02/admin/diagnostic` | Avec le personnage concerné |

Les liaisons déjà correctes sont conservées : inutile de les refaire pour appliquer HF1. La préparation ne place aucun nouveau marker, ne déplace aucun waypoint et ne démarre pas la quête. Elle ne donne ni progression, ni réputation, ni argent.

## Points utilisés

**A → B → C → D → E → F → G → H− → H → H+ → I → J → K → L → M**, puis **M → N → O → P**.

| Repère | Fonction |
| --- | --- |
| A | Départ près de Léovic ; stationnement de l’ancien guide invisible |
| D | Première pause de lore sur le quai |
| H− / H / H+ | Approche, seconde pause et sortie ; H−/H+ restent facultatifs |
| M | Entretien avec le magistrat |
| P | Arrivée à l’ascenseur |

Les petites armures nommées A, D, etc. sont uniquement des visualisations. Le déplacement de l’escorte personnelle utilise les vrais `minecraft:marker` portant les tags `wp_leovic_registre_port_*`. Les positions sont lues dans le monde ; aucune coordonnée des captures n’est codée en dur.

## Résultat de la préparation

- **Chargé** : le vrai marker est trouvé dans la dimension ; sa position actuelle prime sur le cache.
- **Mémorisé** : le marker n’est pas chargé mais sa dernière position connue, dans cette dimension, est disponible.
- **Absent** : aucune position exploitable ; parcourir la portion concernée et relancer `preparer_parcours`. Les autres positions restent mémorisées.
- **Ambigu** : plusieurs vrais markers portent le même tag ; la préparation refuse de choisir, de déplacer l’ancien guide ou d’écraser le cache.

La lecture administrative porte sur les entités déjà chargées dans la dimension, une seule fois par commande. Aucun chunk n’est chargé de force et cette recherche n’est pas ajoutée au tick d’escorte.

L’ancien guide invisible, s’il existe et s’il est unique, est stationné sur A seulement après validation du parcours. Si A est uniquement mémorisé, charger sa zone puis relancer. Si une scène PQ02 est active, attendre sa fin pour le stationnement. L’absence d’un ancien guide chargé ne crée pas de PNJ supplémentaire : l’escorte personnelle possède son propre acteur.

## Visualisation et compatibilité

```mcfunction
/function capitale:pq02/admin/afficher_points
/function capitale:pq02/admin/masquer_points
```

Ces commandes réutilisent les fonctions historiques d’affichage/nettoyage du core. Elles conservent les vrais markers.

L’ancienne commande fonctionne toujours :

```mcfunction
/function capitale:pq02/admin/preparer_guide_historique
```

Elle appelle désormais `preparer_parcours` et bénéficie de la même détection automatique. La préparation reste une action d’administration ; l’escorte commence après l’acceptation dans les dialogues de Léovic.

Pour libérer uniquement sa propre scène en conservant sa progression : `/function capitale:pq02/admin/nettoyer_scenes`. Cette commande ne réinitialise pas la quête.
