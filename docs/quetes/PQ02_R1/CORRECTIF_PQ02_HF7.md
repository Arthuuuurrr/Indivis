# Indivis — PQ02 HF7 : départ et reprise de l'escorte

31 octobre 2026 — Minecraft/Fabric 1.21.11, Java 21, Dialogue b10, EasyNPC 7.16.0, Nexus PRE35-SI1-OPT3.

## Installation depuis HF6

1. Arrêter le serveur et fermer Minecraft sur les clients.
2. Remplacer uniquement le JAR de quêtes par `haute-capitale-quetes-HF12-PQ02-R1-HF7-1.21.11.jar`, dans `mods/` sur le serveur et les clients. Conserver un seul JAR de quêtes.
3. Redémarrer complètement et reparler à Léovic. Si le personnage était arrêté sur sa réponse « Suivez-moi », utiliser l'action verte « Suivre Léovic ».

Aucune commande, remise à zéro, liaison ou pose de PNJ/repère à refaire. Conserver les deux datapacks déjà installés et les autres mods. Le correctif de sauvegarde Nexus de HF6 est conservé.

## Défaut constaté dans les nouveaux logs

Les deux logs fournis chargent bien HF6. Le dialogue suit `a01 → a02a → a07 → a06 → a06b → a08 → a09`. Le joueur a choisi « Oui. Allons-y. ». À 14:08:03 côté client (13:08:03 côté serveur), le clic suivant envoie `Pq02Choix=1279188191`, soit le choix 15 de la page `a09` (base 1279188176).

HF6 affichait uniquement « Fermer la conversation » sur cette dernière réponse. Le départ était attaché au choix technique 14, utilisé par l'avance de lecture. Le choix visible 15 supprimait la scène d'introduction, avant tout départ. Le PNJ n'atteignait donc même pas la première étape de navigation. Le nouveau banc rejoue exactement les choix relevés, avec le binaire HF6 d'origine, et reproduit cette suppression.

## Correction commune aux transitions

- Les fins de bloc qui portent une action affichent cette action en vert : suivre Léovic, reprendre la route, rejoindre l'ascenseur, confirmer l'inscription, accepter la bourse, revenir aux questions, terminer l'entretien ou se reposer. Le bouton déclenche la véritable transition ; les répliques du scénario restent inchangées.
- Le choix « Oui. Allons-y. » et les choix menant à une reprise de marche sont signalés en vert.
- Après un départ déjà accepté, fermer la dernière réponse avec Échap ou l'ancien bouton de fermeture conserve ce départ. La même règle s'applique à la reprise du trajet et aux trois réponses précédant le court parcours vers l'ascenseur.
- Pendant une question facultative, Léovic reste immobile. Une fermeture explicite reprend depuis le waypoint suspendu. Mort, déconnexion, remplacement par une autre conversation, changement de dimension, téléportation, disparition du PNJ ou inactivité ne déclenchent aucun départ automatique.
- Une fermeture avec Échap n'accorde pas implicitement d'argent, d'inscription ou de récompense de fin ; ces actions conservent leur validation explicite et leurs garde-fous existants.
- Un autre défaut a été reproduit au banc avec le vrai gestionnaire d'actions EasyNPC : si b10 ouvrait une capture pour le Léovic privé avant son action, le routeur reconnaissait seulement le PNJ permanent. Le routeur accepte maintenant aussi l'acteur déjà possédé par la même scène joueur/personnage. Les sessions d'autres PNJ restent refusées.

Aucun changement du trajet A–P, du moteur de navigation, des coordonnées, de la vitesse ou du graphe narratif. Aucun doublon de PNJ permanent. Les marqueurs, récompenses, données de personnage, classes client, Gautier, Hugues et la continuité MQ03 restent identiques à HF6. Le diff du JAR est limité aux métadonnées, à `Pq02Runtime` et à une classe d'aide aux transitions.

## Validation et limites

**Résultats du binaire final : 73 assertions de reproduction HF6, 305 de transitions/navigation, 398 de sauvegarde Nexus, 2 425 narratives serveur, 566 de protocole client et 9 de démarrage passent.** La garde anti-doublon passe également.

Le nouveau banc couvre la trace exacte d'Arthur, les contrôles verts réellement capturés par b10, les fermetures Échap/adieu/fermeture, le repos, les interruptions qui ne doivent pas faire avancer la quête, les véritables actions EasyNPC privées avec et sans capture préalable, puis la navigation réelle jusqu'au magistrat et à l'ascenseur avec deux acteurs indépendants. Les suites narratives, le protocole Fabric/b10, les sauvegardes périodiques Nexus et le démarrage du JAR sont rejoués sur le binaire final. Les résultats et empreintes sont dans `verification/hf7.json`.

Le serveur Minecraft, EasyNPC, la navigation et les sessions b10 sont réels au banc. Les connexions, la sélection des personnages et la carte de parcours sont des fixtures. La reprise après Échap et les choix sont exercés côté serveur avec les messages/captures natifs ; le rendu client reste doublé. Aucun essai graphique, cadrage, FPS, doublage ou franchissement des obstacles de la carte Indivis n'a été effectué ici. Les substitutions monétaires et CapSkills du banc ne sont pas livrées.

## Retour arrière

Pour revenir à HF6, arrêter serveur/clients et remettre le précédent JAR de quêtes, sans changer les datapacks ni les données. HF7 n'ajoute aucun état persistant ni migration ; HF6 réintroduit toutefois les défauts d'enchaînement décrits ci-dessus.
