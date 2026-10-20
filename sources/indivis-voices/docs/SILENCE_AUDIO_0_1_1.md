# Silence du test et distribution 0.1.1

## Constats du 19 octobre 2026

- `latest(20261019-105921).log` charge `indivis_voices 0.1.0+1.21.11` sur le
  client ; le journal serveur `latest - 2026-10-19T125854.848.log` charge la
  même version et initialise le pont b10/Nexus.
- Le rechargement client ne contient ni ressources `indivis_voices`, ni pack
  de démonstration externe. La capture des réglages indique volume général
  26 % et Narrator/Voice 100 % : ces deux curseurs ne sont pas coupés.
- La distribution 0.1.0 excluait volontairement les OGG du JAR. Le ZIP audio
  était facultatif, mais son absence signifiait zéro prise disponible ; le
  client n'annonçait donc aucune voix et le serveur gardait le chemin texte.
- Accepter automatiquement les packs serveur ne configure pas un pack sur le
  serveur. Voices 0.1.0 ne publiait ni URL de pack ni téléchargement automatique.

L'absence de banque chargée suffit à expliquer le silence. Elle ne prouve pas
que les étapes suivantes (capture b10, son, fin audio, séquence) ont fonctionné :
elles n'ont pas été exercées par cet essai sans assets.

## Correction

À la demande d'Arthur, le JAR 0.1.1 inclut les huit OGG déjà audités et leur
`sounds.json` dans `assets/indivis_voices`. Fabric charge ces ressources avec
le mod. Pas de pack à sélectionner ni de configuration serveur à ajouter.
Une seule source de fichiers est conservée dans `demo-resourcepack/assets` ;
Gradle la copie dans les ressources client. Aucun master n'est copié.

`verifyBundledVoices` contrôle le **JAR remappé** pendant `check/build` : chaque
asset et son SHA-256, chaque liaison d'événement sonore et l'absence des
masters. Vérifier uniquement les fichiers de développement ne suffisait pas.

Compilation propre client/serveur et remap **réussis** : 17 tests JUnit, zéro
échec. Le contrôle du JAR livré trouve huit OGG aux hashes attendus et aucun
master. Deux archives temporaires volontairement invalides (asset absent,
asset modifié) sont bien rejetées par le même vérificateur. Les résultats et
empreintes du correctif sont dans [verification-0.1.1.json](verification-0.1.1.json).
Le lancement serveur isolé décrit dans `VALIDATION.md` reste celui de 0.1.0 ;
il n'a pas été rejoué pour 0.1.1. L'adaptateur local de compilation pour la sonde
de sockets Unix est le même principe que précédemment, sans modification d'un
binaire tiers ni inclusion dans le mod.

Les diagnostics client et serveur rendent visible le nombre de prises
chargées/annoncées et les motifs de retour texte. `/ivvoices test` isole la
lecture locale de la logique de quête : première voix de Géraud, canal VOICE,
sans atténuation de distance, sans PNJ ni paquet serveur. Une nouvelle commande
remplace le test précédent ; une session serveur l'arrête. Ce contrôle ne
valide pas la synchronisation b10 ni la spatialisation d'une voix de quête.

## Recette sur une copie de test

1. Remplacer Voices 0.1.0 par **0.1.1+1.21.11**, client et serveur ; redémarrer.
2. Sans ZIP vocal externe activé, `/ivvoices` doit afficher 8/8, pont b10 prêt,
   canal serveur présent, volumes non nuls. Le log client doit annoncer 8/8.
3. Hors dialogue, `/ivvoices test` doit jouer Géraud sans changer la progression.
   Répéter la commande ne doit pas empiler les sons. Arrêter/recharger pendant
   le test ne doit laisser aucun son ; un dialogue serveur doit l'interrompre.
4. Rejouer les trois répliques de réveil de Géraud puis les trois d'introduction
   d'Althéon sur un personnage approprié ; vérifier fins, clic, ESC, choix et caméra.
5. Continuer la recette complète de `VALIDATION.md`, notamment à deux joueurs.

Le changement ne touche aucun texte/ID, datapack, JAR existant, permission,
protocole ni règle de progression. L'écoute et la recette en jeu restent à faire
sur la pile complète ; une compilation et un test de présence des OGG ne les
remplacent pas.
