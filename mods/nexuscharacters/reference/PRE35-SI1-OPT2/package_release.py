"""Only package a JAR whose exact bytes passed verify_release.py."""
from pathlib import Path
import csv,hashlib,json,shutil,zipfile
R=Path(__file__).resolve().parent;OUT=R/'out'
a=json.loads((R/'qa/release-audit.json').read_text());perf=json.loads((R/'qa/comparison.json').read_text())
jar=OUT/'NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT2.jar'
assert hashlib.sha256(jar.read_bytes()).hexdigest()==a['sha256']==perf['candidate_sha256']
lines=['# Nexus PRE35 SI1 OPT2 — vérifications du 28 octobre 2026','',
'Version : `1.0.0-beta.35+indivis.1.21.11.SI1.OPT2`. Correctif cumulatif : OPT1 est inclus.','',
'## Installation','',
'Fermer complètement Minecraft. Remplacer l’ancien JAR Nexus dans le dossier `mods` de l’instance réellement utilisée. Ne conserver qu’un JAR Nexus. Le chargement côté client est indispensable pour profiter du correctif de rendu.',
'Conserver la liaison Indivis-Nexus-NPC PREVIEW1, EasyNPC et Skin Layers. Aucun nouveau fichier de configuration, preset ou asset à installer. Aucun déploiement automatique sur le serveur.','',
'Ligne attendue dans le journal du client après redémarrage :','',
'```text','nexuscharacters 1.0.0-beta.35+indivis.1.21.11.SI1.OPT2','```','',
'## Ce qui change','',
'- Listes de sommets et de polygones dimensionnées au besoin pendant les transformations, pour éviter leur surallocation.',
'- Mêmes appels Vertex.transform, opérations JOML, coordonnées, normales et UV. Aucun recyclage des objets retournés.',
'- Aucun LOD, suppression de relief, saut d’animation ou changement d’EPS/espacement.',
'- Les nouvelles tables de requêtes, listes de fragments recyclées et recherches de classes mémorisées ont été retirées après les mesures. Elles réduisaient certaines allocations mais ne donnaient pas un gain CPU fiable, avec des ralentissements dans plusieurs cas. Le clipping SpatialSurfaceCleaner, GenericSkinLayerSupport et LowerJointSupport restent identiques à OPT1.','',
'## Contrôles terminés sur ce JAR','',
'- Compilation reproductible Java Temurin 21.0.13+9 ; 4966 entrées d’assets inchangées octet pour octet.',
'- 2400 cas de transformation (28800 polygones), dont entrées non finies/singulières et propriété des sorties.',
'- 10368 tests de faces extérieures et 5400 configurations/cas de découpe contre le code PRE35 conservé.',
'- 13 méthodes originales de SurfaceGeometry préservées instruction par instruction ; la transformation originale est conservée comme oracle.',
f"- Oracle en véritable client : {a['oracle']['oraclePolygons']} polygones de découpe et {a['transforms']['oraclePolygons']} polygones transformés comparés valeur flottante par valeur flottante ; aucune différence.",
f"- {a['reuse']['hits']} réutilisations de parties exercées avec vérification de l’oracle.",
'- 216 états PNJ/joueurs identiques entre les trois processus PRE35 SI1, OPT1 et OPT2 ; poses réellement nouvelles pendant les mesures.',
'- 23/23 captures de menus identiques pixel par pixel à OPT1, création/sélection, races et rotation.',
f"- {sum(a['geometry'].values())} états supplémentaires de géométrie, caches et bras identiques à OPT1, corps normal/slim.",
'- Export par le véritable éditeur, imports natifs EasyNPC, rendu des couches cosmétiques et absence de fuite d’état sur un PNJ ordinaire.',
'- Nouveaux IDs 930 de coiffure, barbe classique et tenue découverts et rendus sur un vrai PNJ, sans changement du code de production.',
'- Réglage Skin Layers modifié à chaud 1.05 → 1.15 → 1.05 sur un vrai modèle : variantes distinctes puis restauration.',
'- Monde réellement fermé puis rouvert : dix PNJ, UUID, recettes et cinq tags témoins conservés. Vues de face/dos inspectées.','',
'## Mesures CPU et allocations','',
'Préparation native groupée des modèles sur le thread de rendu : extraction d’état et calcul des surfaces. Ce ne sont pas des FPS et le dessin GPU, le monde et la soumission des accessoires ne sont pas inclus.',
'Même environnement, mêmes recettes, processus séparés : PRE35 SI1, OPT1 puis OPT2. Échauffement global de 320 images à trois entités, puis 1500 images statiques ou 64 images animées par cas. Médiane de cinq séries de 24 passages. Le nombre de recalculs est contrôlé pour exclure un faux gain par répétition de poses déjà en cache.',
'Oracle désactivé pendant les chronométrages. Toutes les lignes, y compris les variations défavorables, sont fournies dans MESURES_CPU.csv.','',
'| Type | Pose | Entités | SI1 CPU ms | OPT1 CPU ms | OPT2 CPU ms | SI1 allocations Mio | OPT2 allocations Mio |',
'| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: |']
for r in perf['rows']:
    lines.append('| '+('PNJ' if r['kind']=='npc' else 'Joueur')+' | '+{'static':'Immobile','head_turn':'Tête mobile','walking':'Marche'}[r['pose']]+' | '+str(r['entities'])+' | '+f"{r['pre35_cpu_ms']:.3f} | {r['opt1_cpu_ms']:.3f} | {r['opt2_cpu_ms']:.3f} | {r['pre35_allocated_MiB']:.3f} | {r['opt2_allocated_MiB']:.3f} |")
lines+=['','## Portée et limites','',
'Client Linux en rendu logiciel llvmpipe, Minecraft 1.21.11, Fabric Loader 0.19.5, Fabric API 0.141.6+1.21.11, EasyNPC 7.16.0 et liaison PREVIEW1 inchangée. Dépendances principales : Skin Layers 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2, Iris 1.10.7, PAL 1.1.10, owo 0.13.0, Cloth Config 21.11.153, Pufferfish Skills 0.19.0 et HUD OPT1.',
'Le pack de production complet, les véritables sorts/caméra de dialogue et les comptes Mojang authentifiés ne sont pas reproduits dans cette session OPT2. Aucun chiffre FPS de Lion-Port n’est revendiqué.',
'Le compte synthétique hors ligne conserve les avertissements PRE35 sur le manifeste propriétaire du vault. Les classes de persistance ne sont pas modifiées ; leur validation complète n’est pas prétendue. Le rechargement des véritables PNJ EasyNPC est vérifié séparément.',
'Les nouveaux assets restent soumis aux formats UV et au catalogue Nexus ; le test 930 n’est pas une garantie pour un PNG arbitraire mal formé.',
'Aucune fusion dans main et aucun déploiement sur le serveur de production.','',
'## Identité','',f"SHA-256 OPT2 : `{a['sha256']}`",f"SHA-256 OPT1 : `{perf['base_sha256']}`",f"SHA-256 PRE35 SI1 : `{perf['original_sha256']}`",'']
report=OUT/'VERIFICATIONS_NEXUS_OPT2.md';report.write_text('\n'.join(lines))
shutil.copy2(R/'qa/MESURES_CPU.csv',OUT/'MESURES_CPU.csv')
archive=OUT/'Nexus_OPT2.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
    for p in [jar,report,OUT/'MESURES_CPU.csv']:z.write(p,p.name)
    for n in ['build.json','exact.json','comparison.json','release-audit.json','bytecode-audit.txt']:z.write(R/'qa'/n,'verification/'+n)
    for label in ['opt1-perf','pre35-perf','opt2-perf']:
        record=json.loads((R/'previous/npc/qa'/('last-'+label+'.json')).read_text())
        for n in ['opt-perf.csv','opt-geometry.csv']:z.write(Path(record['game'])/'evidence'/n,'verification/'+label+'-'+n)
    p=Path(a['records']['opt2-reload']['game'])/'evidence'
    for n in ['presets-face.png','presets-dos.png']:z.write(p/n,'apercus/'+n)
with zipfile.ZipFile(archive) as z:assert z.testzip() is None and z.read(jar.name)==jar.read_bytes()
record={'jar':str(jar),'zip':str(archive),'report':str(report),'sha256':a['sha256']}
(R/'qa/deliverables.json').write_text(json.dumps(record,indent=2)+'\n');print('DELIVERABLES_READY',json.dumps(record))
