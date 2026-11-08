# Indivis — HF5 : fermeture de la fenêtre de Léovic

**31 octobre 2026 — Fabric/Minecraft 1.21.11, Java 21, Dialogue b10.**

## Installation

1. Arrêter le serveur et fermer Minecraft sur les clients.
2. Remplacer l’ancien JAR de quêtes par **`haute-capitale-quetes-HF12-PQ02-R1-HF5-1.21.11.jar`**, sur **le serveur et chaque client**. Garder un seul JAR de quêtes.
3. Redémarrer complètement, puis reprendre le clic normal sur Léovic.

**Aucune commande, liaison, pose, préparation de trajet ni remise à zéro à refaire.** Les deux datapacks, les PNJ, les points et les progressions sont conservés. Le compagnon PQ02 séparé ne doit pas être installé. Dialogue, Nexus, Voices et les autres mods ne sont pas remplacés par cette livraison.

## Défaut reproduit et correction

HF4 confirmait que la session, le PNJ et la fenêtre étaient prêts avant d’envoyer les textes. Cette confirmation ne protégeait pas la fenêtre après l’arrivée de la page.

Le nouveau banc utilise le **JAR HF4 exact** : une capture privée déjà confirmée contient une réplique et un choix ; un appel extérieur à `setScreen(null)` la retire ; le moniteur natif b10 finit alors la session avec **`ECRAN_FERME`**. Une ouverture puis fermeture de menu étranger entre deux ticks produit aussi cette fin, car le moniteur n’a pas vu ce menu.

HF5 intercepte la remise à zéro **avant le retrait** de la capture PQ02. La protection exige la session active et le bon UUID de PNJ attendus par PQ02. Elle garde la même fenêtre, sa page et ses choix. Échap et les fins serveur restent autorisés ; les autres sessions n’héritent pas de la protection. L’ouverture d’un inventaire ou d’un autre menu reste possible. Sa fermeture reste possible également ; une visite très brève est mémorisée pour permettre le retour natif b10 lorsque ses drapeaux l’autorisent.

Le contrôle est commun à PQ02 et ne dépend d’aucun waypoint. Il ne cherche aucune entité dans un tick ni n’ajoute de boucle de réouverture. Le runtime serveur, les dialogues, l’escorte, les récompenses, la persistance et les marqueurs sont identiques à HF4. **Gautier est conservé octet pour octet.**

## Contrôles exécutés

- **7 assertions :** reproduction des deux chemins de fermeture sur le JAR HF4 exact.
- **63 assertions :** protection HF5 appliquée par le vrai moteur de mixins Fabric/Knot sur le client du banc ; retraits répétés, conservation de page/choix, Échap, fin serveur, autres menus, menu furtif, autre session et nettoyage.
- **566 assertions de protocole client :** les 111 blocs PQ02 passent par les événements de messages Fabric et l’état natif b10.
- **2 425 assertions serveur :** narration, navigation, isolation, reprises, récompenses, Hugues, Gautier et retour MQ03 auprès d’Althéon/aux Archives.
- Compilation Java 21, **261 contrôles statiques**, **145 contrôles Java de règles** ; démarrage du JAR de production intact : **8 PASS** et refus du compagnon obsolète PASS.

Le JAR ne diffère de HF4 que par la métadonnée, la configuration client de mixin, l’adaptateur client PQ02 et une nouvelle classe de mixin client. Aucun fichier existant n’est retiré. Les deux datapacks restent identiques.

## Limites précises

Le banc reproduit la raison de fermeture et vérifie la protection du chemin `setScreen(null)`. **Il n’identifie pas le composant exact qui effectue cet appel sur le client d’Arthur.**

Le cycle b10 et le mixin sont réels ; les surfaces graphiques, le monde client, ses entités et les sorties réseau du banc sont doublés. La liste de mixins client est activée dans le serveur de test pour exercer l’injection ; elle reste exclusivement client dans le mod livré. Aucun client graphique relié au serveur, carte Indivis, cadrage rendu, FPS ou doublage n’a été testé. Les autres limites du banc narratif sont détaillées dans `RAPPORT.md`.

Les résultats et empreintes du binaire effectivement testé figurent dans `verification/hf5.json`. Les archives de jeu sont destinées à une recette avec le modpack réel ; aucune installation ni modification du serveur utilisateur n’a été effectuée ici.
