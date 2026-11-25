"""Only package a hash-pinned binary after correctness and repeated A/B passes."""
from pathlib import Path
import collections,csv,hashlib,json,math,statistics,zipfile
R=Path(__file__).resolve().parent;P=R.parent/'presets_import';PERF=R.parent/'perf_bb1_20261125';OUT=R/'out';QA=R/'qa'
B=json.loads((QA/'build.json').read_text());JAR=Path(B['jar']);SHA=B['sha256'];assert hashlib.sha256(JAR.read_bytes()).hexdigest()==SHA
F=json.loads((QA/'functional.json').read_text());M=json.loads((QA/'performance-runs.json').read_text());assert F['jar_sha256']==M['candidate_sha256']==SHA
REVIEW=json.loads((QA/'visual-review.json').read_text());assert REVIEW['jar_sha256']==SHA and REVIEW['reviewed']
FUTURE=json.loads((QA/'future.json').read_text());assert FUTURE['nexus_sha256']==SHA and 'FUTURE_ASSETS hair=930 beard=930 outfit=930'in FUTURE['pass']
assert B['variant']=='production' and B['disabled']=='memo'
fields=['render_cpu_ns','render_wall_ns','interval_ns','allocated_bytes','native_ns','native_alloc_bytes','native_calls','cosmetic_calls','recomputes']
def percentile(v,q):
 a=sorted(v);x=(len(a)-1)*q;i=math.floor(x);return a[i]+(a[min(i+1,len(a)-1)]-a[i])*(x-i)
rows=[];per=[];records=[];stages=[];dependencies=None;classes=None
for label in M['labels']:
 rec=json.loads((PERF/(label+'.json')).read_text());family='candidate' if '-candidate-'in label else'reference'
 assert not rec['pilot'] and not rec['freeze'] and not rec['jfr'] and not rec['diagnostics'] and rec['animated_only']
 assert rec['jar_sha256']==(SHA if family=='candidate'else B['base_sha256'])
 ev=Path(rec['evidence']);assert not(ev/'failure.txt').exists();raw=list(csv.DictReader((ev/'frames.csv').open()));assert len(raw)==1040
 rec['environment']=json.loads((ev/'environment.json').read_text());rec['family']=family;records.append(rec)
 deps={n:h for n,h in rec['mods'].items()if n not in ['NexusCharacters.jar','perf-harness.jar']}
 if dependencies is None:dependencies=deps
 assert deps==dependencies
 with zipfile.ZipFile(Path(rec['game'])/'mods/perf-harness.jar')as z:code={n:hashlib.sha256(z.read(n)).hexdigest()for n in z.namelist()if n.endswith('.class')}
 if classes is None:classes=code
 assert classes==code
 groups=collections.defaultdict(list)
 for row in raw:
  assert row['pose']=='animated_orbit' and int(row['cosmetic_calls'])>=int(row['count'])
  groups[(row['variant'],row['kind'],int(row['count']))].append(row);rows.append({'run':label,'family':family,**row})
 for (variant,kind,count),group in groups.items():
  result={'run':label,'family':family,'variant':variant,'kind':kind,'count':count,'samples':len(group)}
  for f in fields:
   v=[int(x[f])for x in group];result[f]={'median':statistics.median(v),'p95':percentile(v,.95)}
  if count:assert result['recomputes']['median']>=count,'Animated geometry unexpectedly frozen'
  per.append(result)
 for stage in json.loads((ev/'stages.json').read_text()):
  if stage['scene']['variant']=='authored':
   for name in ['BlockbenchModelBaker.bakes','BlockbenchTextureCache.creates']:assert stage['cache_end'][name]==stage['cache_start'][name]
  stages.append({'run':label,'family':family,**stage})
groups=collections.defaultdict(list)
for row in per:groups[(row['family'],row['variant'],row['kind'],row['count'])].append(row)
aggregate=[]
for (family,variant,kind,count),rs in groups.items():
 assert len(rs)==3
 row={'family':family,'variant':variant,'kind':kind,'count':count,'repeats':3,'frames':sum(x['samples']for x in rs)}
 for f in fields:
  vals=[x[f]['median']for x in rs];row[f]={'median':statistics.median(vals),'min_run':min(vals),'max_run':max(vals),'p95':statistics.median(x[f]['p95']for x in rs)}
 aggregate.append(row)
def get(f,v,k,n):return next(x for x in aggregate if(x['family'],x['variant'],x['kind'],x['count'])==(f,v,k,n))
def value(row,f,scale):return row[f]['median']/scale
def fr(x):return f'{x:.2f}'.replace('.',',')
def gain(a,b):return 100*(1-b/a)
data={'jar_sha256':SHA,'runs':records,'per_run':per,'aggregates':aggregate,'stages':stages,'identical_harness_classes':classes,'identical_dependencies':dependencies,'frames':len(rows)}
(OUT/'mesures-OPT1.json').write_text(json.dumps(data,indent=2,ensure_ascii=False)+'\n')
with(OUT/'mesures-par-image-OPT1.csv').open('w',newline='',encoding='utf-8-sig')as f:
 w=csv.DictWriter(f,fieldnames=rows[0].keys());w.writeheader();w.writerows(rows)
flat=[]
for row in aggregate:flat.append({**{k:row[k]for k in ['family','variant','kind','count','repeats','frames']},**{f+'_'+s:row[f][s]for f in fields for s in ['median','p95','min_run','max_run']}})
with(OUT/'synthese-OPT1.csv').open('w',newline='',encoding='utf-8-sig')as f:
 w=csv.DictWriter(f,fieldnames=flat[0].keys());w.writeheader();w.writerows(flat)
lines=['# Nexus BB1.OPT1 — correctif et vérifications du 25 novembre 2026','',
'## Installation','',
'Fabric **1.21.11** : remplacer le précédent JAR Nexus par `'+JAR.name+'`. Ne pas garder deux versions de Nexus dans `mods/`. Conserver le mod de liaison Indivis–Nexus–NPC pour EasyNPC. Aucun preset, monde ni fichier de personnage n’est à supprimer.','',
'Version interne : `1.0.0-beta.35+indivis.1.21.11.SI1.OPT3.BB1.OPT1`. SHA-256 : `'+SHA+'`.','',
'## Ce qui change','',
'- Les variantes de couleur réutilisent les préparations géométriques identiques, sans partager leurs textures ni leur identité. L’égalité vérifie positions, UV, normales, rangs et volumes, bit pour bit.',
'- Découpe et écriture des surfaces avec moins d’objets temporaires ; réutilisation des listes et wrappers de travail, sans réutiliser les buffers finaux du rendu différé.',
'- Cache de parties : au plus 32 profils / 16 Mio estimés par modèle, avec quatre variantes exactes par partie. Cache local canonique : 8 Mio par modèle ; interner géométrique : 256 clés / 8 Mio estimés. Ces plafonds sont des budgets de cache, pas des mesures de RAM totale.',
'- Invalidation conservée sur changement de corps/cubes, remplacement de forme et rechargement des ressources. Aucune liste de coiffures, barbes ou vêtements n’est codée en dur dans cette optimisation.',
'- Pas de réduction de 3D, de distance de détail, de cadence des animations ni de précision des poses. Les 5 191 entrées `assets/` de la BB1 sont identiques. Répartition des presets, couleurs et options des menus inchangées.',
'- Une mémoïsation globale des transformations a été essayée puis désactivée : sa gestion coûtait trop sur les poses distinctes. Les essais exploratoires ne sont pas mélangés aux mesures finales.','',
'## Mesures finales A/B','',
'Six clients réels, exécutés successivement A1/B1/B2/A2/A3/B3 : trois BB1 d’origine, trois JAR finaux strictement identiques à celui livré. **'+str(len(rows))+' images mesurées** après échauffement. Mêmes classes de mesure et mêmes dépendances, vérifiées par SHA-256. Aucun client concurrent.','',
'Animations tête/membres distinctes par entité et caméra en orbite ; 0/3/8/24 PNJ EasyNPC, 3/8 joueurs OtherClientPlayer. Pour chaque image, le nombre d’appels de couche cosmétique doit être au moins égal au nombre de sujets prévu. Les recomputations continuent : les animations ne sont pas figées.','',
'Deux familles de scènes, comparées chacune à rendu constant entre A et B : anciens assets, puis nouveaux modèles Blockbench chargés avec douze teintes. Cinq profils hérités : haut-elfe, elfe des bois, nordique, nain et naine ; ce ne sont pas cinq races distinctes.','',
'Pour chaque scène : 36 images d’échauffement puis 96 mesurées ; pour 24 PNJ : 20 puis 40. Les chiffres ci-dessous sont les médianes des trois médianes ; le CSV conserve les P95 et les bornes entre exécutions.','',
'**Temps CPU du thread de rendu, en millisecondes par image — pas des FPS prédits sur le PC du joueur.**','',
'| Scène animée | Assets | BB1 (ms) | OPT1 (ms) | Baisse CPU | Allocations surfaces BB1 → OPT1 (Mio/image) |','|---|---|---:|---:|---:|---:|']
for kind,counts in [('npc',[3,8,24]),('player',[3,8])]:
 for variant,title in [('legacy','Origine'),('authored','Nouveaux modèles')]:
  for n in counts:
   a=get('reference',variant,kind,n);b=get('candidate',variant,kind,n);x=value(a,'render_cpu_ns',1e6);y=value(b,'render_cpu_ns',1e6)
   lines.append(f'| {n} '+('PNJ'if kind=='npc'else'joueurs')+f' | {title} | {fr(x)} | {fr(y)} | {fr(gain(x,y))} % | {fr(value(a,"native_alloc_bytes",1048576))} → {fr(value(b,"native_alloc_bytes",1048576))} |')
lines+=['','Une baisse négative signifie un ralentissement mesuré, pas un gain. Les scènes d’origine et de nouveaux modèles ne doivent pas être comparées entre elles pour attribuer un gain au correctif.','',
'**Lecture des résultats : le gain CPU est surtout net pour les 24 PNJ aux recettes variées. Sur les petits groupes, les écarts restent faibles ou variables ; ce test ne démontre pas une amélioration CPU uniforme. La réduction des allocations de surfaces est en revanche constatée dans les deux familles de scènes.**','',
'Aucun nouveau bake Blockbench ni aucune nouvelle texture recolorée pendant les plages mesurées après échauffement. Aucun des chronométrages n’utilise les oracles de vérification ni le contrôle diagnostique à poses figées.','',
'## Vérifications du JAR final','',
'- **116 042 comparaisons** : 20 000 polygones aléatoires, découpes six plans, transformations/écriture, 16 000 nettoyages indexés, cas dégénérés, seuils epsilon, signes de zéro, changements d’UV et de forme. Comparaisons exactes des bits flottants à la BB1.',
'- **411 états de géométrie identiques à la BB1** : 320 états de corps classique/slim et perspectives + 91 états de poses/invalidation ; 55 états de bras en première personne ; 23 écrans et contrôles des sliders, rotation, couleurs et HUD.',
'- Catalogue complet des **91 modèles**, 182 captures face/dos et 76 variantes de teintes dans le client réel ; inspection visuelle complémentaire des captures finales, sans rendu de substitution.',
'- PNJ : cinq nouveaux profils raciaux, cinq anciens profils et cinq presets EasyNPC natifs, export éditeur, conservation des champs/UUID/tags et rechargement d’une sauvegarde isolée de 15 PNJ.',
'- Nouveaux assets : trois PNG d’essai supplémentaires (coiffure, barbe classique et tenue, ID 930), découverts automatiquement puis affichés sur un vrai PNJ EasyNPC. Le JAR livré n’a pas été modifié pour ce test.',
'- Oracles supplémentaires activés dans un client séparé : **552 états PNJ/joueurs animés**, dont 14 585 042 comparaisons de découpe. Modification de la configuration Skin Layers 1,05 → 1,15 → 1,05 et purge des caches. Ces compteurs incluent les réexécutions de référence, pas autant de poses distinctes.',
'- API commune : **292 vérifications avec classes client interdites**. Il s’agit d’un contrôle de liaison de l’API, pas du démarrage complet d’un serveur dédié.',
'- Audit binaire : **273 classes inchangées**, 39 corps de méthodes préservés dans les classes ciblées ; aucune classe du banc de mesure ni de pose figée incluse dans le JAR. Les 5 191 entrées d’assets sont inchangées.','',
'## Portée','',
'Rendu OpenGL logiciel **llvmpipe**, Java 21, quatre processeurs déclarés à Java et quatre threads du moteur logiciel ; 800 × 450, distance de rendu 2, VSync désactivée, sans shaderpack. EntityCulling est exclu du banc pour ne pas masquer des sujets ; cette exclusion ne modifie pas le JAR livré.','',
'Les gains ci-dessus portent sur la préparation/rendu des personnages dans ces scènes isolées. Ils ne couvrent pas le coût complet de Lion-Port, des sorts, des particules, des shaders ou du reste du modpack. Aucun FPS matériel n’est extrapolé. Les avertissements Mojang/Realms hors ligne et de persistance non configurée des mondes de test ne sont pas une validation du réseau de production.','',
'Le ZIP contient le JAR installable, ce rapport, les CSV et les preuves synthétiques. Les profils bruts JVM et leurs variables d’environnement ne sont pas distribués.']
lines+=['','Code, générateur et instantané du banc : [PR #188](https://github.com/Arthuuuurrr/Indivis/pull/188), laissée en brouillon pour revue ; aucun déploiement ni fusion automatique.']
report=OUT/'Nexus-BB1-OPT1-Verification-2026-11-25.md';report.write_text('\n'.join(lines)+'\n')
checksums=OUT/'SHA256SUMS-OPT1.txt';checksums.write_text(SHA+'  '+JAR.name+'\n')
bundle=OUT/'Nexus-BB1-OPT1-1.21.11.zip'
with zipfile.ZipFile(bundle,'w',zipfile.ZIP_DEFLATED)as z:
 for f in [JAR,report,checksums,OUT/'mesures-OPT1.json',OUT/'mesures-par-image-OPT1.csv',OUT/'synthese-OPT1.csv']:z.write(f,f.name)
 for n in ['kernel.txt','server-linkage.txt','bytecode-audit.txt','functional.json','performance-runs.json','visual-review.json','future.json']:z.write(QA/n,'verifications/'+n)
 for f in sorted((QA/'review').glob('*.png')):z.write(f,'captures-reelles/planches/'+f.name)
 for name in REVIEW['full_resolution']:z.write(P/'previous/npc/qa'/name,'captures-reelles/'+name.replace('/','-'))
 for rec in F['records']:
  evidence=Path(rec['game'])/'evidence';z.write(evidence/'pass.txt','verifications/'+rec['mode']+'-pass.txt')
print('OPT1_PACKAGE_PASS',SHA,'frames='+str(len(rows)),str(bundle),flush=True)
