# Colin — correctif PQ04/PQ05 R1 HF1 — 10/11/2026

Ce correctif s'applique à Minecraft Fabric **1.21.11** et à l'installation **PQ04/PQ05 R1** livrée le 10 novembre. Le seul fichier à remplacer est le mod de quêtes.

## Installation

1. Arrêter le serveur et Minecraft côté client. Conserver l'ancien JAR pour un retour arrière.
2. Dans `mods/` du serveur **et de chaque client**, remplacer `haute-capitale-quetes-PQ04-PQ05-R1-1.21.11.jar` par **`haute-capitale-quetes-PQ04-PQ05-R1-HF1-1.21.11.jar`**. Garder un seul JAR portant l'ID `haute_capitale_quetes`.
3. Garder les deux datapacks R1 déjà installés : `capitale_core_1.5.7_PQ04_PQ05_R1_20261109.zip` et `hc_quetes_FUSION_C1_PQ04_PQ05_R1_20261109.zip`.
4. Redémarrer le serveur et les clients. Un `/reload` seul ne remplace pas le JAR.
5. Se placer à moins de quatre blocs du Colin public, **au poste inférieur**, puis exécuter :

```mcfunction
/function capitale:pq45/liaison/colin
```

La sélection privilégie désormais le Colin reconnu par son nom, ses tags ou ses anciennes commandes. Les PNJ situés à un autre étage ne sont pas comptés comme des voisins à quatre blocs. Une scène de Roch ne bloque plus cette liaison.

Pour cibler exactement le Colin identifié dans les logs fournis, utiliser cette commande administrateur :

```mcfunction
/indivispq45 bind_colin 57cfc655-9020-4a60-9c02-0b96aa3810c7
```

Ce PNJ doit être chargé, public et à moins de quatre blocs de l'administrateur. Si Colin a été supprimé puis recréé, son UUID aura changé : utiliser la fonction de liaison ordinaire ou l'UUID du nouveau PNJ.

Fermer un éventuel ancien dialogue, puis cliquer normalement sur Colin. Pour un personnage arrivé après Roch, le clic doit ouvrir sa séquence PQ04. **Ne pas relancer le prologue et ne pas réinitialiser les scores.** Le correctif ne modifie ni la progression, ni la caisse, ni les paiements.

## Validation sur la carte Indivis

- Après le congé de Roch : cliquer Colin et obtenir son introduction, plutôt que son ancien dialogue générique.
- Choisir le départ vers l'auberge ; vérifier la marche vers les repères Colin A–Z déjà mémorisés.
- Si un repère manque, le message doit le préciser. Vérifier les points avec `/function capitale:pq45/admin/diagnostic_points` ; leur préparation R1 reste `/function capitale:pq45/admin/preparer_parcours`.
- Vérifier que la caisse reste livrable une seule fois et que les autres gardes gardent leurs propres interactions.

Les tests automatisés vérifient le rattachement et l'entrée au clic sur un serveur Fabric/EasyNPC réel, avec un joueur synthétique. Ils ne parcourent pas la carte Indivis et ne constituent pas une validation du rendu de ses dialogues ou de ses obstacles.

## Retour arrière

Arrêter le serveur et les clients, puis remettre le JAR R1 précédent. Les deux datapacks R1 n'ont pas été remplacés. Les données de personnage n'ont subi aucune migration supplémentaire.
