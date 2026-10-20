# Indivis — Administration 0.1.0-alpha.1

Mod Fabric **Minecraft 1.21.11**, Java **21+**, Fabric Loader **0.18.0+** et Fabric API. **Candidat compilé, non testé dans Minecraft/Fabric.**

Il ouvre une interface native du jeu pour sélectionner un joueur connecté et gérer le personnage Nexus actif : rang social, criminalité, cinq réputations, autorisations de zones, assistance judiciaire, monnaie et diagnostic.

## Installation et utilisation

Serveur arrêté, ajouter `indivis-admin-0.1.0-alpha.1+1.21.11.jar` à `mods/`, puis redémarrer. Les clients 1.21.11 reçoivent les dialogues natifs sans devoir installer ce mod. Pour une partie locale avec serveur intégré, installer le JAR dans l'instance Fabric locale.

Le core actuel reste nécessaire pour les actions de gestion. Ce mod ne remplace aucun datapack, Nexus, journal ou mod de commandes admin déjà présent. Son identifiant est `indivis_admin` et sa commande est distincte.

- `/indivisadmin` : liste des joueurs connectés, avec leur personnage Nexus actif.
- `/indivisadmin joueur PseudoMinecraft` : accès direct au dossier de ce joueur connecté.
- Sélectionner une rubrique, choisir une action, puis vérifier la cible et confirmer.
- Fermer avec le bouton ou Échap. Réouvrir la commande pour une nouvelle session.

Permissions : opérateur de niveau 2 ou supérieur, vérifié aussi au moment d'appliquer l'action. Le rang social RP ne donne aucun droit d'administration.

La cible est le **personnage actif**, pas un autre personnage hors ligne du même compte. Si Nexus est installé sans personnage actif, si son profil est verrouillé ou si son API ne répond pas, les modifications sont désactivées. Sans Nexus installé, le mod utilise le profil de compte.

## Actions

| Rubrique | Fonctions cataloguées |
|---|---:|
| Rangs sociaux 0 à 100, par paliers de 10 | 11 |
| Criminalité : valeurs et variations natives | 20 |
| Réputations : Couronne, Cœur, Port, Garde des Profondeurs, Profondeurs | 85 |
| Accorder/retirer des accès aux zones | 20 |
| Justice : effacer le dossier, libérer la procédure, temporisations RP | 3 |
| Monnaie : émissions bancaires et conversion ancienne monnaie | 7 |
| Diagnostic numérique | 2 |

Le catalogue contient **148** entrées. Les fonctions réellement chargées sont vérifiées à chaque interaction ; seules celles présentes sont proposées. L'archive originale core 1.5.7 fournie contient 141 de ces fonctions ; la base SI1 restaurée en contient 148. Les sept entrées de monnaie absentes de l'original ne sont pas inventées dans ce mod. Voir `docs/core_audit.json` et `src/main/resources/admin_actions.tsv`.

Les fonctions `capitale:*_self` s'exécutent avec le joueur sélectionné comme `@s`, dans sa dimension et à sa position. Le core conserve ses règles de recalcul des accès, préfixes, justice et inventaire. Le mod ne reproduit pas ces règles en écrivant directement les scores.

Retirer une autorisation individuelle ne neutralise pas les autres sources d'accès maintenues par le core. Effacer le dossier criminel remet aussi à zéro les amendes et divers états judiciaires : les détails sont affichés avant confirmation. Les émissions de monnaie suivent le comportement natif du core en cas d'inventaire plein.

## Contrôle de la cible et traçabilité

Les boutons sont liés à l'administrateur, à sa connexion et à son personnage, puis à la connexion/personnage de la cible. Ils expirent au bout de cinq minutes et ne sont utilisables qu'une fois. Une reconnexion, un changement de personnage, un verrou de profil ou le retrait des droits opérateur empêche l'ancienne confirmation d'agir. Si les valeurs affichées ont changé, une nouvelle confirmation est demandée.

Les appels sont limités au catalogue embarqué ; les commandes reçues du client ne contiennent ni chemin de fonction libre ni score à écrire. Les appels au core se font en fin de tick serveur, en dehors de l'exécution du bouton Brigadier. Les résultats sont relus après le retour de la fonction.

Chaque appel est tracé dans la console serveur et dans `logs/indivis-admin/actions.jsonl` : administrateur, cible, personnage actif, fonction et valeurs avant/après. Le retour de la méthode native ne garantit pas que chaque commande interne du datapack a réussi ; les valeurs affichées et les logs du core restent la référence. Aucun journal n'est envoyé à un service externe.

## Vérifications effectuées

- Banc du contrôleur : **19 assertions** avec doubles d'API, notamment cible distincte, confirmation, rejeu, changement de personnage, reconnexion, expiration, retrait OP et valeur modifiée.
- Banc de l'adaptateur livré : **16 assertions** avec doubles Minecraft/Nexus, notamment scores lus sans création, catalogue `Iterable`, `@s`/dimension/position de la cible, profils verrouillés et liste blanche des fonctions.
- Vérification ASM : **15 classes, 131 méthodes, 0 erreur**, sans résolution des API Minecraft.
- JAR : CRC valide, aucun mixin et aucune classe Minecraft/Fabric/Nexus ou classe de test embarquée.

**Non effectués :** démarrage Fabric, décodage/affichage du dialogue dans le jeu, exécution réelle des 148 fonctions, sauvegarde Nexus et test de coexistence sur le serveur complet. Les bancs ne remplacent pas ces vérifications.

Sur une copie du serveur, vérifier l'ouverture OP/non-OP, une modification de rang et de criminalité sur un deuxième joueur, la conservation des valeurs de l'administrateur, les changements de personnage et les accès recalculés. Retirer le JAR désactive l'interface ; cela n'annule pas les modifications administratives déjà appliquées par le core.

## Reconstruction

Un JDK complet **17+** suffit pour ces outils ; le jeu exige Java 21+. Aucune dépendance à télécharger : les accès de production utilisent les noms intermédiaires 1.21.11, les API Fabric présentes à l'exécution et les API Nexus constatées dans le JAR fourni.

```bash
python3 build.py
python3 check.py
```

Le binaire est écrit dans `dist/`. `tools/BuildEntry.java` génère le petit point d'entrée implémentant `ModInitializer` ; les autres classes sont compilées depuis `src/main/java`. Les doubles `tests/java` sont exclusivement des entrées de banc et ne sont jamais livrés dans le mod.

Les contrats natifs sont référencés dans `docs/API.md`. Les traces et limites sont dans `audit/`. La compilation ne fournit pas de validation de liaison avec le jeu.
