# Indivis — HF4 : synchronisation complète des dialogues PQ02

Minecraft/Fabric **1.21.11**, Java **21** — **29 octobre 2026**.

## Installation

1. Arrêter le serveur et fermer Minecraft sur les clients.
2. Remplacer le précédent JAR de quêtes par **`haute-capitale-quetes-HF12-PQ02-R1-HF4-1.21.11.jar`**, dans `mods/` du serveur **et de chaque client**. Garder un seul JAR de quêtes. Le compagnon `indivis-pq02` ne doit pas être installé.
3. Garder les datapacks, les autres mods, les PNJ, les points, les liaisons et les données de personnage.
4. Redémarrer complètement et reprendre le clic normal sur Léovic.

**Aucune commande à modifier ou à refaire. Aucun reset de quête, déplacement de PNJ ou remplacement de datapack. Les clients doivent aussi recevoir HF4.**

## Changement du moteur commun

HF3 tentait de récupérer un écran retiré après affichage. Les nouveaux logs confirment que cette protection était insuffisante. HF4 remplace ce mécanisme par une confirmation de disponibilité de la capture.

Le serveur ouvre la session native b10 et attend que le client confirme la bonne session, le bon UUID de PNJ chargé dans son monde et une fenêtre de capture stable. Ensuite seulement, il envoie les répliques et les choix. L’attente est protégée avant le moniteur de fin de capture b10, notamment lorsque le chargement ou la réponse réseau prennent du temps. Une réponse ancienne, d’un autre joueur, d’un autre personnage ou d’une étape incompatible est refusée.

Cette règle s’applique à **chaque changement de caméra de PQ02** : premier accueil, conversations du parcours, magistrat, plan à trois, séparation et retour ultérieur. Les données et coordonnées des points ne sont pas remplacées par des cas particuliers. L’attente est bornée ; un échec conserve la progression et les décisions. Aucun gain ne dépend de cette confirmation.

Les dialogues, règles, trajet, récompenses, persistance, marqueurs et mixins restent ceux de HF3. **Gautier est conservé octet pour octet dans sa version HF3** : vitesse 1,25 et reprise de Géraud pendant son retour. Les deux datapacks sont identiques à HF3.

## Contrôles et limites

Le banc client passe les **111 blocs** par les vrais événements de messages Fabric et les méthodes natives d’ouverture/fermeture/capture b10. Il couvre une demande reçue avant la session, un acteur pas encore chargé, la stabilité de l’écran, un délai de capture expiré pendant l’attente, les changements de focale, les répliques, les choix, Échap et la déconnexion. Le banc serveur vérifie le blocage de l’envoi avant confirmation, les refus de jetons et de personnages incompatibles, la commande sans OP et le délai maximal. Le parcours narratif, les récompenses et MQ03 sont relancés.

Les nouveaux logs montrent une fin `ECRAN_FERME` sans choix PQ02, mais ne donnent toujours pas l’appel précis qui retire l’écran sur le client. Le test de livraison désordonnée démontre une faiblesse réelle de HF3 ; **il n’est pas présenté comme la reproduction exacte de cet appel**. HF4 journalise aussi la demande, la confirmation, la page et, au plus une fois par focale, la pile d’un retrait d’écran sans fin volontaire. Ces traces restent dans le log client.

Les surfaces graphiques, le monde client et la sortie C2S du banc sont doublés ; aucun rendu Minecraft ni carte Indivis n’a été lancé. Les connexions, sélections Nexus et intégrations économiques restent soumises aux limites détaillées dans `RAPPORT.md`. Les résultats exacts et empreintes du JAR testé figurent dans `verification/hf4.json`.
