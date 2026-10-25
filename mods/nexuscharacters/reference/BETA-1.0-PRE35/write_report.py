from pathlib import Path
import json,datetime,zoneinfo,hashlib,zipfile
R=Path(__file__).resolve().parent;Q=R/'qa';data=json.loads((Q/'comparisons.json').read_text());archive=data['archive'];build=data['build']
def percent(a,b):return 100*(1-b/a)
rows=[]
labels={'walking1':'Marche, 1 personnage','walking4':'Marche, 4 personnages','walking8':'Marche, 8 personnages','relative4':'Rotations relatives, 4 personnages'}
gains=[]
for name,label in labels.items():
 old=data['multi_perf']['baseline'][name];new=data['multi_perf']['candidate'][name]
 cpu=percent(old['cpu_ms_per_pose'],new['cpu_ms_per_pose']);allocation=percent(old['allocation_bytes_per_pose'],new['allocation_bytes_per_pose']);gains.append(cpu)
 rows.append(f"| {label} | {old['cpu_ms_per_pose']:.3f} | {new['cpu_ms_per_pose']:.3f} | {cpu:.1f} % | {allocation:.1f} % |")
world=json.loads((Q/'last-candidate-world.json').read_text());oracle=(Q/'oracle-world-evidence/oracle-counts.txt').read_text().strip();exact=(Q/'exact-geometry.txt').read_text().strip()
created=datetime.datetime.now(zoneinfo.ZoneInfo('Europe/Paris')).isoformat(timespec='seconds')
group_cost=data['multi_perf']['candidate']['walking8']['cpu_ms_per_pose']*8
dense=data['multi_perf']['candidate']['relative4']['cpu_ms_per_pose']
report=f'''# PRE35 SI1 — vérifications et optimisation du rendu des joueurs

Rapport établi le {created}. Minecraft 1.21.11, Fabric Loader 0.19.5.
Issue #178 ; PR en brouillon #179, sans fusion ni déploiement serveur.

## Résultat livré

Le calcul des surfaces animées est accéléré sans diminuer la 3D, sans masquer des
couches, sans espacer davantage les assets et sans arrondir les poses.
Sur le banc à poses nouvelles, le CPU de préparation baisse de
{min(gains):.1f} à {max(gains):.1f} % selon le cas mesuré. Il s'agit du calcul de
préparation des poses, pas du framerate global du modpack.

La PRE35 précédente avait été perdue avec le nettoyage de l'environnement temporaire.
Cette livraison est reconstruite ; TOUS les résultats ci-dessous concernent le nouveau
JAR, identifié par son SHA-256. Ils ne sont pas repris des chiffres historiques du
checkpoint documentaire `CHECKPOINT-2026-10-25.md`.

## Base et identification

- Base exacte : `NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar`.
- Version : `1.0.0-beta.34+indivis.1.21.11.SI1`.
- SHA-256 base : `{build['base_sha256']}`.
- Livrable : `NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar`.
- Version : `{build['version']}`.
- SHA-256 livré : `{build['jar_sha256']}`.

Les sources, le patch compilé et le manifeste permettent de reconstruire ce JAR
octet pour octet depuis la base exacte. La PRE34 sans SI1 et la PRE33 ne sont pas
des bases acceptées par le script de reconstruction.

## Diagnostic ciblé

Le diagnostic initial, conservé dans le checkpoint de la PR, porte sur le profil
client `QDXof4JAxf.sparkprofile` : 121,258 secondes, échantillonnage Java 4 ms,
25 octobre 2026 16:35:49–16:37:50 UTC, soit 17:35–17:37 à Paris ce jour-là.
En branches inclusives dédupliquées du thread de rendu : Nexus environ 62,07 %,
PosedSurfaceSupport 57,79 %, SurfaceGeometry 33,35 %, PreparedSurfaceCache 0,10 %.
Ces chiffres sont inclusifs et ne doivent pas être additionnés.

Le cache exact aide les poses déjà vues, mais chaque animation continuellement
nouvelle demande encore des découpes. Regarder plusieurs joueurs multiplie ces
travaux, y compris lorsque leurs renderers partagent les mêmes modèles.
Ce diagnostic n'attribue pas l'ensemble des ralentissements du serveur au client.
Le log serveur comporte également des retards de 2 à 5,836 secondes ; leur fuseau
n'est pas attesté et leur cause n'est pas établie par ce profil client.

## Modification du calcul

Deux corps de méthodes délèguent aux nouveaux helpers :

1. `SurfaceGeometry.subtract(Polygon, Box, boolean)` : une projection par sommet
   et boîte, conservation des coordonnées des sommets survivants sur les six plans.
   Chaque nouveau sommet est reprojeté avec l'opération JOML d'origine.
2. `PosedSurfaceSupport.clean(...)` : réutilisation des mêmes requêtes de cellules
   à l'intérieur d'une passe, et suppression des listes intermédiaires inutiles.

L'ordre des plans, les formules d'intersection, EPS, les rangs, UV, normales et
tri stable des boîtes sont conservés. Les listes de sommets émises ne créent pas
de nouvel alias mutable avec l'entrée. Buffers propres au thread, cache de requêtes
limité à 2048 entrées par passe, aucun cache de poses approximatif.
Le nettoyage 3D des intersections entre corps et membres reste exécuté.

## Non-régression du JAR

- {archive['original_entries']} entrées originales, aucune suppression.
- {archive['identical_original_entries']} entrées originales identiques, dont
  {archive['identical_assets']} assets et {archive['identical_original_classes']} classes.
- Changements : `fabric.mod.json` et les deux classes ciblées ; quatre classes
  de support ajoutées.
- Contrats et corps des 26 autres méthodes des classes ciblées vérifiés.
- Quatre méthodes de référence QA contrôlées instruction par instruction contre
  la base SI1 ; elles sont absentes du JAR de production.
- Classes serveur, SI1, quêtes, réseau, persistance, menus, mains et HUD non réécrits.

L'identité des classes serveur préserve leur code ; elle ne remplace pas une recette
complète des quêtes/synchronisations sur le serveur de production.

## Vérifications réalisées sur cette reconstruction

- `{exact}`
- `{data['bytecode']}`
- 507 empreintes de géométrie identiques à la base : 320 états de rendu,
  91 de cache/invalidation, 96 de modèles partagés alternant normal et slim.
- 23 captures complètes de menus pixel-identiques, toutes races et plusieurs
  échelles d'interface ; contrôle de la rotation, des bannières et des sliders.
- Mains, changements de perspective, poses relatives, invalidation des caches,
  rechargement de ressources et retour depuis une caméra alternative contrôlés.
- Nouveaux PNG de coiffure, barbe et tenue, absents du JAR livré : 23 contrôles,
  IDs de test 930, corps 2, normal et slim. Ils ne sont présents que dans le banc.
- Monde isolé : 24 scènes ; 1, 2, 4 puis 8 entités `OtherClientPlayerEntity`
  effectivement observées dans le chemin de rendu Nexus. Huit entités aussi
  observées en dialogue et en F1. Les six meshes de base et la présence de
  géométrie 3D extérieure sont vérifiés lors des rendus.
- Canalisation : véritable contrôleur et animation `off_hand_channeling` de
  Spell Engine RC17. Caméra réelle Dialogue b10, démarrage et arrêt contrôlés.
- `{oracle}`

L'oracle exécute l'ancienne et la nouvelle méthode sur les mêmes listes de
polygones ET les mêmes instances de boîtes ; comparaison des bits des positions,
UV, normales, rangs, tailles et ordre des polygones/sommets.
Ses preuves sont séparées de celles de production, sans écrasement commun.

Marqueur du monde de production :

```text
{world['completion']}
```

## Mesures CPU et allocations

Clients successifs, pas de concurrence entre le client témoin et le candidat.
Cinq échantillons par cas, 24 phases par échantillon, modèles natifs réellement
préparés par le renderer. Poses nouvelles sur CHAQUE appel ; le compteur de
recalcul doit valoir 24 × nombre de personnages pour chaque échantillon.
Les résultats sont des médianes, temps CPU du thread par pose.

| Cas | PRE34 SI1, ms/pose | PRE35 SI1, ms/pose | CPU en moins | Allocations en moins |
| --- | ---: | ---: | ---: | ---: |
{chr(10).join(rows)}

La marche à huit représente encore environ {group_cost:.1f} ms de CPU pour le
groupe dans ce banc. Le cas dense à rotations relatives coûte encore
{dense:.1f} ms par pose. La baisse est mesurée, mais ce coût résiduel interdit
d'en déduire une promesse de 60 FPS avec huit joueurs ou de disparition de tous
les ralentissements sur Indivis.

## Environnement et limites

Java 21, Minecraft 1.21.11, Loader 0.19.5, JOML 1.10.8, affichage logiciel Mesa
llvmpipe, 1920 × 1080. Fabric API 0.141.6, Skin Layers 1.10.2,
Sodium 0.8.7, ImmediatelyFast 1.14.2, Iris 1.10.7 sans shaderpack,
EntityCulling 1.10.2 / TRender 1.0.13, PAL 1.1.10,
Accessories 1.4.3-beta patché + NULLCAP-GUARD1, EasyNPC 7.16.0,
Spell Engine RC17, Spell Power RC9, RPG RC3, Dialogue b10 et HUD OPT1.
Les empreintes des dépendances du banc sont enregistrées dans `qa/test-mods.json`.

Les journaux du banc contiennent des avertissements préexistants de dépendances
optionnelles et les erreurs de services en ligne attendues en mode hors ligne.
Ils ne sont pas présentés comme un démarrage sans aucun avertissement.
Le banc n'utilise pas de compte Mojang connecté : l'identité du code de gestion
de skin Mojang est contrôlée, pas une récupération réseau authentifiée.

Le banc distant utilise huit entités dans un monde client isolé, pas huit connexions
réseau. Les variations normal/slim sont vérifiées dans les modèles, prévisualisations,
mains et PNG futurs ; la largeur distante réelle n'est pas revendiquée comme une
recette de tous les renderers de tous les mods.
La canalisation ne couvre pas tous les effets de sorts, dégâts ni combats possibles.
Lion-Port, Distant Horizons, les armures personnalisées et l'ensemble du modpack
de production ne sont pas reproduits. Pas de mesure de FPS sur le PC d'Arthur.

## Installation / retour arrière

Remplacer uniquement le JAR NexusCharacters côté client, jeu fermé, et n'en garder
qu'un seul dans `mods`. Conserver la PRE34 SI1 hors du dossier pour revenir en arrière.
Ne pas remplacer HUD OPT1, Skin Layers, les autres mods ou les datapacks.
Pas de migration de personnages ni modification des formats de sauvegarde.
Les futurs assets gardent les règles de nommage/UV/découverte déjà supportées.
'''
(R/'PRE35-verifications.md').write_text(report)
print('REPORT35_READY',len(report),flush=True)
