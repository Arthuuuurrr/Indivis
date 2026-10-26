# Indivis Nexus NPC — prototype PREVIEW1

Issue #180. Module compagnon pour Nexus PRE35 SI1 et EasyNPC 7.16.0 sous Fabric Minecraft 1.21.11.
Développement et vérifications en cours ; aucun déploiement et aucune fusion dans main.

## Périmètre

Recettes d'apparence Nexus appliquées aux véritables types `easy_npc:humanoid` et
`easy_npc:humanoid_slim`, sans remplacer l'entité. Rendu natif 3D Nexus, oreilles,
barbes, nez nain, couleurs et morphologie. La mise à l'échelle raciale utilise
le root-model EasyNPC existant. Le skin et le root-model précédents sont sauvegardés
dans l'enveloppe de recette pour permettre `reset`.

La recette est enregistrée et synchronisée dans `SkinData.Content`, via les mécanismes
EasyNPC existants. Aucun paquet récurrent par image. Le seul nouveau paquet sert à
publier un preset client, après validation et contrôle OP 4 côté serveur.

## Utilisation prévue

Installer le même petit module compagnon côté client ET serveur. Ne remplacer ni
Nexus PRE35 SI1, ni EasyNPC, ni les autres mods/datapacks.

```text
/nexusnpc list
/nexusnpc spawn haut_elfe_ivoire
/nexusnpc apply <uuid-ou-sélecteur-unique> nain_bronze
/nexusnpc reset <uuid-ou-sélecteur-unique>
/nexusnpc_local editor mon_preset
/nexusnpc_local push mon_preset
```

`editor` ouvre le créateur Nexus en mode PNJ. Le bouton « Créer » exporte uniquement
la recette ; il ne doit pas créer de personnage joueur. Le titre identifie ce mode.
Choisir une apparence Nexus, pas un skin de compte Mojang. Les fichiers locaux sont
dans `config/indivis-nexus-npc/local/`. La publication transmet la recette au serveur,
qui la valide et la stocke dans `config/indivis-nexus-npc/presets/`.
Les IDs existants ne sont pas remplacés silencieusement : utiliser un nouvel ID.

Les préréglages intégrés sont dans le JAR : haut-elfe d'ivoire, nain du bronze,
naine des forges, nordique roux, elfe des bois. Aucune nouvelle race non prise en
charge par Nexus n'est inventée ; les options et limites raciales existantes sont
respectées. Les nouveaux assets doivent suivre les règles Nexus déjà supportées.

Les commandes serveur sont réservées aux administrateurs OP 4. Aucune application
automatique sur les PNJ existants. Avant désinstallation, `reset` permet de retrouver
l'apparence précédente des PNJ modifiés ; garder le module tant que la recette 3D
doit rester affichée.

## Éléments à vérifier avant livraison

Rendu réel, corps normal/slim, export dans l'éditeur, publication C2S, données NBT
hors apparence inchangées, reset, rechargement de monde/ressources et charge de rendu.
Les chiffres PRE35 obtenus sur des joueurs ne sont pas des mesures de ce prototype PNJ.

## Compilation

Java 21, Minecraft 1.21.11 remappé intermediary et dépendances Fabric dans `../pre31/runtime`.
Placer les trois JAR d'origine dans `base/`, puis `python3 build.py`.
Les dépendances et assets tiers ne sont pas redistribués. Le JAR Nexus de base reste
strictement identique au SHA-256 `399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a`.
