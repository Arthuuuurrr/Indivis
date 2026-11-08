# Indivis — HF3 : dialogue de Léovic et rythme de Gautier

Minecraft/Fabric **1.21.11**, Java **21** — livraison du **29 octobre 2026**.

## Installation depuis HF1 ou HF2

1. Arrêter le serveur et fermer Minecraft sur les clients.
2. Dans `mods/`, remplacer le précédent JAR de quêtes par **`haute-capitale-quetes-HF12-PQ02-R1-HF3-1.21.11.jar`**, sur le serveur **et chaque client**. Garder un seul JAR de quêtes. Ne pas installer le compagnon `indivis-pq02`.
3. Garder le core **PQ02 R1 HF1**, `hc_quetes` **PQ02 R1**, les autres mods, les PNJ et les repères déjà préparés.
4. Redémarrer complètement et reprendre au clic droit sur Léovic.

**Aucune commande à modifier ou à refaire. Aucun reset, aucune liaison ni remise en place des personnages.** Le correctif de fermeture est côté client : mettre à jour le serveur seul ne suffit pas.

## Changements

**Léovic.** Les logs montrent l’ouverture de la session privée, puis une fin `ECRAN_FERME` après deux ou trois secondes, sans choix PQ02 envoyé. HF3 conserve une référence à l’écran de cette session. Si cet écran disparaît sans demande de fin, il est rétabli une seule fois pour la trame en cours, avec la même page. Échap, le bouton de fermeture, les fins serveur, les autres menus et un changement de session restent respectés. Aucun dialogue ni gain de quête n’est rejoué lors du rétablissement.

**Gautier.** Sa vitesse de navigation passe de 1,00 à **1,25**, sur sa copie privée. Il continue à parler pendant son approche. Après « Bien, quartier-maître ! », il repart vers son poste et Géraud reprend immédiatement ; le dialogue n’attend plus la fin du retour. Le point d’approche et le poste existants sont conservés. Perrin et les autres dialogues PQ01 ne sont pas modifiés.

Les deux datapacks sont identiques à HF2. Le serveur PQ02, les règles, les récompenses, les marqueurs, la persistance, le graphe et les mixins sont identiques à HF2. Les seuls changements de code de production sont l’adaptateur client PQ02 et deux classes de la passerelle PQ01 pour le rythme de Gautier.

## Vérifications et limites

**1 697 assertions passent** sur serveur Fabric/EasyNPC jetable, dont 17 vérifications du cycle d’écran et 15 du déplacement et du retour de Gautier. S’ajoutent 261 contrôles statiques, 145 contrôles Java, le démarrage du JAR de production et le refus du compagnon en doublon. Les dialogues PQ02, les récompenses, la séparation entre joueurs, Hugues et le retour MQ03 aux Archives sont contrôlés.

Le scénario de régression retire volontairement l’écran dans le banc : avec HF2, le moniteur natif b10 produit `ECRAN_FERME` ; avec HF3, la conversation est conservée. **Les logs fournis ne permettent pas d’identifier l’opération précise qui retire l’écran sur le client réel.** Ce test couvre le chemin de fermeture observé et la protection ajoutée.

L’état et la capture b10 sont exécutés, avec un écran, un client Minecraft et une sortie réseau C2S simulés. La navigation et les acteurs EasyNPC du test de Gautier sont réels. Les connexions et la sélection Nexus restent synthétiques ; monnaie, CapSkills et bonus externes sont substitués uniquement dans le banc. Aucun client graphique, FPS, doublage ni parcours sur la carte Indivis n’a été testé ici.

Voir `RAPPORT.md` et `verification/hf3.json` pour le périmètre et les preuves.
