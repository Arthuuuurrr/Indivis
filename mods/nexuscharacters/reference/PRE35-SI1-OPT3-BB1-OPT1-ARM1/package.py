"""Package only the immutable JAR covered by correctness and six final clients."""
from pathlib import Path
import collections,csv,hashlib,json,math,re,statistics,zipfile

R=Path(__file__).resolve().parent;P=R.parent/'presets_import';PERF=R.parent/'perf_bb1_20261125';Q=R/'qa';OUT=R/'out'
B=json.loads((Q/'build.json').read_text());JAR=Path(B['jar']);SHA=B['sha256']
assert hashlib.sha256(JAR.read_bytes()).hexdigest()==SHA
F=json.loads((Q/'functional.json').read_text());M=json.loads((Q/'performance-runs.json').read_text());V=json.loads((Q/'visual-review.json').read_text())
assert F['jar_sha256']==M['candidate_sha256']==V['jar_sha256']==SHA and V['reviewed']
assert len(F['records'])==5 and len(M['labels'])==6
assert all(x['nexus_sha256']==SHA for x in F['records'])
assert 'FUTURE_ASSETS hair=930 beard=930 outfit=930'in F['records'][-1]['pass']
fields=['render_cpu_ns','render_wall_ns','interval_ns','allocated_bytes','native_ns','native_alloc_bytes','native_calls','cosmetic_calls','recomputes']
def percentile(values,q):
 a=sorted(values);x=(len(a)-1)*q;i=math.floor(x);return a[i]+(a[min(i+1,len(a)-1)]-a[i])*(x-i)
rows=[];per=[];records=[];stages=[];dependencies=None;classes=None;warm=[]
for label in M['labels']:
 rec=json.loads((PERF/(label+'.json')).read_text());family='candidate'if'-candidate-'in label else'reference'
 assert not any(rec[x]for x in ['pilot','freeze','jfr','diagnostics'])and rec['animated_only']
 assert rec['jar_sha256']==(SHA if family=='candidate'else B['base_sha256'])
 ev=Path(rec['evidence']);assert(ev/'pass.txt').exists()and not(ev/'failure.txt').exists()
 raw=list(csv.DictReader((ev/'frames.csv').open()));assert len(raw)==1040
 rec['environment']=json.loads((ev/'environment.json').read_text());rec['family']=family;records.append(rec)
 deps={n:h for n,h in rec['mods'].items()if n not in ['NexusCharacters.jar','perf-harness.jar']}
 if dependencies is None:dependencies=deps
 assert deps==dependencies,'Different dependencies'
 with zipfile.ZipFile(Path(rec['game'])/'mods/perf-harness.jar')as z:code={n:hashlib.sha256(z.read(n)).hexdigest()for n in z.namelist()if n.endswith('.class')}
 if classes is None:classes=code
 assert classes==code,'Different benchmark bytecode'
 groups=collections.defaultdict(list)
 for row in raw:
  assert row['pose']=='animated_orbit'and int(row['cosmetic_calls'])>=int(row['count'])
  groups[(row['variant'],row['kind'],int(row['count']))].append(row);rows.append({'run':label,'family':family,**row})
 for(variant,kind,count),group in groups.items():
  result={'run':label,'family':family,'variant':variant,'kind':kind,'count':count,'samples':len(group)}
  for f in fields:
   values=[int(x[f])for x in group];result[f]={'median':statistics.median(values),'p95':percentile(values,.95)}
  if count:assert result['recomputes']['median']>=count,'Animations unexpectedly frozen'
  per.append(result)
 for stage in json.loads((ev/'stages.json').read_text()):
  start,end=stage['cache_start'],stage['cache_end'];scene=stage['scene']
  if scene['variant']=='authored':
   for name in ['BlockbenchModelBaker.bakes','BlockbenchTextureCache.creates']:assert end[name]==start[name]
  if family=='candidate'and scene['count']:
   for name in ['ArmorPreparedSurface.bakes','ArmorContactVolumes.bakes','ArmorPreparedSurface.fallbacks']:assert end[name]==start[name],(label,scene,name)
   rigid=end['ArmorPreparedSurface.rigidPolygons']-start['ArmorPreparedSurface.rigidPolygons'];contact=end['ArmorPreparedSurface.contactPolygons']-start['ArmorPreparedSurface.contactPolygons']
   warm.append({'run':label,'scene':scene,'rigid_polygons':rigid,'contact_polygons':contact,'rigid_ratio':rigid/(rigid+contact),'source_bakes':0,'occluder_topology_bakes':0})
  stages.append({'run':label,'family':family,**stage})
groups=collections.defaultdict(list)
for row in per:groups[(row['family'],row['variant'],row['kind'],row['count'])].append(row)
aggregate=[]
for(family,variant,kind,count),rs in groups.items():
 assert len(rs)==3
 row={'family':family,'variant':variant,'kind':kind,'count':count,'repeats':3,'frames':sum(x['samples']for x in rs)}
 for f in fields:
  vals=[x[f]['median']for x in rs];row[f]={'median':statistics.median(vals),'min_run':min(vals),'max_run':max(vals),'p95':statistics.median(x[f]['p95']for x in rs)}
 aggregate.append(row)
def get(f,v,k,n):return next(x for x in aggregate if(x['family'],x['variant'],x['kind'],x['count'])==(f,v,k,n))
def value(row,f,scale):return row[f]['median']/scale
def fr(x):return f'{x:.2f}'.replace('.',',')
def gain(a,b):return 100*(1-b/a)
data={'jar_sha256':SHA,'runs':records,'per_run':per,'aggregates':aggregate,'stages':stages,'warm_preparation':warm,'identical_harness_classes':classes,'identical_dependencies':dependencies,'frames':len(rows)}
(OUT/'mesures-ARM1.json').write_text(json.dumps(data,indent=2,ensure_ascii=False)+'\n')
with(OUT/'mesures-par-image-ARM1.csv').open('w',newline='',encoding='utf-8-sig')as f:
 w=csv.DictWriter(f,fieldnames=rows[0].keys());w.writeheader();w.writerows(rows)
flat=[]
for row in aggregate:flat.append({**{k:row[k]for k in ['family','variant','kind','count','repeats','frames']},**{f+'_'+s:row[f][s]for f in fields for s in ['median','p95','min_run','max_run']}})
with(OUT/'synthese-ARM1.csv').open('w',newline='',encoding='utf-8-sig')as f:
 w=csv.DictWriter(f,fieldnames=flat[0].keys());w.writeheader();w.writerows(flat)
counts=F['records'][2]['pass'];maximum=re.search(r'ArmorPreparedSurface[^\n]*maxPositionError=([^ ]+) maxNormalError=([^\n]+)',counts)
lines=['# Nexus ARM1 — personnages préparés et vérification','',
'## Installation','',
'Fabric **1.21.11** : remplacer le précédent JAR Nexus par `'+JAR.name+'`. Une seule version de Nexus dans `mods/`. Conserver le mod de liaison Indivis–Nexus–NPC pour EasyNPC. Aucun monde, preset, personnage ou asset à supprimer. Le JAR est cumulatif : les correctifs OPT1 restent inclus.','',
'Version interne : `1.0.0-beta.35+indivis.1.21.11.SI1.OPT3.BB1.OPT1.ARM1`. SHA-256 : `'+SHA+'`.','',
'## Comportement « comme une armure »','',
'La géométrie locale des pièces du personnage est préparée au premier affichage d’une apparence, conservée et animée par les transformations ordinaires des os du modèle. Elle n’est pas régénérée intégralement à chaque pose. Cette préparation est côté client, sans nouveau format de personnage ni fichier de modèle à gérer.','',
'Il s’agit d’un pipeline **hybride**, pas d’une armure rigide qui ignorerait les chevauchements : les faces pouvant toucher une autre partie conservent la découpe animée OPT1. Les segments rigides sont réutilisés, les segments de contact restent recalculés. C’est nécessaire ici pour préserver les raccords et l’occlusion aux épaules, hanches et cou. L’ordre de soumission des faces reste conservé.','',
'- Hiérarchies spatiales locales préparées et volumes d’occlusion actualisés aux poses ; aucune fusion de volumes utilisée pour remplacer la découpe. Buffers finaux immuables, sûrs pour le rendu différé.',
'- Sources : cache partagé borné à 32 Mio estimés / 256 sources. Topologies d’occlusion : 16 Mio estimés / 32 recettes. Ce sont des budgets de cache, pas une mesure de la RAM totale. Purge sur le rechargement natif ; le chemin OPT1 reste disponible pour les cas non pris en charge.',
'- Le mécanisme traite les surfaces natives : cheveux, vêtements, barbes classiques, corps et raccords. Les modèles Blockbench déjà préparés conservent leur pipeline. Aucune liste d’assets n’est codée en dur dans ARM1 ; les nouveaux assets reconnus par le catalogue existant en bénéficient.',
'- Aucun LOD, animation figée, réduction de fréquence ou suppression de détail. Menus, couleurs, répartition des presets et chemin dédié des bras en première personne inchangés. Les **5 191 entrées d’assets** sont byte pour byte identiques à l’OPT1.','',
'## Mesures finales, OPT1 → ARM1','',
'Six clients Minecraft réels, successifs A1/B1/B2/A2/A3/B3, trois exécutions par version et **'+str(len(rows))+' images mesurées** après échauffement. Même code du banc et mêmes dépendances vérifiés par SHA-256 ; pas de client concurrent, JFR, oracle de vérification ou diagnostic à poses figées dans ces chronométrages. Le binaire final livré est celui des trois exécutions B.','',
'Caméra en orbite, têtes et membres animés distinctement par sujet. 0/3/8/24 PNJ EasyNPC ; 3/8 joueurs OtherClientPlayer. Chaque image contient au moins le nombre prévu d’appels de couche cosmétique. Les compteurs de préparation de pose continuent d’évoluer : les poses ne sont pas gelées.','',
'36 images d’échauffement puis 96 mesurées par scène ; 20 puis 40 pour 24 PNJ. Valeurs ci-dessous : médiane de trois médianes. Les CSV/JSON incluent les P95 et bornes entre exécutions. Comparer chaque famille à elle-même, pas anciens assets et modèles Blockbench entre eux.','',
'**Temps CPU du thread de rendu en ms/image, pas des FPS prédits sur le PC du joueur.**','',
'| Scène animée | Assets | OPT1 (ms) | ARM1 (ms) | Baisse CPU | Allocations surfaces OPT1 → ARM1 (Mio/image) |','|---|---|---:|---:|---:|---:|']
for kind,ns in [('npc',[3,8,24]),('player',[3,8])]:
 for variant,title in [('legacy','Origine'),('authored','Modèles Blockbench')]:
  for n in ns:
   a=get('reference',variant,kind,n);b=get('candidate',variant,kind,n);x=value(a,'render_cpu_ns',1e6);y=value(b,'render_cpu_ns',1e6)
   lines.append(f'| {n} '+('PNJ'if kind=='npc'else'joueurs')+f' | {title} | {fr(x)} | {fr(y)} | {fr(gain(x,y))} % | {fr(value(a,"native_alloc_bytes",1048576))} → {fr(value(b,"native_alloc_bytes",1048576))} |')
lines+=['','Une baisse négative indique un ralentissement mesuré. Les variations entre exécutions doivent être consultées avant d’interpréter un petit écart comme un gain robuste. Ces résultats sont des gains additionnels à OPT1 ; les anciens pourcentages BB1 → OPT1 ne sont pas réattribués à ARM1.','',
'Dans les **30 plages animées avec personnages du candidat**, après échauffement : **aucune reconstruction de source ni de topologie d’occlusion**, aucun recours nouveau au chemin de secours. Aucun nouveau bake Blockbench ni recoloration de texture pendant les plages mesurées. La proportion de polygones conservés rigides varie de **'+fr(min(x['rigid_ratio']for x in warm)*100)+' à '+fr(max(x['rigid_ratio']for x in warm)*100)+' %** selon la scène. Le reste reste dynamique : ARM1 ne supprime pas tout le coût de découpe.','',
'## Vérifications du même JAR','',
'- **6 266 comparaisons numériques**, dont 3 000 cas aléatoires de 32 polygones, 128 poses réutilisant effectivement le même tableau rigide, 128 poses à 144 volumes d’occlusion, grilles modifiées et seuils. Topologie, ordre et UV contrôlés.',
'- **411 états géométriques** (320 corps/perspectives + 91 poses/invalidation), 23 captures des menus. Les 126 lignes de première personne sont identiques en bits à l’OPT1. Les surfaces mondiales sont comparées en cours d’exécution au chemin OPT1 conservé.',
'- **91 modèles**, 182 captures face/dos, variantes de couleurs et 328 557 assertions dans le client réel. Inspection visuelle des neuf planches et de captures pleine résolution, dont les cheveux longs dans le dos et les PNJ face/dos.',
'- PNJ classiques/slim, cinq profils raciaux nouveaux et cinq hérités, cinq presets EasyNPC natifs ; sauvegarde puis recharge réelle de **15 PNJ** avec UUID, tags, recettes et champs d’éditeur conservés. Retour à un PNJ ordinaire vérifié.',
'- **552 états animés PNJ/joueurs** en vérification séparée ; '+re.search(r'ArmorPreparedSurface[^\n]*oracleCalls=(\d+)',counts).group(1)+' préparations comparées au résultat OPT1. Test de configuration Skin Layers 1,05 → 1,15 → 1,05 et purge des caches.',
'- **Nouveaux assets testés sans modifier le JAR** : coiffure, barbe classique et tenue ID 930 découvertes automatiquement et rendues sur un vrai PNJ EasyNPC.',
'- Audit binaire : **297 classes existantes inchangées**, 26 corps de méthodes préservés dans les deux classes ciblées ; seuls les points de préparation/purge sont redirigés. Aucune classe du banc ou de diagnostic incluse dans le JAR. API commune : **292 contrôles** avec classes client interdites ; ce n’est pas un démarrage complet de serveur dédié.','',
'## Précision du rendu','',
'Les portions rigides évitent l’ancien aller-retour monde/inverse. Leurs coordonnées locales peuvent donc changer de quelques arrondis flottants : ce n’est pas une égalité bit pour bit des 411 états mondiaux. Le contrat vérifié est : même nombre/ordre de quads, UV exacts en bits, positions et normales à `2e-6 + 8 ulp` par composante. Aucun arrondi ni quantification des poses n’est ajouté. Les zones de contact gardent l’algorithme original.','',
'Écarts maximaux observés dans le test aléatoire : position `2,98e-7`, normale `2,15e-7`. Dans le client animé avec PNJ : position `'+maximum.group(1)+'`, normale `'+maximum.group(2)+'`. Les images fixes ne suffisent pas à démontrer l’absence de scintillement pour tous les shaders et toutes les combinaisons possibles ; elles complètent les contrôles géométriques animés.','',
'## Portée et limites','',
'OpenGL logiciel **llvmpipe**, Java 21, quatre processeurs déclarés à Java et quatre threads de rendu logiciel, 800 × 450, distance de rendu 2, VSync désactivée, sans shaderpack. EntityCulling est exclu du banc afin de garder tous les sujets visibles ; cette exclusion ne modifie pas le JAR livré.','',
'Les tests isolent la préparation/rendu des personnages et ne reproduisent pas tout Lion-Port, la canalisation des sorts, les particules ou le reste du modpack. Aucun gain de FPS matériel ni suppression universelle des ralentissements n’est affirmé. L’architecture est modifiée et vérifiée dans le périmètre ci-dessus ; elle ne rend pas gratuites les zones de contact encore dynamiques.','',
'Code, générateur, tests et instantané du banc : [PR #190](https://github.com/Arthuuuurrr/Indivis/pull/190), brouillon pour revue. Pas de fusion ni déploiement automatique. Le ZIP contient le JAR identique, ce rapport, les mesures et des preuves de contrôle ; aucun profil JVM brut ni variable d’environnement.']
report=OUT/'Nexus-ARM1-Verification.md';report.write_text('\n'.join(lines)+'\n')
checksums=OUT/'SHA256SUMS-ARM1.txt';checksums.write_text(SHA+'  '+JAR.name+'\n')
bundle=OUT/'Nexus-ARM1-1.21.11.zip'
with zipfile.ZipFile(bundle,'w',zipfile.ZIP_DEFLATED)as z:
 for f in [JAR,report,checksums,OUT/'mesures-ARM1.json',OUT/'mesures-par-image-ARM1.csv',OUT/'synthese-ARM1.csv']:z.write(f,f.name)
 for n in ['kernel.txt','server-linkage.txt','bytecode-audit.txt','functional.json','performance-runs.json','visual-review.json']:z.write(Q/n,'verifications/'+n)
 for f in sorted((Q/'review').glob('*.png')):z.write(f,'captures-reelles/planches/'+f.name)
 for name in V['full_resolution']:z.write(P/'previous/npc/qa'/name,'captures-reelles/'+name.replace('/','-'))
 for rec in F['records']:z.write(Path(rec['game'])/'evidence/pass.txt','verifications/'+rec['mode']+'-pass.txt')
with zipfile.ZipFile(bundle)as z:assert hashlib.sha256(z.read(JAR.name)).hexdigest()==SHA and z.testzip()is None
print('ARMOR_PACKAGE_PASS',SHA,'frames='+str(len(rows)),bundle,flush=True)
