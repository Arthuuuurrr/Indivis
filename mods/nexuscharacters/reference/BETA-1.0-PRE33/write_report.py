from pathlib import Path
import csv,json,statistics
R=Path(__file__).resolve().parent
perf=json.loads((R/'qa/pose-perf-summary.json').read_text());audit=json.loads((R/'qa/archive-audit.json').read_text());comp=json.loads((R/'qa/comparisons.json').read_text())
names={'warm-two-poses':'Alternance de deux poses connues','clear-same-pose':'Même pose après détachement du modèle','four-poses':'Alternance de quatre poses connues','eight-relative-poses':'Alternance de huit poses relatives connues','unique-lower-poses':'Poses de base continuellement nouvelles','unique-relative-poses':'Poses relatives continuellement nouvelles'}
rows=[]
for name,label in names.items():
 a=perf['baseline'][name];b=perf['candidate'][name]
 rows.append(f"| {label} | {a['median_cpu_ms']:.4f} | {b['median_cpu_ms']:.4f} | {','.join(map(str,b['sample_recompute_counts']))} |")
report=f'''# Indivis — PRE33 et HUD OPT1 — vérifications du 10 octobre 2026

La PRE33 conserve le calcul 3D de la PRE32 et étend la réutilisation de ses résultats. Le HUD OPT1 évite de rechercher à nouveau les mêmes méthodes, y compris les méthodes absentes. Les gains mesurés concernent les préparations retrouvées dans le cache ; une pose entièrement nouvelle garde le coût de découpe.

## Installation

Remplacer NexusCharacters PRE32 par `NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE33.jar`. Sur le client, remplacer le HUD `BETA_1_3_3_RACE_HEALTH_MODIFIER_COMPAT` par `capitale_rp_hud_BETA_1_3_3_OPT1_1.21.11.jar`. Garder un seul JAR par mod. Aucun fichier de configuration supplémentaire. Conserver les dépendances habituelles. Le HUD serveur peut conserver sa 1.3.3 : les classes serveur et le protocole réseau sont identiques.

## Ce qui change

- Le modèle complet conserve jusqu'à 64 préparations locales, indexées par partie, template, identité de la forme native et matrice relative exacte. Un détachement courant n'abandonne plus ces préparations.
- Jusqu'à 16 poses complètes sont conservées par modèle, avec un plafond de 16 Mio pour leurs tableaux de sommets. Les clés comprennent les templates, les formes, les 12 matrices et les 6 matrices relatives, ainsi que le contexte. Les matrices sont copiées dans les clés ; aucune rotation n'est arrondie.
- Les changements de pièces ou d'identités des cubes invalident la géométrie concernée. Une nouvelle forme sous un template existant ne récupère pas les anciennes surfaces. Un rechargement libère les préparations et détache les anciennes injections. Les enfants du modèle sont relus indépendamment des tableaux de sommets.
- LowerJointSupport mémorise la résolution des champs valides et absents, avec le même parcours des superclasses. Les lectures et écritures relisent les objets courants. Les adaptations de cubes et de Sodium restent intactes.
- Le HUD mémorise la résolution des méthodes sans argument, avec le même ordre déclaré/public/superclasses et le même comportement de repli. Les scores ne sont pas mémorisés.

Le cache indépendant des mains de la PRE32 est conservé octet pour octet. Le clipping, les espacements, les rangs, les UV, les normales, les assets et les interfaces restent ceux de la PRE32.

## Comparaisons et contrôles effectués

| Contrôle | Résultat |
| --- | --- |
| Surfaces de référence | 320 états classic/slim, nouvelles formes, poses animées et transitions première/troisième personne : empreintes identiques PRE32/PRE33 |
| Cache complet et invalidations | 91 états supplémentaires : empreintes identiques, dont détachements, évictions, remplacement/restauration de cubes, nouvelle forme sous le même template et remise à zéro |
| Menus | 23 captures, cinq races et plusieurs échelles d'interface : pixels identiques PRE32/PRE33 ; 688 assertions |
| Mains | Régression native : 654 assertions et 55 instantanés sur le candidat ; helper de production PRE32 identique dans le JAR |
| Nouveaux PNG hors JAR | Coiffure 930, barbe 930 et tenue 930 reconnues ; corps 2, classic/slim et surfaces natives : 23 assertions |
| Monde réel de test | 18 scénarios, 138 assertions sur chaque version ; 560 images de rendu contrôlées sur le candidat, 593 sur le témoin |
| Géométrie en monde | Cinq comparaisons par version entre l'état courant et une découpe refaite sans préparation conservée : identiques |
| Réflexion du HUD et des articulations | 2 518 assertions sur le candidat final : lectures/écritures changeantes, objets distincts, méthodes héritées/privées/d'interface, champs absents, repli sur exception et arguments nuls |
| Scoreboard Minecraft | Six changements de valeur puis suppression d'objectif : lectures identiques via un détenteur compatible ; repli du joueur natif identique |
| Audit du bytecode | 105 méthodes préservées ; huit points Nexus et un résolveur privé HUD ciblés ; calculs des surfaces et lectures de scores préservés |
| Archives | ZIP intègres ; reconstruction déterministe des deux JAR à partir de leur base et du patch de classes ; empreintes contrôlées |

Les scénarios en monde couvrent les mains ordinaires et animées, la troisième personne, l'inventaire, F1, une carte à deux mains, un rechargement réel, une passe de modèle supplémentaire, un détachement avant les mains, un changement de variante, le modèle complet de Player Animation Library, l'ouverture et la fermeture de la caméra réelle de dialogue, l'animation `off_hand_channeling` de Spell Engine et son arrêt. Le test de canalisation active le contrôleur et l'asset d'animation réels ; il ne simule pas un combat, des dégâts ou tous les effets de chaque sort.

Le contrôle de scoreboard utilise le vrai `class_269` et ses vrais scores, avec un objet de test exposant le getter attendu par le HUD. Dans Minecraft 1.21.11, le joueur natif n'expose pas les deux noms de getter recherchés par la 1.3.3 ; son repli à zéro demeure identique. OPT1 optimise ce chemin existant et conserve la synchronisation réseau et la priorité des sources de valeurs. Il ne change pas ce mécanisme en introduisant une nouvelle source de scores.

## Mesures du calcul de surfaces

Même client logiciel et mêmes mods de compatibilité pour les deux mesures finales. Après échauffement, trois échantillons par cas, temps CPU médian du thread de rendu par appel, en millisecondes. Les appels incluent l'application des poses du banc. Le nombre de découpes indiqué est celui de chacun des trois échantillons PRE33.

| Cas | PRE32, ms/appel | PRE33, ms/appel | Découpes PRE33 par échantillon |
| --- | ---: | ---: | --- |
{chr(10).join(rows)}

Sur les poses connues, le témoin refait 60 à 80 découpes par échantillon et le candidat zéro. Les tableaux sont réutilisés ; les cas alternant deux, quatre ou huit poses n'allouent plus de données de géométrie pendant leurs échantillons chauds. Après un détachement, le candidat recrée encore l'état d'injection, environ 50,6 ko par appel dans ce banc, tout en évitant la découpe.

Les poses relatives continuellement nouvelles restent autour de 78 ms/appel dans ce scénario dense. Le cas de poses de base nouvelles mesure environ 9,08 ms sur le témoin et 10,13 ms sur le candidat : ce cas n'est pas amélioré, et ces trois échantillons ne permettent pas d'attribuer cette différence à une variation de mesure ou au surcoût du cache. Ces chiffres sont conservés dans le rapport. Il n'y a pas de facteur de gain global de FPS déduit de ces mesures.

## Périmètre des fichiers

Nexus : {audit['nexus']['identical_entries']} entrées originales identiques, dont les {audit['nexus']['identical_assets']} assets. Quatre entrées modifiées : les métadonnées et trois classes ciblées. Six classes de support ajoutées. Aucune entrée supprimée.

HUD : {audit['hud']['identical_entries']} entrées originales identiques, dont les {audit['hud']['identical_assets']} assets. Seuls les métadonnées et le résolveur dans CapitaleRpHudClient changent ; deux classes ajoutées. Les autres classes client, classes serveur, mixins et messages réseau sont identiques.

| Fichier | SHA-256 |
| --- | --- |
| Nexus PRE32 original | `{audit['nexus']['base_sha256']}` |
| Nexus PRE33 livré | `{audit['nexus']['candidate_sha256']}` |
| HUD 1.3.3 original | `{audit['hud']['base_sha256']}` |
| HUD OPT1 livré | `{audit['hud']['candidate_sha256']}` |

## Environnement et portée

Minecraft 1.21.11, Java Temurin 21.0.12.1, Fabric Loader 0.19.5, rendu Mesa llvmpipe OpenGL 4.5 et écran de test 1920×1080. Dépendances pertinentes : Fabric API 0.141.6, Skin Layers 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2, Iris 1.10.7 sans shaderpack, Accessories 1.4.3-beta, owo 0.13.0, EasyNPC 7.12.1, PAL 1.1.10, Spell Engine RC17, Spell Power RC9, RPG RC3 et dialogue b10. Les dépendances servent au banc ; elles ne sont pas remplacées par cette livraison.

Les captures vérifient un monde isolé et des personnages de test. Lion-Port, ses PNJ, ses armures personnalisées, Distant Horizons, le shaderpack et le modpack complet ne sont pas reproduits ici. Les gains de FPS pendant tes sorts précis restent à mesurer dans cet environnement. Les protections de rendu sont évaluées par conservation du bytecode, comparaison des géométries, comparaison des menus et vérifications des injections en jeu ; les tests ne présentent pas chaque vue de tous les futurs assets possibles.

Les sources, le banc, les CSV, les empreintes des captures et les patches reconstructibles sont conservés dans la branche PRE33 liée à l'issue #163, empilée sur la PRE32 (#162). Aucune fusion dans main.
'''
(R/'PRE33-verifications.md').write_text(report)
(R/'README.md').write_text('''# Nexus PRE33 et HUD 1.3.3 OPT1

Optimisation des préparations exactes des surfaces de personnage et des recherches réflexives. Base frozen PRE32 commit e6e01dfac5bf08cef696f2c50400dbb2fd849a0e. Issue #163. Branche empilée sur fix/nexus-pre32-arm-cache.

Lire PRE33-verifications.md et les données qa pour les résultats et leurs limites.

## Reconstruction des binaires

Avec Python 3 et les JAR originaux portant les empreintes des manifests :

    python assemble.py nexus /chemin/NexusCharacters-PRE32.jar /sortie/NexusCharacters-PRE33.jar
    python assemble.py hud /chemin/capitale_rp_hud_BETA_1_3_3_RACE_HEALTH_MODIFIER_COMPAT.jar /sortie/capitale_rp_hud_OPT1.jar

L'assemblage vérifie les bases, chaque classe du patch et le SHA du JAR complet, puis remplace atomiquement la sortie.

## Compilation et banc

Le banc local a une arborescence pre33 pour ce dossier et pre31 pour le JDK/runtime Minecraft reconstruits. Les scripts de préparation du runtime sont dans la référence PRE31 du dépôt. Placer la base PRE32 dans pre33/base/NexusCharacters-PRE32.jar et le HUD original sous pre33/hud-base. Les versions des mods de test sont listées dans qa/test-mods.json.

    python pre33/build.py
    python pre33/run_client.py --baseline --diagnostics
    python pre33/run_client.py --compat --diagnostics
    python pre33/run_client.py --baseline --pose-only --compat --diagnostics
    python pre33/run_client.py --pose-only --compat --diagnostics
    python pre33/run_client.py --baseline --world-diag --compat
    python pre33/run_client.py --world-diag --compat
    python pre33/run_client.py --future-assets --compat
    python pre33/run_client.py --reflection-only --compat

Ne pas lancer deux clients avec le même numéro de PRE33_DISPLAY. Le runner conserve un état et un classpath de banc par contexte, exige un fichier de fin récent et échoue sur les marqueurs de régression.

Les composants de calcul géométrique originaux restent dans la base. Patch33.java ajoute les points de cache et délègue deux résolutions de champs et une résolution de méthode ; Verify33.java reconstruit les instructions avant points de cache et vérifie toutes les autres méthodes des classes touchées.
''')
print('REPORT33_WRITTEN',len(report.encode()))
