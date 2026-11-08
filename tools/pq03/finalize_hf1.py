"""Livraison HF1 : deux composants changés, journal R1 conservé, preuves liées aux octets."""
from pathlib import Path
import json,hashlib,re
import build
H=Path(__file__).resolve().parent;R=H/'reports';D=H/'docs';O=H/'output';BASE=H.parent/'pq03_fix_20261108/baseline_r1'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def read(name):return json.loads((R/name).read_text())
b=read('build.json');static=read('static.json');native=read('native_fixture.json');clients=[read('protocol_pq03_fixture.json'),read('protocol_pq02_with_pq03_fixture.json')]
for k,v in b['files'].items():assert sha(O/v['filename'])==v['sha256']==static['files'][k]['sha256']
digest=b['files']['mod']['sha256'];assert native['mod_production_sha256']==digest and all(c['jar_sha256']==digest and c['status']=='PASS' for c in clients)
native_count=int(re.match(r'PASS (\d+)',(R/'native.txt').read_text())[1]);restart_count=int(re.match(r'PASS (\d+)',(R/'restart.txt').read_text())[1]);rules_count=int(re.match(r'PASS (\d+)',(R/'rules.txt').read_text())[1])
protocol=[int(re.search(r'PASS (\d+) real client',(R/n).read_text())[1]) for n in ['protocol_pq03.txt','protocol_pq02_with_pq03.txt']]
assert native_count>=2113 and restart_count==9 and rules_count==98 and protocol==[302,566] and static['status']=='PASS'
for name in ['native_server.log','restart_server.log']:
 log=(R/name).read_text();assert not re.search(r'Failed to load function|Parsing error|Unknown function|\[Indivis PQ03.*(?:Exception|Error)',log)
assert '[quetes] 70 fiche(s) de quête, 0 POI, 0 erreur(s)' in (R/'native_server.log').read_text()
old=read('build.json');r1diff={}
for kind,name in build.FILES.items():
 old_name=name.replace('_HF1.zip','.zip').replace('-HF1-1.21.11.jar','-1.21.11.jar');a=build.read(BASE/'output'/old_name);z=build.read(O/name)
 r1diff[kind]={'changed':[n for n in a if a[n]!=z[n]],'added':sorted(set(z)-set(a)),'removed':sorted(set(a)-set(z)),'preserved':sum(a[n]==z[n] for n in a)}
assert not r1diff['quests']['changed'] and not r1diff['quests']['added']
assert len(r1diff['core']['changed'])==2 and len(r1diff['core']['added'])==1
(R/'hf1_diff_r1.json').write_text(json.dumps(r1diff,ensure_ascii=False,indent=2)+'\n')
evidence=['build.json','static.json','rules.txt','native.txt','native_fixture.json','native_server.log','restart.txt','restart_server.log','protocol_pq03.txt','protocol_pq03_fixture.json','protocol_pq02_with_pq03.txt','protocol_pq02_with_pq03_fixture.json','hf1_diff_r1.json']
validation={'status':'PASS','date':'2026-11-08','release':'PQ03-R1-HF1','components':b['files'],'native_assertions':native_count,'restart_assertions':restart_count,'rules_assertions':rules_count,'protocol_assertions':protocol,'static_checks':static['checks'],'journal_cards_loaded':70,'journal_errors':0,'fixture_functions':native['functions'],'changes_from_r1':r1diff,'source_sha256':b['source_sha256'],'native_test_sources':{p.relative_to(H/'regression/tests').as_posix():sha(p) for p in (H/'regression/tests').rglob('*.java')},'evidence_sha256':{n:sha(R/n) for n in evidence},'limitations':['Monde et connexions synthétiques, pas de rendu graphique réel.','Vrais événements Fabric, état b10, acteurs EasyNPC et profils Nexus OPT3.','Dessin du bandeau vérifié par ses appels, sans capture de la carte Indivis.','Sélection Nexus, monnaie, CapSkills et primes adaptés dans les fixtures seulement.','Aucune mesure FPS/TPS/MSPT ou test audio.']}
(R/'validation.json').write_text(json.dumps(validation,ensure_ascii=False,indent=2)+'\n')
(D/'SHA256SUMS.txt').write_text(''.join(v['sha256']+'  '+v['filename']+'\n' for v in b['files'].values()))
changes=read('files_changed.json');rows=['# PQ03 R1 HF1 — Changements exhaustifs','', 'Comparaison avec les archives initiales HF8/Fusion C1 et le JAR HF9. `hf1_diff_r1.json` isole les différences face à la livraison R1 du 7 novembre. Aucun fichier supprimé.','', '| Archive | Chemin | Opération | Motif |','|---|---|---|---|']
for x in changes:rows.append('| '+x['archive']+' | `'+x['path']+'` | '+x['operation']+' | '+x['reason'].replace('|','/')+' |')
(D/'CHANGEMENTS_FICHIERS.md').write_text('\n'.join(rows)+'\n')
docs={p.name:p.read_bytes() for p in D.iterdir() if p.is_file() and p.name not in {'PQ02_A_REPRENDRE.md','RAPPORT_PQ03_R1.md'}}
docs['historique/RAPPORT_PQ03_R1.md']=(D/'RAPPORT_PQ03_R1.md').read_bytes()
docs.update({n:(R/n).read_bytes() for n in ['build.json','static.json','rules.txt','native.txt','native_fixture.json','restart.txt','protocol_pq03.txt','protocol_pq03_fixture.json','protocol_pq02_with_pq03.txt','protocol_pq02_with_pq03_fixture.json','validation.json','files_changed.json','hf1_diff_r1.json']})
patch={build.FILES[k]:(O/build.FILES[k]).read_bytes() for k in ['core','mod']};patch.update({'documentation/'+n:v for n,v in docs.items()});patch['LIRE_EN_PREMIER.md']=(D/'RAPPORT_PQ03_R1_HF1.md').read_bytes();build.write(O/'PQ03_R1_HF1.zip',patch)
full={'serveur/mods/'+build.FILES['mod']:(O/build.FILES['mod']).read_bytes(),'clients/mods/'+build.FILES['mod']:(O/build.FILES['mod']).read_bytes()}
for k in ['core','quests']:full['serveur/datapacks/'+build.FILES[k]]=(O/build.FILES[k]).read_bytes()
full.update({'documentation/'+n:v for n,v in docs.items()});full['LIRE_EN_PREMIER.md']=(D/'INSTALLATION.md').read_bytes();build.write(O/'Indivis_PQ03_R1_HF1_Installation_Complete.zip',full)
for name in ['PQ03_R1_HF1.zip','Indivis_PQ03_R1_HF1_Installation_Complete.zip']:
 pack=build.read(O/name)
 for k,v in b['files'].items():
  matches=[data for path,data in pack.items() if path.endswith(v['filename'])]
  if name=='PQ03_R1_HF1.zip' and k=='quests':assert not matches;continue
  assert matches and all(hashlib.sha256(data).hexdigest()==v['sha256'] for data in matches)
packages={n:{'sha256':sha(O/n),'bytes':(O/n).stat().st_size} for n in ['PQ03_R1_HF1.zip','Indivis_PQ03_R1_HF1_Installation_Complete.zip']};(R/'packages.json').write_text(json.dumps(packages,indent=2)+'\n')
print(json.dumps({'status':'PASS','native':native_count,'restart':restart_count,'rules':rules_count,'protocol':protocol,'static':static['checks'],'packages':packages},indent=2))
