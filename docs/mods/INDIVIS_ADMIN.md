# Indivis — Administration

Correctif **0.1.0-alpha.2+1.21.11**, prévu pour le serveur Fabric 1.21.11. Remplace alpha.1 ; pas de mod client supplémentaire. Le core, Nexus et le journal restent les fichiers existants.

Corrige le rejet de la cible des commandes de dialogue/messages et préserve le verrou antérieur autour du hook de sauvegarde Nexus. Le contrôle OP2, le personnage actif, les confirmations à usage unique et les appels aux fonctions natives du core restent en place. Un mixin de compatibilité facultatif est ajouté pour le Nexus PRE34-SI1 fourni.

[Installation, explication du verrou et recette](../../sources/indivis-admin/README.md) · [empreinte du JAR](../../sources/indivis-admin/docs/ARTIFACT.json) · [suivi #174](https://github.com/Arthuuuurrr/Indivis/issues/174).

Validation : 19 assertions contrôleur, 21 adaptateur (API doublées), 12 modèle de commandes, 6 politique Nexus réelle ; cible d’injection PRE34 vérifiée et handler compilé exécuté. ASM : 16 classes/133 méthodes, aucune erreur. **Aucun démarrage Minecraft/Fabric effectué ici** : application réelle du mixin, affichage et actions restent à recetter en jeu.
