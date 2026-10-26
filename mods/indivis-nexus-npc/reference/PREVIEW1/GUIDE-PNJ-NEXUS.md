# Indivis — Presets de PNJ Nexus PREVIEW1

26 octobre 2026. Module compagnon Fabric pour Minecraft 1.21.11, NexusCharacters
PRE35 SI1 et EasyNPC 7.16.0. Ce sont des profils EasyNPC créés avec les apparences
Nexus, pas de simples skins PNG. Aucun déploiement sur le serveur de production.

## Les cinq presets

| Identifiant | Direction visuelle |
| --- | --- |
| `haut_elfe_ivoire` | Très grand et fin, longues oreilles, cheveux blancs, iris bleus, tenue ivoire et or. |
| `elfe_des_bois` | Plus petit et élancé, oreilles relevées, peau dorée, cheveux bruns, iris verts, tenue verte et sombre. |
| `nain_bronze` | Petit et large, nez nain, barbe rousse tressée, iris ambrés, armure bronze. |
| `naine_forge` | Petite et robuste sur corps slim, cheveux roux, barbe fournie, tenue de forge rouge et brune. |
| `nordique_roux` | Grand et massif, peau pâle, longs cheveux roux, barbe classique rousse, iris bleus, tenue nordique. |

Les limites raciales Nexus sont respectées. Aucun asset de base n'est remplacé.
Pas de preset orc : cette race n'est pas proposée par cette version du créateur Nexus.

## Installation

1. Sauvegarder le monde et la configuration avant tout essai.
2. Ajouter `Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar` dans `mods/` **du client et du serveur**.
3. Conserver Nexus PRE35 SI1, EasyNPC 7.16.0 et leurs dépendances : le module ne les remplace pas.
4. Utiliser Java 21, Fabric Loader 0.19.5 et Fabric API 0.141.6+1.21.11, ou versions compatibles plus récentes.

Base Nexus vérifiée : `NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar`, version
interne `1.0.0-beta.35+indivis.1.21.11.SI1`, SHA-256
`399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a`.
Une autre version de Nexus n'est pas couverte par ce prototype.

Les cinq presets sont déjà intégrés au JAR, exposés dans les presets par défaut
EasyNPC sous `indivis_nexus`, catégorie « Indivis — Nexus ». Le dossier
`presets-easynpc/` du ZIP contient leurs copies SNBT pour import manuel éventuel ;
il n'est pas nécessaire de les recopier pour installer le module.

## Créer ou appliquer un preset

Commandes administrateur, niveau OP 4 :

```text
/nexusnpc list
/nexusnpc spawn haut_elfe_ivoire
/nexusnpc spawn elfe_des_bois
/nexusnpc spawn nain_bronze
/nexusnpc spawn naine_forge
/nexusnpc spawn nordique_roux
```

`spawn` crée un nouveau PNJ à la position de la source de commande. Espacer les
placements pour comparer leurs silhouettes. Pour ne changer que l'apparence
d'un PNJ existant, utiliser ces commandes plutôt qu'un import de preset complet :

```text
/nexusnpc apply <uuid-du-pnj> nain_bronze
/nexusnpc reset <uuid-du-pnj>
```

La cible doit être `easy_npc:humanoid` ou `easy_npc:humanoid_slim`. `apply` conserve
son type, son UUID et son nom ; il change son profil de skin et l'échelle du modèle
racine. Le skin et le modèle racine d'origine sont sauvegardés pour `reset`.
Les facteurs d'échelle ne s'accumulent pas lors d'applications successives.
Ne pas importer un preset EasyNPC complet sur un PNJ de quête pour une simple retouche visuelle.

## Tes propres presets dans l'éditeur Nexus

```text
/nexusnpc_local editor mon_preset
```

Le créateur s'ouvre avec le titre « Créer un preset de PNJ ». Choisir une apparence
Nexus, renseigner prénom et nom, puis cliquer sur « Créer » : dans ce mode, le
bouton exporte une recette locale sans créer de personnage joueur. Les couleurs
et cosmétiques restent ceux du créateur Nexus.

```text
/nexusnpc_local push mon_preset
/nexusnpc spawn mon_preset
```

`push` envoie la recette au serveur ; attendre sa confirmation. Le serveur contrôle
les droits OP 4 et valide les données. Un ID existant n'est pas écrasé silencieusement :
choisir un nouvel ID pour une variante. Les skins Mojang/de compte ne sont pas
exportables par ce prototype.

Fichiers locaux : `config/indivis-nexus-npc/local/`.
Recettes publiées : `config/indivis-nexus-npc/presets/` côté serveur.

## Relief et nouveaux assets

Le module réutilise la composition et la géométrie natives Nexus pour les cheveux,
vêtements, barbes classiques, oreilles et barbes volumétriques. Il ne contient
pas de liste fermée de coiffures ou de tenues. Les nouveaux assets reconnus par
le catalogue Nexus peuvent être exportés dans un nouveau preset, sous réserve
de respecter ses formats UV et ses règles de nommage. Le test d'ajout utilise
trois nouveaux IDs `930`, sans modification du code de production du module.

## Limites et retrait

Préversion visuelle vérifiée dans une instance isolée avec de vrais PNJ EasyNPC.
Le test des poses animées reste coûteux : ce pack n'est pas validé pour un usage
en foule. La mesure CPU n'est pas une validation du framerate de Lion-Port ni du
pack complet. Faire l'essai sur une copie du serveur ; ne pas le déployer en masse.
Le rapport joint précise les mesures et les vérifications.

Le module doit rester installé pour afficher les recettes 3D. Avant de le retirer,
utiliser `reset` sur les PNJ concernés et conserver une sauvegarde. Il ne modifie
aucun datapack de quête et n'applique rien automatiquement aux PNJ existants.
