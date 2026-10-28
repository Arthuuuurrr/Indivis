# PQ02 R1 HF2 — Léovic : correction de l’ouverture du dialogue

Minecraft/Fabric **1.21.11**, Java **21**. Livraison du **28 octobre 2026**.

## Installation sur une installation HF1 existante

1. Arrêter le serveur et fermer Minecraft sur les clients.
2. Remplacer le JAR de quêtes précédent par **`haute-capitale-quetes-HF12-PQ02-R1-HF2-1.21.11.jar`**, dans `mods/` du serveur et de chaque client. Garder un seul JAR de quêtes ; le compagnon `indivis-pq02` reste exclu.
3. Conserver le core **PQ02 R1 HF1**, `hc_quetes` **PQ02 R1**, les PNJ, les repères, les liaisons et les données Nexus. Les deux datapacks sont identiques à ceux de HF1. Les autres mods ne sont pas modifiés.
4. Redémarrer complètement et reprendre l’interaction normale avec Léovic. Aucune commande de placement, de préparation, de remise à zéro ou d’achèvement n’est nécessaire pour appliquer HF2.

## Cause identifiée et correction

Les logs fournis montrent **PQ01=100, PQ02=30, verrou=0**, les 18 repères reconnus, puis l’exception **`Copie EasyNPC non créée : leovic`** au premier clic.

Le problème est reproduit avec le JAR HF1 exact : une fonction Minecraft appelle le runtime PQ02, qui demande la création du PNJ avec une commande imbriquée. Cette commande est mise en attente par Minecraft ; le code cherche la copie avant l’exécution de sa création. Les anciens tests entraient directement dans le moteur narratif et ne couvraient pas cette entrée par fonction.

HF2 traite l’ouverture à la fin du tick serveur, après le retour de la fonction. La requête est limitée à une par compte et vérifie de nouveau le personnage, la dimension, l’étape et la session. Déconnexion, sauvegarde/changement de personnage, verrouillage, changement d’étape ou fermeture de la caméra annulent une requête devenue incompatible. Aucun waypoint, texte narratif ou montant de récompense n’est réécrit.

## Vérifications

Le cas d’échec HF1 a été reproduit sur serveur jetable. Le correctif est ensuite exercé par le véritable gestionnaire de commandes EasyNPC et les fonctions du datapack, avec vérification du premier dialogue, des requêtes répétées, des annulations et de l’entretien avec le magistrat. Les contrôles précédents de narration, escorte, isolation, récompenses, PQ01 et MQ03 sont relancés. Les preuves détaillées figurent dans `verification/hf2.json` et les rapports de la livraison complète.

Les connexions et la sélection Nexus du banc restent synthétiques. Les appels économiques externes sont remplacés seulement dans le banc. Aucun client graphique, parcours sur la carte Indivis, FPS ni doublage réel n’a été testé ici.
