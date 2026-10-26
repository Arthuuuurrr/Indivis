"""Package only after verify_release.py has accepted the frozen JAR/evidence."""
from pathlib import Path
import json,csv,zipfile,hashlib
R=Path(__file__).resolve().parents[1]
audit=json.loads((R/'qa/release-audit.json').read_text());perf=json.loads((R/'qa/comparison.json').read_text())
jar=R/'NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1.jar'
assert hashlib.sha256(jar.read_bytes()).hexdigest()==audit['sha256']==perf['candidate_nexus_sha256']
out=R/'release';out.mkdir(exist_ok=True)
csv_path=out/'MESURES_CPU.csv'
with csv_path.open('w',newline='') as f:
 writer=csv.DictWriter(f,fieldnames=list(perf['rows'][0]));writer.writeheader();writer.writerows(perf['rows'])
lines=['# Nexus PRE35 SI1 OPT1 — vérifications',
 '', 'Correctif du calcul des surfaces 3D partagé par les joueurs et les PNJ Nexus/EasyNPC.',
 'Le rendu 3D n’est ni désactivé ni remplacé par une version plate. Aucun LOD, saut d’animation ou arrondi de pose ajouté.',
 '', '## Installation', '',
 'Remplacer l’ancien JAR Nexus PRE35 SI1 par le JAR OPT1, sans garder deux Nexus dans `mods`.',
 'Conserver la liaison Indivis-Nexus-NPC PREVIEW1, EasyNPC et 3D Skin Layers. Aucun nouveau fichier de configuration requis.',
 'Les presets et assets existants sont conservés. Ce correctif n’ajoute pas les nouveaux presets de morphologie encore en préparation.',
 '', '## Ce qui change', '',
 '- Index des volumes candidats par grille ; fin des collectes/tri répétés pour chaque polygone.',
 '- Bornes des fragments calculées une fois ; exclusion exacte des cellules qui ne peuvent rien recouper.',
 '- Réutilisation par partie du corps seulement si les entrées et dépendances sont strictement identiques.',
 '- Projections de sommets réutilisées pendant le test des six faces d’un même volume.',
 '- Cache par workspace borné à six profils et 8 Mio estimés ; invalidation avec les états PRE35.',
 '', '## Contrôles terminés', '',
 '- 5 400 cas de découpe contre le code PRE35 conservé, y compris les rangs égaux et les ajouts à une grille.',
 '- 10 368 tests de faces coplanaires/proches des seuils, normales inversées et transformations non uniformes.',
 f"- {int(audit['oracle']['oraclePolygons']):,} polygones comparés en jeu sur les mêmes entrées, sans différence de topologie, coordonnées, normales ou UV.".replace(',', ' '),
 f"- {int(audit['reuse']['hits']):,} réutilisations vérifiées par cet oracle exact.".replace(',', ' '),
 '- 216 états de PNJ/joueurs identiques entre processus PRE35 et OPT1, dont des poses continuellement nouvelles.',
 '- 320 états de géométrie + 91 états d’invalidation/cache + 55 états de bras : identiques à PRE35.',
 f"- {sum(v['difference_box'] is None for v in audit['menu_pixels'])}/23 captures de menus identiques pixel par pixel (création/sélection, cinq races, plusieurs échelles et rotations).",
 '- Corps normal/slim, remplacement de cubes/shapes, changement de perspective et vidage des caches contrôlés.',
 '- Nouveaux identifiants 930 de coiffure, barbe et tenue découverts et rendus sur un véritable EasyNPC (ressources ajoutées seulement pour le test).',
 '- Rechargement d’un vrai monde de test : dix PNJ, UUID, recettes et cinq tags conservés ; vues de face/dos contrôlées.',
 f"- {audit['unchanged_assets']} entrées d’assets inchangées octet pour octet. Classes réseau, persistance, UI et cosmétiques spécialisées inchangées.",
 '', '## Mesures CPU', '',
 'Temps du thread de rendu pour extraire l’état et préparer les modèles natifs. La colonne « entités » est le coût total du groupe par itération, pas un coût par individu.',
 'Après 320 échauffements à trois entités en poses changeantes, puis 32 échauffements par cas : médiane de cinq séries de 24 itérations. Poses nouvelles, mêmes recettes et mêmes entrées avant/après.',
 'Sans oracle ni enregistreur pendant le chronométrage final. Les allocations désignent les octets créés par itération, pas la mémoire conservée.',
 'Ce ne sont pas des FPS : le dessin GPU, le reste du modpack et le coût d’une scène complète sont hors de cette mesure. Le client de test utilise un rendu logiciel.',
 '', '| Type | Pose | Entités | PRE35 CPU (ms) | OPT1 CPU (ms) | Réduction CPU |',
 '| --- | --- | ---: | ---: | ---: | ---: |']
for row in perf['rows']:
 kind='PNJ' if row['kind']=='npc' else 'Joueur'
 pose={'static':'Immobile','head_turn':'Tête mobile','walking':'Marche'}[row['pose']]
 lines.append(f"| {kind} | {pose} | {row['entities']} | {row['before_cpu_ms']:.3f} | {row['after_cpu_ms']:.3f} | {row['cpu_reduction_pct']:.1f} % |")
lines += ['', 'Tous les cas et les allocations figurent dans MESURES_CPU.csv ; les résultats moins favorables ne sont pas omis.',
 '', '## Portée et limites', '',
 'Les tests portent sur Minecraft 1.21.11, Fabric 0.19.5, Nexus PRE35 SI1/OPT1, la liaison PREVIEW1 et EasyNPC 7.16.0, avec les dépendances du banc de test.',
 'Le modpack de production complet, les véritables sorts et la caméra de dialogue n’ont pas été reproduits ici. Le chemin partagé des joueurs a été testé avec de vrais OtherClientPlayer de test, sans connexion Mojang authentifiée.',
 'Le compte hors ligne synthétique produit les avertissements PRE35 de manifest/owner du vault v0.7.1 : la persistance des personnages de production n’est donc pas déclarée validée. Le rechargement NBT EasyNPC est, lui, testé séparément.',
 'Aucun déploiement sur le serveur de production ni aucune fusion dans main. Les gains de ce banc ne signifient pas disparition de tous les ralentissements du modpack.',
 '', '## Identité du fichier', '', f"SHA-256 OPT1 : `{audit['sha256']}`", f"SHA-256 PRE35 SI1 : `{audit['base_sha256']}`", '']
report=out/'VERIFICATIONS_NEXUS_OPT1.md';report.write_text('\n'.join(lines))
notes=out/'LISEZ-MOI.txt';notes.write_text('NEXUS PRE35 SI1 OPT1\n\nRemplacer uniquement l’ancien JAR Nexus par le JAR OPT1.\nNe pas conserver deux JAR Nexus dans mods.\nGarder la liaison PREVIEW1, EasyNPC et 3D Skin Layers.\nAucun fichier de configuration supplémentaire.\n\nLire VERIFICATIONS_NEXUS_OPT1.md pour les résultats et les limites des tests.\n')
archive=out/'NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1.zip'
with zipfile.ZipFile(archive,'w',compression=zipfile.ZIP_DEFLATED) as z:
 for p in [jar,notes,report,csv_path]:z.write(p,p.name)
 for name in ['release-audit.json','comparison.json','exact-index-pass.txt']:z.write(R/'qa'/name,'qa/'+name)
 for role in ['baseline','candidate']:
  root=Path(perf[role]['game'])/'evidence'
  for name in ['opt-perf.csv','opt-geometry.csv']:z.write(root/name,'qa/'+role+'-'+name)
with zipfile.ZipFile(archive) as z:assert z.testzip() is None
manifest={'jar':str(jar),'report':str(report),'archive':str(archive),'sha256':audit['sha256']}
(R/'qa/deliverables.json').write_text(json.dumps(manifest,indent=2)+'\n');print(json.dumps(manifest))
